package org.example.notificationservice.integration;

import org.example.notificationservice.controller.EmailController;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@TestConfiguration
@Profile("test")
public class TestWebConfig {

    @Bean
    @Primary
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();
        handlerMapping.setRemoveSemicolonContent(false);
        handlerMapping.setOrder(0);
        return handlerMapping;
    }

    @Bean
    @Profile("!test")
    public EmailController emailController() {
        return null;
    }
} 