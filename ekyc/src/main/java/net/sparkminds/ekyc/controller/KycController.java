package net.sparkminds.ekyc.controller;

import lombok.RequiredArgsConstructor;
import net.sparkminds.ekyc.service.KycService;
import net.sparkminds.ekyc.service.dto.enums.DocumentType;
import net.sparkminds.ekyc.service.dto.request.KycDocumentRequestDto;
import net.sparkminds.ekyc.service.dto.response.KycDocumentResponseDto;
import net.sparkminds.ekyc.service.dto.response.KycFaceResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/api/public/merchant/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping(value="/document")
    public ResponseEntity<?> verifyDocument(@RequestPart MultipartFile frontSideImage,
                                            @RequestPart MultipartFile backSideImage) {
        KycDocumentResponseDto responseDto = kycService.verifyDocument(frontSideImage, backSideImage, DocumentType.CITIZEN_ID_CARD);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping(value="/face")
    public ResponseEntity<?> verifyFace(@RequestBody MultipartFile file)
            throws IOException {
        KycFaceResponseDto kycDetailsResponseDto = kycService.verifyFace(file);
        return ResponseEntity.ok(kycDetailsResponseDto);
    }

}
