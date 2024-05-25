import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'package:lab_flutter/utils/globals.dart' as globals;

import 'acompanhar.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  static Route<void> route() {
    return MaterialPageRoute(builder: (context) => const HomePage());
  }

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final _formKey = GlobalKey<FormState>();
  String transactionType = 'compra';
  final stockController = TextEditingController();
  final quantityController = TextEditingController();
  final priceController = TextEditingController();

  Future<void> _submitTransaction() async {
    if (_formKey.currentState!.validate()) {
      if (globals.loggedInUser == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Usuário não autenticado!')),
        );
        return;
      }

      String endpoint = transactionType == 'compra' ? '/compra' : '/venda';
      var response = await http.post(
        Uri.parse('${dotenv.env['API_URL']}$endpoint'),
        body: {
          'ativo': stockController.text,
          'quant': quantityController.text,
          'val': priceController.text,
          'username': globals.loggedInUser,
        },
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Transação enviada com sucesso!')),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao enviar transação!')),
        );
      }
    }
  }

  void _navigateToAcompanhar() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => AcompanharPage()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Compra e Venda de Ações'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              DropdownButtonFormField<String>(
                value: transactionType,
                items: [
                  DropdownMenuItem(value: 'compra', child: Text('Compra')),
                  DropdownMenuItem(value: 'venda', child: Text('Venda')),
                ],
                onChanged: (value) {
                  setState(() {
                    transactionType = value!;
                  });
                },
                decoration: InputDecoration(labelText: 'Tipo de Transação'),
              ),
              TextFormField(
                controller: stockController,
                decoration: InputDecoration(labelText: 'Ação'),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor, insira a ação';
                  }
                  return null;
                },
              ),
              TextFormField(
                controller: quantityController,
                decoration: InputDecoration(labelText: 'Quantidade'),
                keyboardType: TextInputType.number,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor, insira a quantidade';
                  }
                  return null;
                },
              ),
              TextFormField(
                controller: priceController,
                decoration: InputDecoration(labelText: 'Preço'),
                keyboardType: TextInputType.number,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor, insira o preço';
                  }
                  return null;
                },
              ),
              SizedBox(height: 20),
              ElevatedButton(
                onPressed: _submitTransaction,
                child: Text('Enviar'),
              ),
              SizedBox(height: 20),
              ElevatedButton(
                onPressed: _navigateToAcompanhar,
                child: Text('Quero acompanhar uma ação'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
