package com.example.demo.Services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Models.Bolsa;
import com.example.demo.Models.Livro;
import com.example.demo.Models.Transacao;
import com.example.demo.Repositories.BolsaRepository;
import com.example.demo.Repositories.LivroRepository;
import com.example.demo.Repositories.TransacaoRepository;

@Service
public class BolsaService {

    private static List<String> dadosList = new ArrayList<String>();
    private static DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm:ss");

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private BolsaRepository bolsaRepository;

    private List<Transacao> transacoes = new ArrayList<>();


    public List<Transacao> getTransacoes() {
        return transacoes;
    }
    

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
        getDadosLivro();
        transacoes = transacaoRepository.findAll();
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
                                quantRemovida += 1;
                                // Remove pedido do banco de dados
                                removeLivro(acao, corretoraLista, Integer.parseInt(quantidadeLista), Double.parseDouble(precoLista));
                                registraTransacao(acao, Integer.parseInt(quantidade),
                                        Double.parseDouble(preco),
                                        corretora, corretoraLista);
                                achou = true;
                                quantIgual = true;
                            } else {
                                int quantTransferida = 0;
                                int quantRestante = 0;
                                String temp = auxList.remove(i - quantRemovida);
                                quantRemovida += 1;
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
                                // Remover o livro parcialmente (atualizar quantidade) do banco de dados
                                updateLivroQuantidade(acao, corretoraLista, Integer.parseInt(aux2[1]), Double.parseDouble(precoLista), quantRestante);
                                registraTransacao(acao, quantTransferida, Double.parseDouble(preco),
                                        corretora, corretoraLista);
                            }
                        }
                    } else {
                        if (Double.parseDouble(preco) <= Double.parseDouble(precoLista)) {
                            if (Integer.parseInt(quantidade) == Integer.parseInt(quantidadeLista)) {
                                auxList.remove(i - quantRemovida);
                                quantRemovida += 1;
                                // Remove pedido do banco de dados
                                removeLivro(acao, corretoraLista, Integer.parseInt(quantidadeLista), Double.parseDouble(precoLista));
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
                                // Remover o livro parcialmente (atualizar quantidade) do banco de dados
                                updateLivroQuantidade(acao, corretoraLista, Integer.parseInt(aux2[1]), Double.parseDouble(precoLista), quantRestante);
                                registraTransacao(acao, quantTransferida, Double.parseDouble(precoLista),
                                        corretoraLista, corretora);
                            }
                        }
                    }
                }
            }
        }
    
        if (!achou) {
            dadosList.add(tipo + "." + acao + ";" + quantidade + ";" + preco + ";" + corretora);
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
    
    private void removeLivro(String acao, String corretora, int quantidade, double preco) {
        List<Livro> livros = livroRepository.findByBolsaAndCompradorAndQtdAndValor(acao, corretora, quantidade, preco);
        if (!livros.isEmpty()) {
            livroRepository.delete(livros.get(0));
        }
    }
    
    private void updateLivroQuantidade(String acao, String corretora, int quantidadeOriginal, double preco, int quantidadeRestante) {
        List<Livro> livros = livroRepository.findByBolsaAndCompradorAndQtdAndValor(acao, corretora, quantidadeOriginal, preco);
        if (!livros.isEmpty()) {
            Livro livro = livros.get(0);
            livro.setQtd(quantidadeRestante);
            livroRepository.save(livro);
        }
    }
    

    public void enviaMsg(String topic, String message, String name) {
        rabbitTemplate.convertAndSend(name, topic, message);
        System.out.println(" [x] Sent '" + topic + "':'" + message + "'");
    }

    public void getDadosLivro() {
        dadosList.clear();  // Limpa a lista para evitar dados duplicados
        List<Livro> livros = livroRepository.findAll();
        for (Livro livro : livros) {
            String tipo = livro.getAtividade();
            String acao = livro.getBolsa();
            String corretora = livro.getComprador();
            String quantidade = String.valueOf(livro.getQtd());
            String preco = String.valueOf(livro.getValor());
            String msg = tipo + "." + acao + ";" + quantidade + ";" + preco + ";" + corretora;
            dadosList.add(msg);
        }
    }

    public void registraLivro() {
        livroRepository.deleteAll();
        
        for (int i = 0; i < dadosList.size(); i++) {
            String[] aux = dadosList.get(i).split(";", 2);
            String key = aux[0];
            String message = aux[1];
            String[] dadosK = key.split("\\.");
            String tipo = dadosK[0];
            String acao = dadosK[1];
            String[] dadosM = message.split(";");
            String quantidade = dadosM[0];
            String preco = dadosM[1];
            String corretora = dadosM[2];
            
            // Salva os novos registros no banco de dados
            saveLivro(tipo, acao, corretora, Integer.parseInt(quantidade), Double.parseDouble(preco));
            
            String topico = acao;
            String msg = tipo + ";" + acao + ";" + quantidade + ";" + preco + ";" + corretora;
            enviaMsg(topico, msg, "Broker");
        }
    
        for (int i = 0; i < dadosList.size(); i++) {
            System.out.println(i + ": " + dadosList.get(i));
        }
    }

    
    private void registraTransacao(String ativo, int quant, double val, String comprador, String vendedor) {
        Transacao transacao = new Transacao();
        transacao.setBolsa(ativo);
        transacao.setComprador(comprador);
        transacao.setVendedor(vendedor);
        transacao.setData(LocalDateTime.now().format(formatador));
        transacao.setQtd(quant);
        transacao.setValor(val);
        transacaoRepository.save(transacao);
    
        String topico = "transacao" + ";" + ativo;
        String msg = LocalDateTime.now().format(formatador) + ";" + quant + ";" + val
                + ";" + comprador + ";" + vendedor;
        enviaMsg(topico, msg, "Broker");
    
        // Atualiza a lista de dados com a nova transação
        dadosList.clear();
        getDadosLivro();
    }

    
    public Livro saveLivro(String tipo, String acao, String corretora, int quant, double valor) {
        Livro livro = new Livro();
        livro.setAtividade(tipo);
        livro.setBolsa(acao);
        livro.setComprador(corretora);
        livro.setQtd(quant);
        livro.setValor(valor);
        return livroRepository.save(livro);
    }
    

}
