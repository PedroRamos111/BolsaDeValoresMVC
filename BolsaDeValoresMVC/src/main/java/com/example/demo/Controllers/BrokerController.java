package com.example.demo.Controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Models.Broker;
import com.example.demo.Services.BrokerService;

@RestController
public class BrokerController {

    @Autowired
    private BrokerService brokerService;

    @GetMapping("/mensagem")
    public ModelAndView exibirMensagem() {
        ModelAndView mav = new ModelAndView("exibirMensagem");
        String mensagemFormatada = brokerService.getFormattedMessage();
        mav.addObject("mensagem", mensagemFormatada);
        return mav;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("broker", new Broker());
        return mav;
    }

    @PostMapping("/login")
    public ModelAndView authenticate(@ModelAttribute("broker") Broker loginBroker, HttpSession session) {
        String username = loginBroker.getName();
        String password = loginBroker.getSenha();

        boolean userExists = brokerService.existsByUsername(username);

        if (userExists) {
            boolean isAuthenticated = brokerService.authenticate(username, password, session);
            if (isAuthenticated) {
                session.setAttribute("name", username);
                return new ModelAndView("redirect:/logado");
            }
        }

        return new ModelAndView("redirect:/login?error");
    }

    @GetMapping("/registro")
    public ModelAndView register() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("broker", new Broker());
        return mav;
    }

    @PostMapping("/register")
    public ModelAndView registerBroker(@ModelAttribute("broker") Broker broker) {
        brokerService.saveBroker(broker);
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/logado")
    public ModelAndView logado() {
        ModelAndView mav = new ModelAndView("logado");
        mav.addObject("broker", new Broker());
        return mav;
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
    public ResponseEntity<String> acompanharAcao(HttpSession session, @RequestParam String acompanha,
            @RequestParam String username) {
        return ResponseEntity.ok(brokerService.acompanha(username, acompanha));
    }

    @GetMapping("/acompanha")
    public ModelAndView acompanha() {
        ModelAndView mav = new ModelAndView("acompanha");
        mav.addObject("broker", new Broker());
        return mav;
    }

    @GetMapping("/pedidos")
    public ModelAndView verPedidos() {
        ModelAndView mav = new ModelAndView("verPedidos");
        mav.addObject("compras", brokerService.getTodasCompras());
        mav.addObject("vendas", brokerService.getTodasVendas());
        return mav;
    }
    
    @GetMapping("/transacoes")
    public ModelAndView verTransacoes() {
        ModelAndView mav = new ModelAndView("verTransacoes");
        mav.addObject("transacoes", brokerService.getTransacoes());
        return mav;
    }

}
