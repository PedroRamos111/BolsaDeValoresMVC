package com.example.demo.Controllers;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Services.BolsaService;


@Controller
public class BolsaController {
    @Autowired
    private BolsaService bolsaService;

    @PostMapping("/start")
    public ResponseEntity<String> start() throws InterruptedException {
        bolsaService.start();
        return ResponseEntity.ok("Início realizado com sucesso!");
    }


}