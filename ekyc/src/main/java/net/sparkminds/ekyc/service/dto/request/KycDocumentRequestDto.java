package net.sparkminds.ekyc.service.dto.request;

import lombok.Data;
import net.sparkminds.ekyc.service.dto.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

@Data
public class KycDocumentRequestDto {
    private DocumentType documentType;
    private MultipartFile frontSideImage;
    private MultipartFile backSideImage;
}
