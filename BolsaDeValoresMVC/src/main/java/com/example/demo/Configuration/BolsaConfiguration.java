package com.example.demo.Configuration;

import java.util.UUID;

import javax.sound.midi.Receiver;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.demo.Services.BolsaService;

@Configuration
public class BolsaConfiguration {

    static final String topicExchangeName = "topic_logs";

    static final String queueName = "Broker";

    @Bean
    TopicExchange topicExchangeBolsa() {
        return new TopicExchange(topicExchangeName, false, false);
    }

    @Bean
    Queue queueBolsa() {
        return new Queue(queueName, false);
    }

    @Bean
    Binding vendaBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("compra.#");
    }

    @Bean
    Binding compaBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("venda.#");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(BolsaService bolsaService) {
        return new MessageListenerAdapter(bolsaService, "recebePedido");
    }
}
