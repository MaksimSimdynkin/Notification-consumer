package org.example.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailService {
    protected final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        log.info("EmailService инициализирован с mailSender типа: {}", mailSender.getClass().getName());
        log.info("Email отправителя настроен как: {}", fromEmail);
    }
    
    public void sendEmail(String to, String subject, String text) {
        log.info("Попытка отправки email получателю: {}, тема: {}, от: {}", to, subject, fromEmail);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            log.debug("Создано сообщение: {}", message);
            log.info("Отправка сообщения через SMTP сервер...");
            mailSender.send(message);
            log.info("Email успешно отправлен получателю: {}", to);
        } catch (Exception e) {
            log.error("Ошибка при отправке email получателю: {}. Тип ошибки: {}, Сообщение: {}", 
                     to, e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить email: " + e.getMessage(), e);
        }
    }
}
