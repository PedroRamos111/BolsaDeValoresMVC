// ignore_for_file: unused_field, use_build_context_synchronously
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
    final login = jsonDecode(
        await loginF(passwordTextController.text, usernameTextController.text));

    if (login['success'] as bool) {
      globals.loggedInUser = usernameTextController.text;
      context.showSnackBar(message: 'Login realizado com sucesso');
      Navigator.of(context)
          .pushAndRemoveUntil(HomePage.route(), (route) => false);
    } else {
      context.showErrorSnackBar(message: 'Erro no login');
    }
  }

  Future<String> loginF(String password, String name) async {
    await dotenv.load(fileName: ".env");
    final loginRoute = Uri.parse('${dotenv.env['API_URL']}/login');

    final fetch = await http.post(
      loginRoute,
      body: {"name": name, "password": password},
    );

    //final data = jsonDecode(fetch.body.toString());
    Object res;

    if (fetch.statusCode == 200) {
      res = {"success": true};
    } else {
      res = {
        "success": false,
      };
    }
    return jsonEncode(res);
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
                  hintText: "Nome",
                  obscureText: false),
              const SizedBox(
                height: 25.0,
              ),
              MyTextfield(
                controller: passwordTextController,
                hintText: "Senha",
                obscureText: true,
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
