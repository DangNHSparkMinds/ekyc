package net.sparkminds.ekyc.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.ekyc.entity.Kyc;
import net.sparkminds.ekyc.entity.Otp;
import net.sparkminds.ekyc.entity.Role;
import net.sparkminds.ekyc.entity.User;
import net.sparkminds.ekyc.entity.UserSession;
import net.sparkminds.ekyc.exception.BadRequestException;
import net.sparkminds.ekyc.exception.DuplicateKeyException;
import net.sparkminds.ekyc.exception.NotFoundException;
import net.sparkminds.ekyc.repository.KycRepository;
import net.sparkminds.ekyc.repository.OtpVerificationRepository;
import net.sparkminds.ekyc.repository.RoleRepository;
import net.sparkminds.ekyc.repository.UserRepository;
import net.sparkminds.ekyc.repository.UserSessionRepository;
import net.sparkminds.ekyc.service.AuthService;
import net.sparkminds.ekyc.service.OTPVerificationService;
import net.sparkminds.ekyc.service.dto.enums.KycStatus;
import net.sparkminds.ekyc.service.dto.enums.OtpPurpose;
import net.sparkminds.ekyc.service.dto.enums.OtpVerifiedStatus;
import net.sparkminds.ekyc.service.dto.enums.RoleName;
import net.sparkminds.ekyc.service.dto.request.LoginRequestDto;
import net.sparkminds.ekyc.service.dto.request.RegisterOtpCodeRequestDto;
import net.sparkminds.ekyc.service.dto.request.RegisterUserInfoRequestDto;
import net.sparkminds.ekyc.service.dto.request.RegisterPhoneNumberRequestDto;
import net.sparkminds.ekyc.service.dto.response.LoginResponseDto;
import net.sparkminds.ekyc.service.dto.response.RegisterOtpCodeResponseDto;
import net.sparkminds.ekyc.service.dto.response.RegisterResponseDto;
import net.sparkminds.ekyc.service.jwt.JwtUtils;
import net.sparkminds.ekyc.utils.ValidateUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Integer MAX_RETRY = 3;

    private static final Integer LIMIT_NUMBER_OF_OTP = 5;

    private static final Integer HOUR_LIMIT = 1;

    private static final Integer OTP_RESEND_INTERVAL = 30;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final UserSessionRepository userSessionRepository;

    private final KycRepository kycRepository;

    private final OtpVerificationRepository otpVerificationRepository;

    private final AuthenticationManager authenticationManager;

    private final OTPVerificationService otpVerificationService;


    @Override
    public void registerWithPhoneNumber(RegisterPhoneNumberRequestDto registerRequestDTO) {
        // Check the number of OTPs that have not been successful within 1 hour
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(HOUR_LIMIT);
        List<Otp> otpVerifications = otpVerificationRepository
                .findByPhoneNumberAndCreatedAtAfterAndOtpStatusNot(
                        registerRequestDTO.getPhoneNumber(),
                        oneHourAgo,
                        OtpVerifiedStatus.SUCCESS
                );

        //Checking OTP number failed
        if (otpVerifications.size() >= LIMIT_NUMBER_OF_OTP) {
            throw new BadRequestException("Too many failed OTP attempts. Please try again later.");
        }

        // Check the last OTP sending time
        Optional<Otp> lastOtp = otpVerificationRepository
                .findTopByPhoneNumberOrEmailOrderByCreatedAtDesc(registerRequestDTO.getPhoneNumber(), null);

        // If there has been an OTP sent recently, check the time
        if (lastOtp.isPresent()) {
            LocalDateTime otpCreatedAt = lastOtp.get().getCreatedAt();
            if (otpCreatedAt.isAfter(LocalDateTime.now().minusSeconds(OTP_RESEND_INTERVAL))) {
                throw new IllegalStateException("You can only request an OTP every 30 seconds.");
            }
        }

        // Send OTP
        otpVerificationService.sendOTPCode(registerRequestDTO.getPhoneNumber(), OtpPurpose.REGISTER);
    }

    @Override
    public void registerWithEmail(String email) {
        Optional<User> userOptional = userRepository.findByPhoneNumberOrEmail(null, email);
        if (userOptional.isPresent()) {
            throw new BadRequestException("Email address existed");
        }
        otpVerificationService.sendOTPCodeToMail(email, OtpPurpose.REGISTER);
    }

    @Override
    public RegisterOtpCodeResponseDto verifyOTPCode(RegisterOtpCodeRequestDto registerRequestDTO) {
        otpVerificationService.verifyOTPCode(registerRequestDTO.getPhoneNumber(),
                registerRequestDTO.getEmail(), registerRequestDTO.getOtpCode(), MAX_RETRY);

        String token = JwtUtils.generateRegisterToken(registerRequestDTO.getPhoneNumber(),
                registerRequestDTO.getEmail(), "REGISTER_PASSWORD");
        RegisterOtpCodeResponseDto response = new RegisterOtpCodeResponseDto();
        response.setToken(token);
        return response;
    }

    @Override
    @Transactional
    public RegisterResponseDto registerWithPassword(String token, RegisterUserInfoRequestDto registerRequestDTO) {
        if (!StringUtils.hasText(token) && !token.startsWith("Bearer ") && !JwtUtils.validateJwtToken(token)) {
            throw new BadRequestException("token.invalid");
        }
        String requestToken = token.substring(7);
        String phoneNumber = JwtUtils.getClaimFromJwtToken(requestToken, "phoneNumber");
        String email = JwtUtils.getClaimFromJwtToken(requestToken, "email");
        String scope = JwtUtils.getClaimFromJwtToken(requestToken, "scope");
        if (!Objects.equals("REGISTER_PASSWORD", scope)) {
            throw new BadRequestException("token.invalid");
        }

        if (userRepository.existsByPhoneNumberOrEmail(phoneNumber, email)) {
            throw new DuplicateKeyException("user.existed");
        }

        Optional<Role> roleOptional = roleRepository.findByRoleName(RoleName.MERCHANT);
        if(ValidateUtils.isNullOrEmpty(roleOptional)){
            throw new NotFoundException("role.not.found");
        }

        String passwordEncoded = passwordEncoder.encode(registerRequestDTO.getPassword());
        User user = User.builder()
                .phoneNumber(phoneNumber)
                .email(email)
                .role(roleOptional.get())
                .fullName(registerRequestDTO.getFullName())
                .password(passwordEncoded)
                .status(KycStatus.PENDING.toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        User newUser = userRepository.save(user);

        String emailOrPhoneNumber = email == null ? phoneNumber : email;
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailOrPhoneNumber, registerRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = JwtUtils.generateAccessToken(authentication);
        String refreshToken = JwtUtils.generateRefreshToken(authentication);

        UserSession userSession = new UserSession();
        userSession.setToken(accessToken);
        userSession.setRefreshToken(refreshToken);
        userSession.setUserId(newUser.getId());
        userSession.setCreatedAt(LocalDateTime.now());
        userSession.setExpiresAt(JwtUtils.getExpireTimeFromToken(accessToken));
        userSessionRepository.save(userSession);

        Kyc kyc = new Kyc();
        kyc.setUserId(newUser.getId());
        kyc.setStatus(KycStatus.PENDING);
        kycRepository.save(kyc);

        RegisterResponseDto registerResponseDTO = new RegisterResponseDto();
        registerResponseDTO.setAccessToken(accessToken);
        registerResponseDTO.setRefreshToken(refreshToken);
        return registerResponseDTO;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Optional<User> userOptional = userRepository.findByPhoneNumberOrEmail(loginRequestDto.getPhoneNumber(), loginRequestDto.getEmail());
        if (userOptional.isEmpty()) {
            throw new NotFoundException("user.not.found");
        }

        String accessToken = JwtUtils.generateAccessToken(authentication);
        String refreshToken = JwtUtils.generateRefreshToken(authentication);

        UserSession userSession = new UserSession();
        userSession.setToken(accessToken);
        userSession.setRefreshToken(refreshToken);
        userSession.setUserId(userOptional.get().getId());
        userSession.setCreatedAt(LocalDateTime.now());
        userSession.setExpiresAt(JwtUtils.getExpireTimeFromToken(accessToken));
        userSessionRepository.save(userSession);

        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setAccessToken(accessToken);
        responseDto.setRefreshToken(refreshToken);
        return responseDto;
    }
}
