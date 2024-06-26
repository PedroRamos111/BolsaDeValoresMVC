package com.example.demo.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Models.Transacao;
import com.example.demo.Repositories.TransacaoRepository;
import com.example.demo.Services.BolsaService;

@Controller
public class BolsaController {
    @Autowired
    private BolsaService bolsaService;
    @Autowired
    private TransacaoRepository transacaoRepository;

    @PostMapping("/start")
    public ResponseEntity<String> start() throws InterruptedException {
        bolsaService.start();
        return ResponseEntity.ok("Início realizado com sucesso!");
    }

    @GetMapping("/transacoes")
    public String getTransacoes(Model model) {
        List<Transacao> transacoes = transacaoRepository.findAll();
        model.addAttribute("transacoes", transacoes);
        return "verTransacoes";
    }

}