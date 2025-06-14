package org.example.notificationservice.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Primary
@Slf4j
public class TestEmailService extends org.example.notificationservice.service.EmailService {
    
    private static final String TEST_FROM_EMAIL = "test@example.com";
    
    public TestEmailService(JavaMailSender mailSender) {
        super(mailSender);
    }
    
    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(TEST_FROM_EMAIL);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            log.debug("Sending test email from {} to {} with subject: {}", TEST_FROM_EMAIL, to, subject);
            mailSender.send(message);
            log.info("Test email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send test email to: {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send test email: " + e.getMessage(), e);
        }
    }
} 