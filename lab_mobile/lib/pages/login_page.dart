// ignore_for_file: unused_field
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:lab_flutter/components/my_button.dart';
import 'package:lab_flutter/components/my_textfild.dart';
import 'package:lab_flutter/models/broker.dart' as broker;
import 'package:lab_flutter/pages/cadastro.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  static Route<void> route() {
    return MaterialPageRoute(builder: (context) => const LoginPage());
  }

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final usernameTextController = TextEditingController();

  final passwordTextController = TextEditingController();

  Future<void> signUserIn() async {
    broker.Broker usuario = broker.Broker(email: usernameTextController.text);
    final login = jsonDecode(await usuario.login(passwordTextController.text));

    if (login['success'] as bool) {
      //context.showSnackBar(message: login['message'] as String);
      //Navigator.of(context).pushAndRemoveUntil(HomePage.route(), (route) => false);
    } else {
      //context.showErrorSnackBar(message: login['message'] as String);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: const Color(0xFF242424),
        body: SafeArea(
            child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const SizedBox(height: 50),
              const Padding(
                padding: EdgeInsets.fromLTRB(25, 0, 0, 0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Text("Login",
                        style: TextStyle(
                            color: Colors.white,
                            fontSize: 30,
                            fontWeight: FontWeight.bold)),
                  ],
                ),
              ),
              const SizedBox(height: 25),
              MyTextfield(
                  controller: usernameTextController,
                  hintText: "Email",
                  obscureText: false),
              const SizedBox(
                height: 25.0,
              ),
              MyTextfield(
                controller: passwordTextController,
                hintText: "Senha",
                obscureText: true,
              ),
              const Padding(
                padding: EdgeInsets.symmetric(horizontal: 25.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    Text(
                      'Esqueceu a senha',
                      style: TextStyle(color: Colors.white),
                    )
                  ],
                ),
              ),
              const SizedBox(height: 25.0),
              MyButton(
                onTap: signUserIn,
              ),
              const SizedBox(
                height: 25.0,
              ),
              const Padding(
                padding: EdgeInsets.fromLTRB(25, 0, 0, 0),
                child: Row(
                  children: [
                    Text(
                      "Sua primeira vez aqui?",
                      style: TextStyle(fontSize: 20, color: Colors.white),
                    ),
                  ],
                ),
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(25, 0, 0, 0),
                child: Row(
                  children: [
                    InkWell(
                      child: const Text(
                        "Cadastre-se",
                        style: TextStyle(
                            color: Colors.purple,
                            fontSize: 20,
                            fontWeight: FontWeight.bold),
                      ),
                      onTap: () {
                        Navigator.push(context, CadastroUsuario.route());
                      },
                    )
                  ],
                ),
              )
            ],
          ),
        )));
  }
}
