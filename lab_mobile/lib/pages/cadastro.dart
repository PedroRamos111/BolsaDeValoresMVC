import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter/material.dart';
import 'package:lab_flutter/pages/login_page.dart';


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
  final _emailController = TextEditingController();
  final _senhaController = TextEditingController();

  final String appTitle = 'HappyHour';  // String variable for app title
  final String cadastroText = 'Cadastro';
  final image = const AssetImage('assets/images/Bg.png');


  Future<void> signUserUp() async {
    final registerRoute = Uri.parse('localhost:8080/register');

    final fetch = await http.post(registerRoute,
      body: {'name':_nomeController.text,'email':_emailController.text,'password':_senhaController.text},
      encoding: const Utf8Codec(allowMalformed: false)
    );

    final data = jsonDecode(fetch.body.toString());

    if(fetch.statusCode == 201) {
      //context.showSnackBar(message: data['message']);
      Navigator.of(context)
          .pushAndRemoveUntil(LoginPage.route(), (route) => false);
    } else {
      //context.showErrorSnackBar(message: data['message'] + "! " + data['error']);
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
            decoration: BoxDecoration(
              image: DecorationImage(
                image: image,
                fit: BoxFit.cover,
              ),
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

                  Center(
                    child:
                    Text(
                      appTitle,
                      textAlign: TextAlign.center,
                      style: TextStyle(fontSize: screenSize.width * 0.14,
                          fontFamily:'LilyScriptOne',
                          color: Colors.white,
                          shadows: const [
                            Shadow( // bottomLeft
                                offset: Offset(-1, -1),
                                color: Colors.black
                            ),
                            Shadow( // bottomRight
                                offset: Offset(1, -1),
                                color: Colors.black
                            ),
                            Shadow( // topRight
                                offset: Offset(1, 1),
                                color: Colors.black
                            ),
                            Shadow( // topLeft
                                offset: Offset(-1, 1),
                                color: Colors.black
                            ),
                          ]
                      ),
                    ),
                  ),

                  SizedBox(height:MediaQuery.of(context).size.height * 0.05),

                  Text(
                    cadastroText,
                    style: TextStyle(fontSize: screenSize.width * 0.06, color: Colors.white),
                  ),

                  SizedBox(height:MediaQuery.of(context).size.height * 0.015),
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
                  // Campo de email
                  Container(
                    height: MediaQuery.of(context).size.height * 0.053,
                    child:
                    TextFormField(
                      controller: _emailController,
                      decoration: InputDecoration(hintText: 'Email',
                        hintStyle: TextStyle(color: Colors.white.withOpacity(0.5)),
                        border: OutlineInputBorder(
                          borderSide: BorderSide(color: Colors.white),
                          borderRadius: BorderRadius.circular(6.0),
                        ),
                        filled: true,
                        fillColor: Colors.white.withOpacity(0.2),
                        contentPadding: EdgeInsets.symmetric(vertical: 1, horizontal: 8), // Reduzindo o padding
                      ),
                      style:TextStyle(
                        fontSize: MediaQuery.of(context).size.height * 0.02,
                        color: Colors.white,
                        height: 1
                        ,
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return 'O email é obrigatório';
                        }
                        if (!RegExp(r"^[a-zA-Z0-9.a-zA-Z0-9!#$%&'*+-/=?^_`{|}~-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*$").hasMatch(value)) {
                          return 'Digite um email válido';
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
                          borderSide: BorderSide(color: Colors.white),
                          borderRadius: BorderRadius.circular(6.0),
                        ),
                        filled: true,
                        fillColor: Colors.white.withOpacity(0.2),
                        contentPadding: EdgeInsets.symmetric(vertical: 1, horizontal: 8), // Reduzindo o padding
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