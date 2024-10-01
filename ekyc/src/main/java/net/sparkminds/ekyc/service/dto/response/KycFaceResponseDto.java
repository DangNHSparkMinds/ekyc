package net.sparkminds.ekyc.service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.ekyc.service.dto.enums.KycStatus;

@Data
@NoArgsConstructor
public class KycFaceResponseDto {
    private String message;
    private KycStatus status;
    private Object metaData;
}
