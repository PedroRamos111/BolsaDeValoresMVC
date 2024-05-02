package com.example.demo.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Services.BolsaService;

@Controller
public class BolsaController {
    @Autowired
    private BolsaService bolsaService;

    @PostMapping("/inicio")
    public ResponseEntity<String> inicio() throws InterruptedException {
        bolsaService.init();
        return ResponseEntity.ok("Início realizado com sucesso!");
    }

}