
import 'package:flutter/material.dart';

class MyButton extends StatelessWidget {
 final Function()? onTap;

  const MyButton({
    super.key,
    required this.onTap
    });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          Container(
          padding: const EdgeInsets.fromLTRB(25, 10, 25, 10),
          margin: const EdgeInsets.symmetric(horizontal: 25.0),
          decoration: BoxDecoration(
          color: Colors.purple,
          borderRadius: BorderRadius.circular(10),
          ),
          child: const Center(child: 
           Text(
            "Confirmar",
            style: TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 16,
              ),
            ),
        ),
      ),
        ],
        ), 
        
    );
  }
}
