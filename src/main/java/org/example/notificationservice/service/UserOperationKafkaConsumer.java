package org.example.notificationservice.service;


import org.example.notificationservice.dto.UserOperationMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


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

        if(operation.equalsIgnoreCase("create")) {
            emailService.sendEmail(email, "Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
        }else if(operation.equalsIgnoreCase("delete")) {
            emailService.sendEmail(email, "Здравствуйте! Ваш аккаунт на сайте был удалён.");
            



        }
    }

}
