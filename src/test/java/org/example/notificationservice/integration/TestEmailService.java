package org.example.notificationservice.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Primary
@Slf4j
public class TestEmailService extends org.example.notificationservice.service.EmailService {
    
    private static final String TEST_FROM_EMAIL = "test@example.com";
    
    @Value("${spring.mail.username:test@example.com}")
    private String fromEmail;
    
    public TestEmailService(JavaMailSender mailSender) {
        super(mailSender);
        log.info("TestEmailService initialized with fromEmail: {}", fromEmail);
    }
    
    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            log.info("Attempting to send test email from {} to {} with subject: {}", fromEmail, to, subject);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            log.debug("Created test mail message: {}", message);
            mailSender.send(message);
            log.info("Test email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send test email to: {}. Error type: {}, Error message: {}", 
                     to, e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("Failed to send test email: " + e.getMessage(), e);
        }
    }
} 