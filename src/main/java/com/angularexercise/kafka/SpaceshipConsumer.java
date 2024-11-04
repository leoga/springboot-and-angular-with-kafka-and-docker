package com.angularexercise.kafka;

import com.angularexercise.model.Spaceship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SpaceshipConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SpaceshipConsumer.class);

    @KafkaListener(topics = "spaceship-topic", groupId = "spaceship-group")
    public void consume(Spaceship spaceship) {
        logger.info("Received Spaceship message: {}", spaceship);
        // Add your business logic here to process the received spaceship message
    }
}