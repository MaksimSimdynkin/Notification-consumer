package org.example.notificationservice.service;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.user_service.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserEventKafkaConsumer {

    private static final Logger logger = Logger.getLogger(String.valueOf(UserEventKafkaConsumer.class));

    @KafkaListener(topics = "user")
    public void consumerUser(ConsumerRecord<String, User> record) {
        logger.info("Received order: order={}, key={}, partition={}");

    }

}
