package net.sparkminds.ekyc.service.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterOtpCodeRequestDto {
    private String phoneNumber;
    private String email;
    private String otpCode;
}
