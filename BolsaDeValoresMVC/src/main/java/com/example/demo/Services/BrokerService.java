package com.example.demo.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Models.Broker;
import com.example.demo.Repositories.BrokerRepository;

@Service
public class BrokerService {
    @Autowired
    private BrokerRepository brokerRepository;

    public Broker findByName(String name) {
        return brokerRepository.findByName(name);
    }

    public Broker saveBroker(Broker broker) {
        return brokerRepository.save(broker);
    }
}