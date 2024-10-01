package net.sparkminds.ekyc.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.ekyc.service.dto.enums.KycStatus;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailsResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status;
    private KycStatus kycStatus;
    private String accountType;
    private String profilePicture;
    private String address;
}
