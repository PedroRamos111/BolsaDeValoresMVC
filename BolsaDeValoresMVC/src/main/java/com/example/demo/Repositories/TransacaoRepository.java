package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Models.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
}
