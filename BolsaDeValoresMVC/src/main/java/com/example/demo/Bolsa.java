package com.example.demo;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.io.File;

public class Bolsa {

    private static final String EXCHANGE_NAME = "topic_logs";
    private static final String arqLivro = "POO_Livro.csv";
    private static final String arqTransacoes = "POO_Transacao.csv";
    private static List<String> dadosList = new ArrayList<String>();
    private static List<String> transacoesList = new ArrayList<String>();
    private static DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm:ss");

    public static void inicio() throws InterruptedException {

        Thread threadRecv = new Thread(new Runnable() {
            public void run() {

                try {
                    recebePedido();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread threadSend = new Thread(new Runnable() {
            public void run() {

                System.out.println("Executando a segunda função");
            }
        });

        Thread threadLivros = new Thread(new Runnable() {
            public void run() {

                getDadosLivro();
            }
        });
        threadLivros.start();
        threadLivros.join();
        threadRecv.start();

        // threadSend.start();
    }

    public static void recebePedido() throws IOException, TimeoutException {

        String[] listTopics = new String[2];
        listTopics[0] = "compra";
        listTopics[1] = "venda";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("gull.rmq.cloudamqp.com");
        factory.setUsername("zwzsdwdx");
        factory.setPassword("dIPnl1KCfla3vDb6FzjDOLh30BP-mrtu");
        factory.setVirtualHost("zwzsdwdx");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();

        if (listTopics.length < 1) {
            System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
            System.exit(1);
        }

        channel.queueBind(queueName, EXCHANGE_NAME, "compra.#");
        channel.queueBind(queueName, EXCHANGE_NAME, "venda.#");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), "UTF-8");
            String routingKey = delivery.getEnvelope().getRoutingKey();

            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            System.out.println(routingKey);
            System.out.println(message);
            try {
                enviaMsg(routingKey, message, "BOLSA");
            } catch (TimeoutException e) {

                e.printStackTrace();
            }
            Thread threadLivro = new Thread(new Runnable() {
                public void run() {

                    try {
                        checkMatch(message, routingKey);
                        registraLivro();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

            // Thread threadEscreve = new Thread(new Runnable() {
            // public void run() {

            // }
            // });
            threadLivro.start();
            // threadEscreve.start();
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

    public static void registraLivro() throws IOException {

        FileWriter arquivo = new FileWriter(arqLivro, false);
        try {
            if (dadosList.size() == 0) {
                arquivo.write("");
            }
            for (int i = 0; i < dadosList.size(); i++) {

                String[] aa = dadosList.get(i).split(";", 2);

                try {
                    arquivo.write(aa[0] + ";" + aa[1] + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            arquivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < dadosList.size(); i++) {
            System.out.println(i + ": " + dadosList.get(i));
        }

    }

    public static void getDadosLivro() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(arqLivro));
            String linha;

            while ((linha = reader.readLine()) != null) {
                StringTokenizer str = new StringTokenizer(linha, "\n");
                String dadosL = str.nextToken();
                String[] dados = dadosL.split(";");
                String key = dados[0];
                String message = dados[1] + ";" + dados[2] + ";" + dados[3];
                checkMatch(message, key);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static synchronized void registraTransacao(String ativo, int quant, double val, String comprador,
            String vendedor) {
        try {
            FileWriter arquivo = new FileWriter(arqTransacoes, true);
            try {
                String linha = LocalDateTime.now().format(formatador) + ";" + ativo + ";" + quant + ";" + val + ";"
                        + comprador + ";" + vendedor;
                arquivo.write(linha + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            arquivo.close();
            String topico = "transacao" + "." + ativo;
            String msg = LocalDateTime.now().format(formatador) + ";" + quant + ";" + val + ";"
                    + comprador + ";" + vendedor;
            enviaMsg(topico, msg, "BOLSA");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    public static void enviaMsg(String topic, String message, String name) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("gull.rmq.cloudamqp.com");
        factory.setUsername("zwzsdwdx");
        factory.setPassword("dIPnl1KCfla3vDb6FzjDOLh30BP-mrtu");
        factory.setVirtualHost("zwzsdwdx");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(name, BuiltinExchangeType.TOPIC);

        channel.basicPublish(name, topic, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + topic + "':'" + message + "'");

        channel.close();
        connection.close();
    }
}
