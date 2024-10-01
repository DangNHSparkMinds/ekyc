package net.sparkminds.ekyc.service.dto;

import lombok.Data;
import net.sparkminds.ekyc.service.dto.enums.KycStatus;

import java.time.LocalDateTime;

@Data
public class KycDocumentDto {
    private String country;
    private String type;
    private Object metaData;
    private KycStatus stastus;
    private LocalDateTime approvedAt;
}
