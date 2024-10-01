package net.sparkminds.ekyc.service;

import net.sparkminds.ekyc.service.dto.enums.OtpPurpose;

public interface OTPVerificationService {
    void sendOTPCode(String phoneNumber, OtpPurpose otpPurpose);

    void sendOTPCodeToMail(String email, OtpPurpose otpPurpose);

    void verifyOTPCode(String phoneNumber, String email, String otpCode, Integer maxRetry);
}
