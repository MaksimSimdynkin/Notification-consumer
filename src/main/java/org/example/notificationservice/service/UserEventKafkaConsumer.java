package org.example.notificationservice.service;

import org.example.notificationservice.dto.UserEventDto;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventKafkaConsumer {

    private final EmailService emailService;


    public UserEventKafkaConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-created")
    public void consumerUser(UserEventDto userEventDto) {
        emailService.sendUserCreatedEmail(userEventDto.getEmail());
    }

    @KafkaListener(topics = "user-deleted")
    public void consumerUserDeleted(UserEventDto userEventDto) {
        emailService.sendUserDeletedEmail(userEventDto.getEmail());
    }
}
