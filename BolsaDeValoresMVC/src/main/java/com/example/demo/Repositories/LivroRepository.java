package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Models.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}

