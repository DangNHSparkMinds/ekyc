package net.sparkminds.ekyc.service.dto.response;

import lombok.Data;
import net.sparkminds.ekyc.service.dto.enums.DocumentType;
import net.sparkminds.ekyc.service.dto.enums.KycStatus;

@Data
public class KycDocumentResponseDto {
    private DocumentType documentType;
    private String message;
    private KycStatus status;
    private Object metaData;
}
