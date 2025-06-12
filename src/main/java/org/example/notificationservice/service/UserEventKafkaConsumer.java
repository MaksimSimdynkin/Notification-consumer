package org.example.notificationservice.service;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.notificationservice.entiti.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserEventKafkaConsumer {

    private final Logger logger = Logger.getLogger(UserEventKafkaConsumer.class.getName());

    @KafkaListener(topics = "user")
    public void consumerUser(ConsumerRecord<String, User> record) {
        logger.info(
                "Received order: order={}, key={}, partition={}"
        );

    }

}
