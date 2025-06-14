package org.example.notificationservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.dto.EmailRequest;
import org.example.notificationservice.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@Slf4j
public class EmailController {
    private final EmailService emailService;
    
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(request.getTo(), request.getSubject(), request.getText());
            return ResponseEntity.ok("Электронное письмо успешно отправлено");
        } catch (Exception e) {
            log.error("Не удалось отправить электронное письмо", e);
            return ResponseEntity.internalServerError()
                    .body("Не удалось отправить электронное письмо: " + e.getMessage());
        }
    }
}
