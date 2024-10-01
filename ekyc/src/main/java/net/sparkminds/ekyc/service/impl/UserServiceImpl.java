package net.sparkminds.ekyc.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import net.sparkminds.ekyc.entity.Kyc;
import net.sparkminds.ekyc.entity.User;
import net.sparkminds.ekyc.exception.BadRequestException;
import net.sparkminds.ekyc.exception.NotFoundException;
import net.sparkminds.ekyc.repository.KycRepository;
import net.sparkminds.ekyc.repository.UserRepository;
import net.sparkminds.ekyc.service.UserService;
import net.sparkminds.ekyc.service.dto.response.UserDetailsResponseDto;
import net.sparkminds.ekyc.utils.SecurityUtil;
import net.sparkminds.ekyc.utils.ValidateUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KycRepository kycRepository;

    @Override
    public UserDetailsResponseDto getUserDetails() {
        Long userId = SecurityUtil.getCurrentUser();
        if (ValidateUtils.isNullOrEmpty(userId)) {
            throw new BadRequestException("user.id.not.found");
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (ValidateUtils.isNullOrEmpty(userOptional)) {
            throw new NotFoundException("user.not.found");
        }
        Optional<Kyc> kycOptional = kycRepository.findByUserId(userId);
        if (ValidateUtils.isNullOrEmpty(kycOptional)) {
            throw new NotFoundException("kyc.not.found");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        User user = userOptional.get();
        Kyc kyc = kycOptional.get();
        UserDetailsResponseDto responseDTO = mapper.convertValue(user, UserDetailsResponseDto.class);
        responseDTO.setKycStatus(kyc.getStatus());
        return responseDTO;
    }
}
