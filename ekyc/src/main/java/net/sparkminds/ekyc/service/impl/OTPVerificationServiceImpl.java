package net.sparkminds.ekyc.service.impl;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import net.sparkminds.ekyc.entity.Otp;
import net.sparkminds.ekyc.exception.BadRequestException;
import net.sparkminds.ekyc.repository.OtpVerificationRepository;
import net.sparkminds.ekyc.service.OTPVerificationService;
import net.sparkminds.ekyc.service.dto.enums.OtpPurpose;
import net.sparkminds.ekyc.service.dto.enums.OtpVerifiedStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPVerificationServiceImpl implements OTPVerificationService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.phone_number}")
    private String fromNumber;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    private final OtpVerificationRepository otpVerificationRepository;
    private final Random random = new Random();

    @Override
    public void sendOTPCode(String phoneNumber, OtpPurpose otpPurpose) {
        String otpCode = generateOTPCode(phoneNumber, null, otpPurpose);
        Twilio.init(accountSid, authToken);
        String messageBody = "Your OTP code is: " + otpCode;
        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(fromNumber),
                messageBody
        ).create();
    }

    @Override
    public void sendOTPCodeToMail(String toEmail, OtpPurpose otpPurpose) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Otp> otpVerifications = otpVerificationRepository
                .findByEmailAndCreatedAtAfterAndOtpStatusNot(
                        toEmail,
                        oneHourAgo,
                        OtpVerifiedStatus.SUCCESS
                );

        if (otpVerifications.size() >= 5) {
            throw new BadRequestException("Too many failed OTP attempts. Please try again later.");
        }

        Optional<Otp> lastOtp = otpVerificationRepository
                .findTopByPhoneNumberOrEmailOrderByCreatedAtDesc(null, toEmail);

        if (lastOtp.isPresent()) {
            LocalDateTime otpCreatedAt = lastOtp.get().getCreatedAt();
            if (otpCreatedAt.isAfter(LocalDateTime.now().minusSeconds(30))) {
                throw new BadRequestException("You can only request an OTP every 30 seconds.");
            }
        }

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Ewallet - Verify your Email");
            helper.setFrom(fromEmail);

            String otpCode = generateOTPCode(null, toEmail, otpPurpose);

            Context context = new Context();
            context.setVariable("otpCode", otpCode);

            String htmlContent = templateEngine.process("otp-email-template", context);
            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(message);
    }

    @Override
    public void verifyOTPCode(String phoneNumber, String email, String otpCode, Integer maxRetry) {
        Optional<Otp> otpEntityOptional = otpVerificationRepository
                .findTopByPhoneNumberOrEmailOrderByCreatedAtDesc(phoneNumber, email);
        if (otpEntityOptional.isEmpty()) {
            throw new BadRequestException("OTP is incorrect. Please try again.");
        }

        Otp otpVerification = otpEntityOptional.get();

        if (otpVerification.getOtpStatus() == OtpVerifiedStatus.SUCCESS) {
            throw new BadRequestException("OTP has already been verified.");
        }

        int retryCount = otpVerification.getRetryCount();
        if (retryCount >= maxRetry) {
            throw new BadRequestException("Too many failed attempts. Please request a new OTP.");
        }

        if (otpVerification.getOtpCode().equals(otpCode)
                && otpVerification.getExpiresAt().isAfter(LocalDateTime.now())
                && !OtpVerifiedStatus.SUCCESS.equals(otpVerification.getOtpStatus())) {
            otpVerification.setOtpStatus(OtpVerifiedStatus.SUCCESS);
            otpVerificationRepository.save(otpVerification);
        } else {
            otpVerification.setRetryCount(otpVerification.getRetryCount() + 1);
            otpVerification.setOtpStatus(OtpVerifiedStatus.FAILED);
            otpVerificationRepository.save(otpVerification);
            throw new BadRequestException("OTP not correct");
        }

    }

    private String generateOTPCode(String phoneNumber, String email, OtpPurpose otpPurpose) {
        String otpCode = String.format("%06d", random.nextInt(999999));
        Otp otpVerification = Otp.builder()
                .phoneNumber(phoneNumber)
                .email(email)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .createdAt(LocalDateTime.now())
                .otpStatus(OtpVerifiedStatus.PENDING)
                .otpPurpose(otpPurpose)
                .build();
        otpVerificationRepository.save(otpVerification);
        return otpCode;
    }
}
