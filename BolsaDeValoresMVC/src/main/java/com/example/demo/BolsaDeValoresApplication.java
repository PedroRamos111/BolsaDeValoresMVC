package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BolsaDeValoresApplication {

	public static void main(String[] args) {

		Thread threadSpring = new Thread(new Runnable() {
			public void run() {

				SpringApplication.run(BolsaDeValoresApplication.class, args);
				System.out.println("aaaa");
			}
		});

		Thread threadBolsa = new Thread(new Runnable() {
			public void run() {
				try {
					Bolsa.inicio();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		threadSpring.start();
		threadBolsa.start();
	}

}
