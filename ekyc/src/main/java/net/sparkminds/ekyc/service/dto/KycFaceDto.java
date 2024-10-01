package net.sparkminds.ekyc.service.dto;

import lombok.Data;
import net.sparkminds.ekyc.service.dto.enums.KycStatus;

import java.time.LocalDateTime;

@Data
public class KycFaceDto {
    private String frontFace;
    private KycStatus status;
    private LocalDateTime approvedAt;
}
