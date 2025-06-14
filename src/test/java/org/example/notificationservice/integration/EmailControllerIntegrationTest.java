package org.example.notificationservice.integration;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.dto.EmailRequest;
import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.profiles.active=test",
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=test@example.com",
        "spring.mail.password=test",
        "spring.mail.properties.mail.smtp.auth=false",
        "spring.mail.properties.mail.smtp.starttls.enable=false",
        "spring.mail.properties.mail.smtp.ssl.enable=false",
        "spring.mail.properties.mail.smtp.from=test@example.com",
        "spring.kafka.bootstrap-servers=",
        "spring.kafka.producer.bootstrap-servers=",
        "spring.kafka.consumer.bootstrap-servers=",
        "spring.kafka.consumer.auto-offset-reset=none",
        "spring.kafka.consumer.enable-auto-commit=false",
        "spring.kafka.consumer.group-id=",
        "spring.kafka.producer.transaction-id-prefix=",
        "spring.kafka.producer.properties.transactional.id=",
        "spring.kafka.template.default-topic=",
        "spring.kafka.listener.missing-topics-fatal=false",
        "spring.kafka.listener.auto-startup=false"
    }
)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
    "logging.level.org.example.notificationservice=DEBUG",
    "logging.level.org.springframework.mail=DEBUG",
    "logging.level.com.icegreen.greenmail=DEBUG",
    "logging.level.jakarta.mail=DEBUG",
    "logging.level.org.springframework.web=DEBUG"
})
class EmailControllerIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
        .withPerMethodLifecycle(false); // Используем один экземпляр для всех тестов

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        log.info("Setting up test environment");
        log.info("GreenMail status before reset: {}", greenMail.getSmtp().getServerSetup());
        greenMail.reset();
        log.info("GreenMail reset completed");
        log.info("GreenMail status after reset: {}", greenMail.getSmtp().getServerSetup());
        log.info("Using email service: {}", emailService.getClass().getName());
        log.info("Test SMTP server is running on port: {}", greenMail.getSmtp().getPort());
        
        // Проверяем, что используется тестовый профиль
        log.info("Active profiles: {}", System.getProperty("spring.profiles.active"));
    }

    @Test
    void testSendEmail() throws MessagingException, IOException {
        log.info("Starting testSendEmail test");
        EmailRequest request = new EmailRequest();
        request.setTo("recipient@example.com");
        request.setSubject("Test Subject");
        request.setText("Test Message");

        log.info("Sending email request: {}", request);
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/email/send",
            request,
            String.class
        );

        log.info("Received response: status={}, body={}", response.getStatusCode(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected OK status but got: " + response.getStatusCode());
        assertEquals("Электронное письмо успешно отправлено", response.getBody(), "Unexpected response body: " + response.getBody());
        
        // Увеличиваем таймаут до 10 секунд
        boolean emailReceived = greenMail.waitForIncomingEmail(10000, 1);
        log.info("Email received: {}", emailReceived);
        assertTrue(emailReceived, "Email was not received within timeout");
        
        if (emailReceived) {
            MimeMessage[] messages = greenMail.getReceivedMessages();
            log.info("Received messages count: {}", messages.length);
            if (messages.length > 0) {
                MimeMessage message = messages[0];
                log.info("First message subject: {}", message.getSubject());
                log.info("First message recipients: {}", message.getAllRecipients()[0]);
                log.info("First message from: {}", message.getFrom()[0]);
                log.info("First message content type: {}", message.getContentType());
                log.info("First message content: {}", message.getContent());
            } else {
                log.error("No messages received despite emailReceived being true");
            }
        }
    }

    @Test
    void testSendEmailWithInvalidData() {
        log.info("Starting testSendEmailWithInvalidData test");
        EmailRequest request = new EmailRequest();
        // Не заполняем обязательные поля

        log.info("Sending invalid email request: {}", request);
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/email/send",
            request,
            String.class
        );

        log.info("Received response: status={}, body={}", response.getStatusCode(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected BAD_REQUEST status but got: " + response.getStatusCode());
        
        String responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        log.info("Response body: {}", responseBody);
        
        assertAll(
            () -> assertTrue(responseBody.contains("Поле 'to' не может быть пустым"), 
                "Response should contain 'to' validation message"),
            () -> assertTrue(responseBody.contains("Поле 'subject' не может быть пустым"), 
                "Response should contain 'subject' validation message"),
            () -> assertTrue(responseBody.contains("Поле 'text' не может быть пустым"), 
                "Response should contain 'text' validation message")
        );
    }
} 