package com.example.demo.Controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Models.Broker;
import com.example.demo.Services.BrokerService;

@Controller
public class BrokerController {

    @Autowired
    private BrokerService brokerService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("broker", new Broker()); 
        return "login";
    }

    @PostMapping("/login")
    public String authenticate(@ModelAttribute("broker") Broker loginBroker, HttpSession session) {
        String nome = loginBroker.getName();
        String senha = loginBroker.getSenha();
        boolean isAuthenticated = brokerService.authenticate(nome, senha);
        if (isAuthenticated) {
            session.setAttribute("name", nome);
            return "redirect:/logado"; // Redireciona para a página de dashboard após o login
        } else {
            return "redirect:/login?error"; // Redireciona de volta para a página de login com um parâmetro de erro
        }
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

    @PostMapping("/compra")
    public void compra(@RequestParam String corretora, @RequestParam String ativo,
                       @RequestParam int quant, @RequestParam double val) {
        brokerService.compra(corretora, ativo, quant, val);
    }

    @PostMapping("/venda")
    public void venda(@RequestParam String corretora, @RequestParam String ativo,
                      @RequestParam int quant, @RequestParam double val) {
        brokerService.venda(corretora, ativo, quant, val);
    }
}
