package com.example.demo.Controllers;

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
    public String login() {
        return "login";
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
}
