package org.example.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.model.UserOperationMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {
    private final EmailService emailService;
    
    public KafkaConsumerService(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @KafkaListener(topics = "user-operation-topic", groupId = "notification-service")
    public void consume(UserOperationMessage message) {
        log.info("Received message: {}", message);
        
        String subject;
        String text;
        
        switch (message.getOperation()) {
            case "CREATE":
                subject = "Добро пожаловать!";
                text = "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
                break;
            case "DELETE":
                subject = "Аккаунт удален";
                text = "Здравствуйте! Ваш аккаунт был удалён.";
                break;
            default:
                log.warn("Unknown operation: {}", message.getOperation());
                return;
        }
        
        emailService.sendEmail(message.getEmail(), subject, text);
    }
}
