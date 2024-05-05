package com.example.demo.Services;

import javax.servlet.http.HttpSession;
import com.example.demo.Services.RabbitMQTopicInitializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Models.Broker;
import com.example.demo.Repositories.BrokerRepository;

@Service
public class BrokerService implements MessageListener  {

    private String msgFormatada;
    
    public String getFormattedMessage() {
        return this.msgFormatada;
    }
    @Autowired
    private BrokerRepository brokerRepository;
    @Autowired
    private RabbitMQTopicInitializer rabbitMQTopicInitializer;

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
        message += ":" + topic;
        rabbitTemplate.convertAndSend("topic_logs", topic, message);
        System.out.println(" [x] Sent '" + message + "'");
    }

    @RabbitListener(queues = "BOLSA") // isso esta errado de proposito pq tava quebrando c os split q n existia
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

    public String acompanha(HttpSession session, String novaAcao) {
        String nome = (String) session.getAttribute("username");
        Broker broker = brokerRepository.findByName(nome);

        if (broker != null) {
            String acoesAcompanhadas = broker.getAcompanha();
            if (acoesAcompanhadas != null && !acoesAcompanhadas.isEmpty()) {
                acoesAcompanhadas += ";" + novaAcao;
            } else {
                acoesAcompanhadas = novaAcao;
            }

            broker.setAcompanha(acoesAcompanhadas);
            saveBroker(broker);

            return "Acompanha adicionado com sucesso!";
        } else {
            return "Você precisa estar logado para acompanhar.";
        }
    }

    public void acompanhados(HttpSession session) {
        String[] acompanhadosS = null;
        String nome = (String) session.getAttribute("username");
        Broker broker = brokerRepository.findByName(nome);
        if (broker != null) {
            String acoesAcompanhadas = broker.getAcompanha();
            acompanhadosS = acoesAcompanhadas.split(";");
        }
        rabbitMQTopicInitializer.initializeTopics(acompanhadosS);

    }

    @Override
    public void onMessage(Message msg) {
        String msgS = new String(msg.getBody()); 
        String[] dadosM = msgS.split(";");
        String tipo = dadosM[0];
        String acao = dadosM[1];
        String quantidade = dadosM[2];
        String preco = dadosM[3];
        String corretora = dadosM[4];
        this.msgFormatada = formatMsg(tipo, acao, quantidade, preco, corretora);
    }
}
