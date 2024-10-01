package net.sparkminds.ekyc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.ekyc.entity.converter.KycDocumentConverter;
import net.sparkminds.ekyc.entity.converter.KycFaceConverter;
import net.sparkminds.ekyc.service.dto.KycDocumentDto;
import net.sparkminds.ekyc.service.dto.KycFaceDto;
import net.sparkminds.ekyc.service.dto.enums.KycStatus;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kyc")
@Data
public class Kyc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private KycStatus status;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "kyc_face_data")
    @Convert(converter = KycFaceConverter.class)
    private KycFaceDto kycFaceDto;

    @Column(name = "kyc_document_data")
    @Convert(converter = KycDocumentConverter.class)
    private KycDocumentDto kycDocumentDto;
}
