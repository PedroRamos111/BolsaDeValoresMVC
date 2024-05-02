package com.example.demo.Services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.core.*;
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

    @PostConstruct
    public void start() {
        System.out.println("start bolsa");
    }

    public void recebePedido(String message) {
        System.out.println("Recebido: " + message);
        checkMatch(message);
    }

    public synchronized void checkMatch(String message) {
        String[] key = message.split(":");
        String[] dadosK = key[1].split("\\.");
        String tipo = dadosK[0];
        String acao = dadosK[1];
        String[] dadosM = key[0].split(";");
        String quantidade = dadosM[0];
        String preco = dadosM[1];
        String corretora = dadosM[2];
        Boolean achou = false;
        Boolean quantIgual = false;
        int quantRemovida = 0;
        System.out.println("tipo: " + tipo);
        System.out.println("acao: " + acao);
        System.out.println("quantidade: " + quantidade);
        System.out.println("preco:" + preco);
        System.out.println("corretora: " + corretora);
        /*
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
        registraTransacao(acao, Integer.parseInt(quantidade),
        Double.parseDouble(preco),
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
        registraTransacao(acao, Integer.parseInt(quantidade),
        Double.parseDouble(preco),
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
         */
    }

    public void enviaMsg(String topic, String message, String name) {
        rabbitTemplate.convertAndSend(name, topic, message);
        System.out.println(" [x] Sent '" + topic + "':'" + message + "'");
    }

    public static void getDadosLivro() {
        /*
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
         */
    }

    public static void registraLivro() throws IOException {

        /*
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
         */
    }

    private static synchronized void registraTransacao(String ativo, int quant, double val, String comprador,
            String vendedor) {
        /*
        try {4
        FileWriter arquivo = new FileWriter(arqTransacoes, true);
        try {
        String linha = LocalDateTime.now().format(formatador) + ";" + ativo + ";" +
        quant + ";" + val + ";"
        + comprador + ";" + vendedor;
        arquivo.write(linha + "\n");
        } catch (IOException e) {
        e.printStackTrace();
        }
        arquivo.close();
        String topico = "transacao" + "." + ativo;
        String msg = LocalDateTime.now().format(formatador) + ";" + quant + ";" + val
        + ";"
        + comprador + ";" + vendedor;
        enviaMsg(topico, msg, "BOLSA");
        } catch (IOException | TimeoutException e) {
        e.printStackTrace();
        }
         */

    }

}
