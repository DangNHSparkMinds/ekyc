package net.sparkminds.ekyc.service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
}
