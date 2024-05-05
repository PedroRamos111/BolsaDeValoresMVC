# BolsaDeValoresMVC

Grupo:

Giovanni Duarte;
<br>Pedro Ramos Vidigal;
<br>Carlos Emanuel;
<br>Gustavo Andrade
<br>Instruções para a compilação:

Baixar o mySQL workbench e criar uma conexão com o nome "bolsadevaloresmvc" e sem senha 
<br>Executar o arquivo BolsaDeValoresApplication.java
<br>Entrar no link: http://localhost:8080/registro
Explicação do código:

O código é feito em spring boot, contendo controllers, entidades, services e configurations.

O Broker está encarregado de enviar e receber mensagens da bolsa, enviando uma mensagem de pedidos de compra e venda, e recebendo atualizações sobre as ações escolhidas. O código do broker implementa a interface Runnable para enviar e receber as mensagens em threads e contem um método para ver qual ação será recebida as mensagens e um método para escolher o conteúdo das mensagens à ser enviada.

Já a bolsa está encarregada de receber os pedidos, enviar as atualizações das ações, descobrir se um pedido de venda ou compra coincide com outro pedido para gerar uma transação. O código da bolsa utiliza threads para receber e enviar as mensagens, também contem um método para registrar os dados de pedidos, um método para registrar os dados de transações, e um método para checar se os pedidos podem se transformar em transações, fazendo as alterações necessárias.
