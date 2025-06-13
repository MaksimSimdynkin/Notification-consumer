package org.example.notificationservice.service;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.user_service.dto.UserOperationMessage;


@Service
public class UserOperationKafkaConsumer {

    private EmailService  emailService;

    public UserOperationKafkaConsumer(EmailService emailService) {
        this.emailService = emailService;
    }


    @KafkaListener(topics = "user-operation-topic", groupId = "notification-group")
    public void listen(UserOperationMessage message) {
        String operation = message.getOperation();
        String email = message.getEmail();

        if("Create".equalsIgnoreCase(operation)) {
            emailService.sendEmail(email, "Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
        }else if("Delete".equalsIgnoreCase(operation)) {
            emailService.sendEmail(email, "Здравствуйте! Ваш аккаунт на сайте был успешно удален.");
        }
    }

}
