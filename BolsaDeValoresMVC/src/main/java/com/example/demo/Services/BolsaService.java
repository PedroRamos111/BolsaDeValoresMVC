package com.example.demo.Services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Models.Bolsa;
import com.example.demo.Repositories.BolsaRepository;

@Service
public class BolsaService {

    private static List<String> dadosList = new ArrayList<String>();

    @Autowired
    private BolsaRepository bolsaRepository;

    public com.example.demo.Models.Bolsa findByName(String name) {
        return bolsaRepository.findByName(name);
    }

    public Bolsa saveBolsa(Bolsa bolsa) {
        return bolsaRepository.save(bolsa);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @PostConstruct
    public void init() {
        String queueName = UUID.randomUUID().toString();
        Queue queue = new Queue(queueName, false);
        amqpAdmin.declareQueue(queue);

        TopicExchange exchange = new TopicExchange("topic_logs", false, false);
        amqpAdmin.declareExchange(exchange);

        Binding compraBinding = BindingBuilder.bind(queue).to(exchange).with("compra.#");
        Binding vendaBinding = BindingBuilder.bind(queue).to(exchange).with("venda.#");

        amqpAdmin.declareBinding(compraBinding);
        amqpAdmin.declareBinding(vendaBinding);

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitTemplate.getConnectionFactory());
        container.setQueueNames(queueName);
        container.setMessageListener(message -> recebePedido(message));
        container.start();
    }

