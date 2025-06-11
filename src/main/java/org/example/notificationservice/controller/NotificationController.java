package org.example.notificationservice.controller;

import org.example.notificationservice.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/notification")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/created")
    public void sendUserCreatedNotification(@RequestParam String email) {
        emailService.sendUserCreatedEmail(email);
    }

    @PostMapping("/deleted")
    public void sendUserDeletedNotification(@RequestParam String email) {
        emailService.sendUserDeletedEmail(email);
    }
}
