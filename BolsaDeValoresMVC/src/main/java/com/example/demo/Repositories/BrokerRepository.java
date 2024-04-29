package com.example.demo.Repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Models.Broker;

public interface BrokerRepository extends JpaRepository<Broker, Long> {
    Broker findByName(String name);
}