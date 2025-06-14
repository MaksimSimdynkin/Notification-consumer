package org.example.notificationservice.integration;

import org.example.notificationservice.config.EmailConfig;
import org.example.notificationservice.service.EmailService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@TestConfiguration
@Profile("test")
@EnableAutoConfiguration(exclude = {
    KafkaAutoConfiguration.class
})
public class TestConfig {

    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 3025;
    private static final String TEST_USERNAME = "test@example.com";
    private static final String TEST_PASSWORD = "test";

    @Bean
    @Primary
    public JavaMailSender testJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(TEST_HOST);
        mailSender.setPort(TEST_PORT);
        mailSender.setUsername(TEST_USERNAME);
        mailSender.setPassword(TEST_PASSWORD);
        mailSender.setProtocol("smtp");
        mailSender.setDefaultEncoding("UTF-8");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.debug", "true");
        props.put("mail.smtp.from", TEST_USERNAME);
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        props.put("mail.smtp.quitwait", "false");
        props.put("mail.smtp.sendpartial", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.SocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.localhost", "localhost");
        props.put("mail.smtp.localaddress", "127.0.0.1");

        return mailSender;
    }

    @Bean
    @Primary
    public EmailService testEmailService(JavaMailSender mailSender) {
        return new TestEmailService(mailSender);
    }

    @Bean
    @Profile("!test")
    public EmailConfig emailConfig() {
        return new EmailConfig();
    }
} 