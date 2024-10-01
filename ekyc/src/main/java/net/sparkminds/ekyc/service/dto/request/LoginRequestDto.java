package net.sparkminds.ekyc.service.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestDto {
    private String email;
    private String phoneNumber;
    private String password;
}
