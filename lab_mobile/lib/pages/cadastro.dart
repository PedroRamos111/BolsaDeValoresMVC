// ignore_for_file: use_build_context_synchronously

import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'package:flutter/material.dart';
import 'package:lab_flutter/pages/login_page.dart';
import 'package:lab_flutter/utils/constants.dart';

class CadastroUsuario extends StatefulWidget {
  const CadastroUsuario({super.key});

  static Route<void> route() {
    return MaterialPageRoute(builder: (context) => const CadastroUsuario());
  }

  @override
  State<CadastroUsuario> createState() => _CadastroUsuarioState();
}

class _CadastroUsuarioState extends State<CadastroUsuario> {
  final _formKey = GlobalKey<FormState>();
  final _nomeController = TextEditingController();
  final _senhaController = TextEditingController();

  final String cadastroText = 'Cadastro';

  Future<void> signUserUp() async {
    await dotenv.load(fileName: ".env");
    final registerRoute = Uri.parse('${dotenv.env['API_URL']}/register');

    print(_senhaController.text);
    final fetch = await http.post(
      registerRoute,
      body: {"name": _nomeController.text, "senha": _senhaController.text},
    );

    if (fetch.statusCode == 200) {
      context.showSnackBar(message:'Cadastro realizado com sucesso');
      Navigator.of(context)
          .pushAndRemoveUntil(LoginPage.route(), (route) => false);
    } else {
      context.showErrorSnackBar(
          message: 'Erro de cadastro');
    }
  }

  @override
  Widget build(BuildContext context) {

    Size screenSize = MediaQuery.of(context).size;
    return Scaffold(
      resizeToAvoidBottomInset: false,
      body:Stack(
        children: [
          Container(
            height: MediaQuery.of(context).size.height,
            width: MediaQuery.of(context).size.width,
            decoration: const BoxDecoration(
              color: Colors.black,
            ),
          ),
          SingleChildScrollView(
            padding: EdgeInsets.symmetric(
              vertical: MediaQuery.of(context).size.height * 0.18,
              horizontal: MediaQuery.of(context).size.width * 0.1,
            ),
            child:
            Form(
              key: _formKey,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [

                  SizedBox(height:MediaQuery.of(context).size.height * 0.05),
                  SizedBox(height:MediaQuery.of(context).size.height * 0.015),
                  Text(
                    cadastroText,
                    style: TextStyle(fontSize: screenSize.width * 0.06, color: Colors.white),
                  ),
                  // Campo de nome
                  Container(
                    height: MediaQuery.of(context).size.height * 0.053,
                    child:
                    TextFormField(
                      controller: _nomeController,
                      decoration: InputDecoration(

                        hintText: 'Nome de usuário',
                        hintStyle: TextStyle(color: Colors.white.withOpacity(0.5),),
                        border: OutlineInputBorder(
                          borderSide: BorderSide(color: Colors.white),
                          borderRadius: BorderRadius.circular(6.0),
                        ),
                        filled: true,
                        fillColor: Colors.white.withOpacity(0.2),
                        contentPadding: EdgeInsets.symmetric(vertical: 1, horizontal: 8), // Reduzindo o padding
                      ),
                      style: TextStyle(
                        fontSize: MediaQuery.of(context).size.height * 0.02,
                        color: Colors.white,
                        height: 1
                        ,
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return 'O nome de usuário é obrigatório';
                        }
                        return null;
                      },
                    ),
                  ),
                  SizedBox(height:MediaQuery.of(context).size.height * 0.03),
                  
                  // Campo de senha
                  SizedBox(
                    height: MediaQuery.of(context).size.height * 0.053,
                    child:
                    TextFormField(
                      controller: _senhaController,
                      decoration: InputDecoration(
                        hintText: 'Senha',
                        hintStyle: TextStyle(color: Colors.white.withOpacity(0.5),),
                        border: OutlineInputBorder(
                          borderSide: const BorderSide(color: Colors.white),
                          borderRadius: BorderRadius.circular(6.0),
                        ),
                        filled: true,
                        fillColor: Colors.white.withOpacity(0.2),
                        contentPadding: const EdgeInsets.symmetric(vertical: 1, horizontal: 8), // Reduzindo o padding
                      ),
                      style:TextStyle(
                        fontSize: MediaQuery.of(context).size.height * 0.02,
                        color: Colors.white,
                        height: 1
                        ,
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return 'A senha é obrigatória';
                        }
                        if (value.length < 6) {
                          return 'A senha deve ter no mínimo 6 caracteres';
                        }
                        return null;
                      },
                    ),
                  ),
                  SizedBox(height:MediaQuery.of(context).size.height * 0.04),
                  // Botão de Cadastrar
                  Center(child:
                  ElevatedButton(
                    onPressed: () {
                      signUserUp();
                    },
                    child: const Text('    Cadastrar    '),
                    style: ElevatedButton.styleFrom(

                      textStyle: TextStyle(fontSize: MediaQuery.of(context).size.height * 0.032,),
                      backgroundColor: Colors.deepPurple,
                      foregroundColor: Colors.white,
                      elevation: 3, // button's elevation when it's pressed
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(5),
                        // button's shape
                      ),
                    ),
                  ),
                  ),
                  SizedBox(height:MediaQuery.of(context).size.height * 0.03),
                  // Link para tela de Login
                  TextButton(
                    onPressed: () {},
                    style: TextButton.styleFrom(
                      foregroundColor: Colors.white,
                    ),
                    child: InkWell(
                      child: Text(
                        'Já é cadastrado?\nFaça login aqui!',
                        style: TextStyle(fontSize: MediaQuery.of(context).size.height * 0.02),
                      ),
                      onTap: () {Navigator.push(context, LoginPage.route());},
                    )
                  )
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
  }