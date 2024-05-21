// ignore: file_names
import 'dart:convert';
import 'package:http/http.dart' as http;

class Broker {
  int? id;
  String? name;
  String? email;
  String? imagePath;
  List<int>? friendIds;
  
  Broker({
    required this.email,
    this.name,
  });

  Future<String> login(String password) async {
    final loginRoute = Uri.parse('localhost:8080/login');

    final fetch = await http.post(loginRoute,
        body: {"email": email, "password": password},
        );

    final data = jsonDecode(fetch.body.toString());
    Object res;

    if (fetch.statusCode == 200) {
      final user = jsonDecode(data['data'].toString());
      id = user['id'] as int;
      name = user['name'] as String;
      res = {"success": true, "message": data['message'].toString()};
    } else {
      res = {
        "success": false,
        "message": "${data['message']}! ${data['error']}"
      };
    }
    return jsonEncode(res);
  }
}
