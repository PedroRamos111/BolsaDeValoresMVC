package com.example.demo.Services;

import javax.servlet.http.HttpSession;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Models.Broker;
import com.example.demo.Repositories.BrokerRepository;

@Service
public class BrokerService {
    @Autowired
    private BrokerRepository brokerRepository;

    public Broker findByName(String name) {
        return brokerRepository.findByName(name);
    }

    public Broker saveBroker(Broker broker) {
        return brokerRepository.save(broker);
    }

    public boolean existsByUsername(String username) {
        Broker broker = brokerRepository.findByName(username);
        return broker != null;
    }

    public boolean authenticate(String nome, String senha, HttpSession session) {
        Broker broker = brokerRepository.findByName(nome);
        if (broker != null && broker.getSenha().equals(senha)) {
            session.setAttribute("username", nome);
            return true;
        } else {
            return false;
        }
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void compra(String ativo, int quant, double val, HttpSession session) {
        String corretora = (String) session.getAttribute("username");
        String topic = "compra." + ativo;
        String message = quant + ";" + val + ";" + corretora;
        enviaPedido(topic, message);
    }

    public void venda(String ativo, int quant, double val, HttpSession session) {
        String corretora = (String) session.getAttribute("username");
        String topic = "venda." + ativo;
        String message = quant + ";" + val + ";" + corretora;
        enviaPedido(topic, message);
    }

    private void enviaPedido(String topic, String message) {
        rabbitTemplate.convertAndSend("topic_logs", topic, message);
        System.out.println(" [x] Sent '" + topic + "':'" + message + "'");
    }

    @RabbitListener(queues = "Broker")
    public void recebeMsg(String message) {
        String[] dadosM = message.split(";");
        String tipo = dadosM[0];
        String acao = dadosM[1];
        String quantidade = dadosM[2];
        String preco = dadosM[3];
        String corretora = dadosM[4];
        System.out.println(formatMsg(tipo, acao, quantidade, preco, corretora));
    }

    private String formatMsg(String tipo, String acao, String quantidade, String preco, String corretora) {
        return "Novos pedidos sobre a ação " + acao + ":\nTipo: " + tipo + "\nQuantidade: " + quantidade +
                "\nPreço: " + preco + "\nBroker responsável: " + corretora;
    }
}
