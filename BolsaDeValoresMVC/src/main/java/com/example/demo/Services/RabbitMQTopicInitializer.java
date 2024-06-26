package com.example.demo.Services;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.TopicExchange;

@Service
public class RabbitMQTopicInitializer {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    public void initializeTopics(String[] topics) {
        for (String topicName : topics) {
            TopicExchange topicExchange = new TopicExchange(topicName);
            rabbitAdmin.declareExchange(topicExchange);
        }
    }

}