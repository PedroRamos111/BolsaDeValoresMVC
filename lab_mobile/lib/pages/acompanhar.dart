import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:lab_flutter/components/my_button.dart';
import 'package:lab_flutter/components/my_textfild.dart';
import 'package:http/http.dart' as http;
import 'package:lab_flutter/pages/cadastro.dart';
import 'package:lab_flutter/utils/constants.dart';
import 'package:lab_flutter/utils/globals.dart' as globals;
import 'homepage.dart';


class AcompanharPage extends StatefulWidget {
  @override
  _AcompanharPageState createState() => _AcompanharPageState();
}

class _AcompanharPageState extends State<AcompanharPage> {
  final _formKey = GlobalKey<FormState>();
  final acompanharController = TextEditingController();

  Future<void> _acompanharAcao() async {
    if (_formKey.currentState!.validate()) {
      if (globals.loggedInUser == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Usuário não autenticado!')),
        );
        return;
      }

      var response = await http.post(
        Uri.parse('${dotenv.env['API_URL']}/acompanha'),
        body: {
          'acompanha': acompanharController.text,
          'username': globals.loggedInUser, // Envia o nome do usuário
        },
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Acompanhamento enviado com sucesso!')),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao enviar acompanhamento!')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Acompanhar Ação'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: acompanharController,
                decoration: InputDecoration(labelText: 'Ação a Acompanhar'),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor, insira a ação';
                  }
                  return null;
                },
              ),
              SizedBox(height: 20),
              ElevatedButton(
                onPressed: _acompanharAcao,
                child: Text('Acompanhar'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
