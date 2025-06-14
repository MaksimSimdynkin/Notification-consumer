package org.example.notificationservice.integration;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.dto.EmailRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/email")
@Slf4j
@Primary
public class TestEmailController {
    private final TestEmailService emailService;
    
    public TestEmailController(TestEmailService emailService) {
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