package net.sparkminds.ekyc.repository;

import net.sparkminds.ekyc.entity.Otp;
import net.sparkminds.ekyc.service.dto.enums.OtpVerifiedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findTopByPhoneNumberOrEmailOrderByCreatedAtDesc(String phoneNumber, String email);

    List<Otp> findByPhoneNumberAndCreatedAtAfterAndOtpStatusNot(String phoneNumber, LocalDateTime createdAt, OtpVerifiedStatus status);

    List<Otp> findByEmailAndCreatedAtAfterAndOtpStatusNot(String phoneNumber, LocalDateTime createdAt, OtpVerifiedStatus status);
}

