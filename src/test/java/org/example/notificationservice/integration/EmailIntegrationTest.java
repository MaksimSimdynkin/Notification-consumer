package org.example.notificationservice.integration;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.example.notificationservice.dto.EmailRequest;
import org.example.notificationservice.model.UserOperationMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;

import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestKafkaConfig.class, TestEmailConfig.class, TestWebConfig.class})
@ActiveProfiles("test")
class EmailIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("test@mail.ru", "password")
                    .withDisabledAuthentication())
            .withPerMethodLifecycle(false);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Настройка почты для тестового окружения
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> greenMail.getSmtp().getPort());
        registry.add("spring.mail.username", () -> "test@mail.ru");
        registry.add("spring.mail.password", () -> "password");
        registry.add("spring.mail.properties.mail.smtp.auth", () -> "false");
        registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> "false");
        registry.add("spring.mail.properties.mail.smtp.ssl.enable", () -> "false");
        registry.add("spring.mail.properties.mail.smtp.from", () -> "test@mail.ru");
        registry.add("spring.mail.properties.mail.transport.protocol", () -> "smtp");
        registry.add("spring.mail.properties.mail.debug", () -> "true");
        
        // Настройка Kafka
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.consumer.group-id", () -> "test-group");
        registry.add("spring.kafka.consumer.key-deserializer", () -> "org.apache.kafka.common.serialization.StringDeserializer");
        registry.add("spring.kafka.consumer.value-deserializer", () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
        registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages", () -> "org.example.*");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, UserOperationMessage> kafkaTemplate;

    @Autowired
    private TestEmailService testEmailService;

    @Autowired
    private TestKafkaConsumerService testKafkaConsumerService;

    @Test
    void testSendEmailViaRestApi() throws Exception {
        // Очищаем предыдущие сообщения
        try {
            greenMail.purgeEmailFromAllMailboxes();
        } catch (Exception e) {
            // Игнорируем ошибки очистки, так как это не критично для теста
        }
        
        // Подготовка данных
        EmailRequest request = new EmailRequest(
                "recipient@example.com",
                "Тестовое письмо",
                "Это тестовое сообщение"
        );

        // Отправка запроса
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/test/email/send",
                request,
                String.class
        );

        // Проверка ответа
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("успешно отправлено");

        // Проверка получения письма
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertThat(receivedMessages).hasSize(1);
            
            MimeMessage message = receivedMessages[0];
            assertThat(message.getSubject()).isEqualTo("Тестовое письмо");
            assertThat(message.getFrom()[0].toString()).isEqualTo("test@mail.ru");
            assertThat(message.getAllRecipients()[0].toString()).isEqualTo("recipient@example.com");
            assertThat(message.getContent().toString()).contains("Это тестовое сообщение");
        });
    }

    @Test
    void testSendEmailViaKafka() throws Exception {
        // Очищаем предыдущие сообщения
        try {
            greenMail.purgeEmailFromAllMailboxes();
        } catch (Exception e) {
            // Игнорируем ошибки очистки, так как это не критично для теста
        }
        
        // Подготовка сообщения
        UserOperationMessage message = new UserOperationMessage(
                "CREATE",
                "user@example.com"
        );

        // Отправка сообщения в Kafka
        kafkaTemplate.send("user-operation-topic", message);

        // Проверка получения письма
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertThat(receivedMessages).hasSize(1);
            
            MimeMessage emailMessage = receivedMessages[0];
            assertThat(emailMessage.getSubject()).isEqualTo("Добро пожаловать!");
            assertThat(emailMessage.getContent().toString())
                    .contains("Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
            assertThat(emailMessage.getFrom()[0].toString()).isEqualTo("test@mail.ru");
            assertThat(emailMessage.getAllRecipients()[0].toString()).isEqualTo("user@example.com");
        });
    }
} 