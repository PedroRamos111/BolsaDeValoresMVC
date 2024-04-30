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

    public boolean authenticate(String nome, String senha) {
        Broker broker = brokerRepository.findByName(nome);
        System.out.println(nome);
        System.out.println(senha);
        System.out.println(broker.getName());
        System.out.println(broker.getSenha());


        if (broker != null && broker.getSenha().equals(senha)) {
            return true; 
        } else {
            return false; 
        }
    }
}
