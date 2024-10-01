package net.sparkminds.ekyc.service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterOtpCodeResponseDto {
    private String token;
}
