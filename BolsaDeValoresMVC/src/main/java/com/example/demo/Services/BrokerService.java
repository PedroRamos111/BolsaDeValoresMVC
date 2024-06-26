package com.example.demo.Services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.Models.Broker;
import com.example.demo.Models.Livro;
import com.example.demo.Models.Transacao;
import com.example.demo.Repositories.BrokerRepository;
import com.example.demo.Repositories.LivroRepository;
import com.example.demo.Repositories.TransacaoRepository;

@Service
public class BrokerService implements MessageListener {

    private String msgFormatada;

    public String getFormattedMessage() {
        return this.msgFormatada;
    }

    @Autowired
    private BrokerRepository brokerRepository;
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private TransacaoRepository transacaoRepository;
    @Autowired
    private RabbitMQTopicInitializer rabbitMQTopicInitializer;

    private List<Livro> todasCompras = new ArrayList<>();
    private List<Livro> todasVendas = new ArrayList<>();
    private List<Transacao> transacoes = new ArrayList<>();

    
    public List<Livro> getTodasCompras() {
        return todasCompras;
    }

    public List<Livro> getTodasVendas() {
        return todasVendas;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }


    @PostConstruct
    public void init() {
        todasCompras = livroRepository.findByAtividade("compra");
        todasVendas = livroRepository.findByAtividade("venda");
        transacoes = transacaoRepository.findAll();
    }



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

    public void compra(String ativo, int quant, double val, String corretora) {
        Livro livro = new Livro();
        livro.setAtividade("compra");
        livro.setComprador(corretora);
        livro.setBolsa(ativo);
        livro.setQtd(quant);
        livro.setValor(val);
        livroRepository.save(livro);
        todasCompras.add(livro);
        enviaPedido("compra." + ativo, quant + ";" + val + ";" + corretora);
    }

    public void venda(String ativo, int quant, double val, String corretora) {
        Livro livro = new Livro();
        livro.setAtividade("venda");
        livro.setComprador(corretora);
        livro.setBolsa(ativo);
        livro.setQtd(quant);
        livro.setValor(val);
        livroRepository.save(livro);
        todasVendas.add(livro);
        enviaPedido("venda." + ativo, quant + ";" + val + ";" + corretora);
    }

    private void enviaPedido(String topic, String message) {
        message += ":" + topic;
        rabbitTemplate.convertAndSend("topic_logs", topic, message);
        System.out.println(" [x] Sent '" + message + "'");
    }

 
    @RabbitListener(queues = "BOLSA")
    public void recebeMsg(String message) {
        String[] dadosM = message.split(";");
        String tipo = dadosM[0];
        String acao = dadosM[1];
        String quantidade = dadosM[2];
        String preco = dadosM[3];
        String corretora = dadosM[4];
        Transacao transacao = new Transacao();
        transacao.setComprador(corretora);
        transacao.setBolsa(acao);
        transacao.setQtd(Integer.parseInt(quantidade));
        transacao.setValor(Double.parseDouble(preco));
        transacaoRepository.save(transacao);
        transacoes.add(transacao);
        System.out.println(formatMsg(tipo, acao, quantidade, preco, corretora));
    }

    private String formatMsg(String tipo, String acao, String quantidade, String preco, String corretora) {
        return "Novos pedidos sobre a ação " + acao + ":\nTipo: " + tipo + "\nQuantidade: " + quantidade +
                "\nPreço: " + preco + "\nBroker responsável: " + corretora;
    }

    public String acompanha(String nome, String novaAcao) {
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
        System.out.println("aaaaaaaasdasdasdasdasdasdasdadasdasdasdasdasd");
    }
}
