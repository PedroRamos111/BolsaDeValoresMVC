package com.example.demo.Repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Models.Bolsa;
public interface BolsaRepository extends JpaRepository<Bolsa, Long> {
    Bolsa findByName(String name);
}