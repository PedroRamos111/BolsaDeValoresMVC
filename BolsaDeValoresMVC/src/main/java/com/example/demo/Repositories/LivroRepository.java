package com.example.demo.Repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.Models.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByComprador(String comprador);
    List<Livro> findByAtividade(String atividade);
}
