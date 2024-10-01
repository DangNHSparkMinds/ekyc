package net.sparkminds.ekyc.service;

import net.sparkminds.ekyc.service.dto.enums.DocumentType;
import net.sparkminds.ekyc.service.dto.request.KycDocumentRequestDto;
import net.sparkminds.ekyc.service.dto.response.KycDocumentResponseDto;
import net.sparkminds.ekyc.service.dto.response.KycFaceResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface KycService {
    KycDocumentResponseDto verifyDocument(MultipartFile frontImage, MultipartFile backImage, DocumentType documentType);

    KycFaceResponseDto verifyFace(MultipartFile file);
}
