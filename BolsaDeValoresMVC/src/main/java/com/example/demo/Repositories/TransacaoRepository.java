package com.example.demo.Repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.Models.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByComprador(String comprador);
    List<Transacao> findByVendedor(String vendedor);
}
