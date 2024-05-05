package com.example.demo.Services;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

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