    public void recebePedido(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received '" + routingKey + "':'" + messageBody + "'");
        try {
            enviaMsg(routingKey, messageBody, "BOLSA");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread threadLivro = new Thread(() -> {
            try {
                checkMatch(messageBody, routingKey);
                registraLivro();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        threadLivro.start();
    }

    public static synchronized void checkMatch(String message, String key) throws IOException {

        String[] dadosK = key.split("\\.");
        String tipo = dadosK[0];
        String acao = dadosK[1];
        String[] dadosM = message.split(";");
        String quantidade = dadosM[0];
        String preco = dadosM[1];
        String corretora = dadosM[2];
        Boolean achou = false;
        Boolean quantIgual = false;
        int quantRemovida = 0;
        List<String> auxList = new ArrayList<String>(dadosList);
        for (int i = 0; i < dadosList.size(); i++) {
            String[] aux = dadosList.get(i).split(";");
            String tipoLista = aux[0].split("\\.")[0];
            String acaoLista = aux[0].split("\\.")[1];
            String quantidadeLista = aux[1];
            String precoLista = aux[2];
            String corretoraLista = aux[3];
            if (!tipo.equals(tipoLista)) {
                if (acao.equals(acaoLista)) {
                    if (tipo.equals("compra")) {
                        if (Double.parseDouble(preco) >= Double.parseDouble(precoLista)) {
                            if (Integer.parseInt(quantidade) == Integer.parseInt(quantidadeLista)) {
                                auxList.remove(i - quantRemovida);
                                quantRemovida = +1;
                                registraTransacao(acao, Integer.parseInt(quantidade), Double.parseDouble(preco),
                                        corretora, corretoraLista);
                                achou = true;
                                quantIgual = true;
                            } else {
                                int quantTransferida = 0;
                                int quantRestante = 0;
                                String temp = auxList.remove(i - quantRemovida);
                                quantRemovida = +1;
                                String[] aux2 = temp.split(";");
                                if (Integer.parseInt(quantidade) < Integer.parseInt(quantidadeLista)) {
                                    quantRestante = Integer.parseInt(aux2[1]) - Integer.parseInt(quantidade);
                                    tipo = tipoLista;
                                    quantTransferida = Integer.parseInt(quantidadeLista) - quantRestante;
                                } else {
                                    quantRestante = Integer.parseInt(quantidade) - Integer.parseInt(aux2[1]);
                                    quantTransferida = Integer.parseInt(quantidade) - quantRestante;
                                }
                                quantidade = String.valueOf(quantRestante);
                                achou = true;
                                registraTransacao(acao, quantTransferida, Double.parseDouble(preco),
                                        corretora, corretoraLista);
                            }
                        }

                    } else {
                        if (Double.parseDouble(preco) <= Double.parseDouble(precoLista)) {
                            if (Integer.parseInt(quantidade) == Integer.parseInt(quantidadeLista)) {
                                auxList.remove(i - quantRemovida);
                                quantRemovida = +1;
                                achou = true;
                                quantIgual = true;
                                registraTransacao(acao, Integer.parseInt(quantidade), Double.parseDouble(preco),
                                        corretoraLista, corretora);
                            } else {
                                int quantTransferida = 0;
                                int quantRestante = 0;
                                String temp = auxList.remove(i - quantRemovida);
                                quantRemovida += 1;
                                String[] aux2 = temp.split(";");
                                if (Integer.parseInt(quantidade) > Integer.parseInt(quantidadeLista)) {
                                    quantRestante = Integer.parseInt(quantidade) - Integer.parseInt(aux2[1]);
                                    quantTransferida = Integer.parseInt(quantidade) - quantRestante;
                                } else {
                                    quantRestante = Integer.parseInt(aux2[1]) - Integer.parseInt(quantidade);
                                    tipo = tipoLista;
                                    quantTransferida = Integer.parseInt(quantidadeLista) - quantRestante;
                                }
                                quantidade = String.valueOf(quantRestante);
                                achou = true;

                                registraTransacao(acao, quantTransferida, Double.parseDouble(precoLista),
                                        corretoraLista, corretora);
                            }
                        }
                    }
                }
            }
        }

        if (!achou) {
            dadosList.add(key + ";" + quantidade + ";" + preco + ";" + corretora);
        } else if (!quantIgual) {
            String temp = tipo + "." + acao + ";" + quantidade + ";"
                    + preco + ";" + corretora;
            auxList.add(temp);
            dadosList.clear();
            dadosList.addAll(auxList);
        } else {
            dadosList.clear();
            dadosList.addAll(auxList);
        }
        for (int j = 0; j < dadosList.size(); j++) {
            System.out.println(dadosList.get(j));
        }

        registraLivro();
    }

    public void enviaMsg(String topic, String message, String name) {
        rabbitTemplate.convertAndSend(name, topic, message);
        System.out.println(" [x] Sent '" + topic + "':'" + message + "'");
    }

    public static void getDadosLivro() {
        /*
         * try {
         * BufferedReader reader = new BufferedReader(new FileReader(arqLivro));
         * String linha;
         * 
         * while ((linha = reader.readLine()) != null) {
         * StringTokenizer str = new StringTokenizer(linha, "\n");
         * String dadosL = str.nextToken();
         * String[] dados = dadosL.split(";");
         * String key = dados[0];
         * String message = dados[1] + ";" + dados[2] + ";" + dados[3];
         * checkMatch(message, key);
         * }
         * reader.close();
         * } catch (IOException e) {
         * e.printStackTrace();
         * }
         */
    }

    public static void registraLivro() throws IOException {

        /*
         * FileWriter arquivo = new FileWriter(arqLivro, false);
         * try {
         * if (dadosList.size() == 0) {
         * arquivo.write("");
         * }
         * for (int i = 0; i < dadosList.size(); i++) {
         * 
         * String[] aa = dadosList.get(i).split(";", 2);
         * 
         * try {
         * arquivo.write(aa[0] + ";" + aa[1] + "\n");
         * } catch (IOException e) {
         * e.printStackTrace();
         * }
         * 
         * }
         * arquivo.close();
         * } catch (IOException e) {
         * e.printStackTrace();
         * }
         * 
         * for (int i = 0; i < dadosList.size(); i++) {
         * System.out.println(i + ": " + dadosList.get(i));
         * }
         */
    }

    private static synchronized void registraTransacao(String ativo, int quant, double val, String comprador,
            String vendedor) {
        /*
         * try {4
         * FileWriter arquivo = new FileWriter(arqTransacoes, true);
         * try {
         * String linha = LocalDateTime.now().format(formatador) + ";" + ativo + ";" +
         * quant + ";" + val + ";"
         * + comprador + ";" + vendedor;
         * arquivo.write(linha + "\n");
         * } catch (IOException e) {
         * e.printStackTrace();
         * }
         * arquivo.close();
         * String topico = "transacao" + "." + ativo;
         * String msg = LocalDateTime.now().format(formatador) + ";" + quant + ";" + val
         * + ";"
         * + comprador + ";" + vendedor;
         * enviaMsg(topico, msg, "BOLSA");
         * } catch (IOException | TimeoutException e) {
         * e.printStackTrace();
         * }
         */

    }
}
