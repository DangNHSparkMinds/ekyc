package net.sparkminds.ekyc.controller;

import lombok.RequiredArgsConstructor;
import net.sparkminds.ekyc.service.AuthService;
import net.sparkminds.ekyc.service.KycService;
import net.sparkminds.ekyc.service.dto.request.LoginRequestDto;
import net.sparkminds.ekyc.service.dto.request.RegisterEmailRequestDto;
import net.sparkminds.ekyc.service.dto.request.RegisterOtpCodeRequestDto;
import net.sparkminds.ekyc.service.dto.request.RegisterUserInfoRequestDto;
import net.sparkminds.ekyc.service.dto.request.RegisterPhoneNumberRequestDto;
import net.sparkminds.ekyc.service.dto.response.LoginResponseDto;
import net.sparkminds.ekyc.service.dto.response.RegisterOtpCodeResponseDto;
import net.sparkminds.ekyc.service.dto.response.RegisterResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final KycService kycVerificationService;

    @PostMapping("/register/phone-number")
    public ResponseEntity<?> registerWithPhoneNumber(@RequestBody RegisterPhoneNumberRequestDto registerRequestDTO) {
        authService.registerWithPhoneNumber(registerRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register/email")
    public ResponseEntity<?> registerWithEmail(@RequestBody RegisterEmailRequestDto registerRequestDTO) {
        authService.registerWithEmail(registerRequestDTO.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register/otp-code")
    public ResponseEntity<?> registerWithOTPCode(@RequestBody RegisterOtpCodeRequestDto registerRequestDTO) {
        RegisterOtpCodeResponseDto registerOTPCodeResponseDTO = authService.verifyOTPCode(registerRequestDTO);
        return ResponseEntity.ok(registerOTPCodeResponseDTO);
    }

    @PostMapping("/register/password")
    public ResponseEntity<RegisterResponseDto> registerWithUserInfo(@RequestHeader("X-Registration-Token") String token,
                                                                    @RequestBody RegisterUserInfoRequestDto registerRequestDTO) {
        RegisterResponseDto registerResponseDTO = authService.registerWithPassword(token, registerRequestDTO);
        return ResponseEntity.ok(registerResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(loginResponseDto);
    }
}
