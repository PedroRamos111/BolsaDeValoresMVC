package com.example.demo.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Models.Broker;
import com.example.demo.Repositories.BrokerRepository;
import com.example.demo.Services.BrokerService;

@RestController
public class BrokerController {

    @Autowired
    private BrokerService brokerService;

    @GetMapping("/register")
    public ModelAndView exibirFormularioCadastro() {
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public Broker registerBroker(@RequestParam String name, @RequestParam String senha) {
        Broker broker = new Broker();
        broker.setName(name);
        broker.setSenha(senha);
        return brokerService.saveBroker(broker);
    }

    @GetMapping("/login")
    public ModelAndView exibirFormularioLogin() {
        return new ModelAndView("login");
    }

    @PostMapping("/login")
    public String realizarLogin(@RequestParam String email, @RequestParam String senha) {
        // Aqui você iria verificar no banco de dados se as credenciais são válidas
        // Por simplicidade, vamos apenas redirecionar para uma página de sucesso
        return "redirect:/logado";
    }

    @GetMapping("/logado")
    public String exibirPaginaLogado() {
        return "logado";
    }
}