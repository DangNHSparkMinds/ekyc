package net.sparkminds.ekyc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.ekyc.service.dto.enums.OtpPurpose;
import net.sparkminds.ekyc.service.dto.enums.OtpVerifiedStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otp")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "otp_code", nullable = false)
    private String otpCode;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "otp_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OtpVerifiedStatus otpStatus;

    @Column(name = "otp_purpose", nullable = false)
    @Enumerated(EnumType.STRING)
    private OtpPurpose otpPurpose;
}
