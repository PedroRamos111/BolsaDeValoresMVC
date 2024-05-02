package com.example.demo.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.demo.Services.BolsaService;

@Configuration
public class BolsaConfiguration {

    @Bean
    BolsaService bolsaS() {
        return new BolsaService();
    }

     @Bean
    Queue queueB() {
        return new Queue("Broker", true);
    }

}
