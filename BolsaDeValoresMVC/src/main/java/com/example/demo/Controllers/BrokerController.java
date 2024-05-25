package com.example.demo.Controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Models.Broker;
import com.example.demo.Services.BrokerService;

@RestController
public class BrokerController {

    @Autowired
    private BrokerService brokerService;

    @GetMapping("/mensagem")
    public String exibirMensagem(Model model) {
        String mensagemFormatada = brokerService.getFormattedMessage();
        model.addAttribute("mensagem", mensagemFormatada);
        return "exibirMensagem"; // nome do arquivo HTML
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("broker", new Broker());
        return "login";
    }

    @PostMapping("/login")
    public String authenticate(@ModelAttribute("broker") Broker loginBroker, HttpSession session) {
        String username = loginBroker.getName();
        String password = loginBroker.getSenha();

        boolean userExists = brokerService.existsByUsername(username);

        if (userExists) {
            boolean isAuthenticated = brokerService.authenticate(username, password, session);
            if (isAuthenticated) {
                session.setAttribute("name", username);
                return "redirect:/logado";
            }
        }

        return "redirect:/login?error";
    }

    @GetMapping("/registro")
    public String register(Model model) {
        model.addAttribute("broker", new Broker());
        return "register";
    }

    @PostMapping("/register")
    public String registerBroker(@ModelAttribute("broker") Broker broker) {
        brokerService.saveBroker(broker);
        return "redirect:/login";
    }

    @GetMapping("/logado")
    public String logado(Model model) {
        model.addAttribute("broker", new Broker());
        return "logado";
    }

    @PostMapping("/compra")
    public ResponseEntity<String> compra(HttpSession session, @RequestParam String ativo,
            @RequestParam int quant, @RequestParam double val, @RequestParam String username) {
        brokerService.compra(ativo, quant, val, username);
        return ResponseEntity.ok("Transação de compra enviada com sucesso!");
    }

    @PostMapping("/venda")
    public ResponseEntity<String> venda(HttpSession session, @RequestParam String ativo,
            @RequestParam int quant, @RequestParam double val, @RequestParam String username) {
        brokerService.venda(ativo, quant, val, username);
        return ResponseEntity.ok("Transação de venda enviada com sucesso!");
    }

    @PostMapping("/acompanha")
    public ResponseEntity<String> acompanharAcao(HttpSession session, @RequestParam String acompanha, @RequestParam String username) {
        return ResponseEntity.ok(brokerService.acompanha(username, acompanha));
    }
    
    @GetMapping("/acompanha")
    public String acompanha(Model model) {
        model.addAttribute("broker", new Broker());
        return "acompanha";
    }


}
