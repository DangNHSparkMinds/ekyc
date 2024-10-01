package net.sparkminds.ekyc.service.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserInfoRequestDto {
    private String fullName;
    private String password;
}
