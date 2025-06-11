package org.example.notificationservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String emailFrom;
    private final String subjectCreated;
    private final String subjectDeleted;
    private final String textCreated;
    private final String textDeleted;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String emailFrom,
                        @Value("${notification.email.subject.created}") String subjectCreated,
                        @Value("${notification.email.subject.deleted}") String subjectDeleted,
                        @Value("${notification.email.text.created}") String textCreated,
                        @Value("${notification.email.text.deleted}") String textDeleted) {
        this.mailSender = mailSender;
        this.emailFrom = emailFrom;
        this.subjectCreated = subjectCreated;
        this.subjectDeleted = subjectDeleted;
        this.textCreated = textCreated;
        this.textDeleted = textDeleted;
    }

    public void sendUserCreatedEmail(String toEmail) {
        sendEmail(toEmail, subjectCreated, textCreated);
    }

    public void sendUserDeletedEmail(String toEmail) {
        sendEmail(toEmail, subjectDeleted, textDeleted);
    }

    private void sendEmail(String toEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}