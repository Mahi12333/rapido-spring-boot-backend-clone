package com.maven.Rapido.utils;

import com.maven.Rapido.exception.APIException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private WebClient webClient;

    @Value("${spring.mail.properties.domain_name}") // Domain name from application properties
    private String domainName;

    @Value("${otp.api-key}")
    private String apiKey;

    @Value("${otp.template}")
    private String otpTemplate;

    @Async
    public void sendEmail(String to, String Subject, String htmlContent, String from) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(Subject);
            //log.info("resetUrl-----{}", htmlContent);
            helper.setText(htmlContent, true);
            helper.setFrom(from != null ? from : domainName); // Use provided sender or default domain
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email----{}", e.getMessage());
            throw new APIException("Failed to send email");
        }
    }

    @Async
    public void sendOtpByPhone(int otp, String phoneNumber, Date expiry) {
        log.info("Sending OTP {} to phone number {} (expires at {})", otp, phoneNumber, expiry);

        String url = String.format(
                "https://2factor.in/API/V1/%s/SMS/%s/%d/%s",
                apiKey, phoneNumber, otp, otpTemplate
        );

        webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("OTP sent successfully: {}", response))
                .doOnError(error -> log.error("Failed to send OTP: {}", error.getMessage()))
                .subscribe();
    }

}
