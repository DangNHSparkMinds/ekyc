package net.sparkminds.ekyc.service;

import net.sparkminds.ekyc.service.dto.request.LoginRequestDto;
import net.sparkminds.ekyc.service.dto.request.RegisterOtpCodeRequestDto;
import net.sparkminds.ekyc.service.dto.request.RegisterUserInfoRequestDto;
import net.sparkminds.ekyc.service.dto.request.RegisterPhoneNumberRequestDto;
import net.sparkminds.ekyc.service.dto.response.LoginResponseDto;
import net.sparkminds.ekyc.service.dto.response.RegisterOtpCodeResponseDto;
import net.sparkminds.ekyc.service.dto.response.RegisterResponseDto;

public interface AuthService {
    void registerWithPhoneNumber(RegisterPhoneNumberRequestDto registerRequestDTO);
    void registerWithEmail(String email);
    RegisterOtpCodeResponseDto verifyOTPCode(RegisterOtpCodeRequestDto registerRequestDTO);
    RegisterResponseDto registerWithPassword(String token, RegisterUserInfoRequestDto registerRequestDTO);
    LoginResponseDto login(LoginRequestDto loginRequestDto);
}
