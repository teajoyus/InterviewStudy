// Flutter code sample for material.AppBar.1

// This sample shows an [AppBar] with two simple actions. The first action
// opens a [SnackBar], while the second action navigates to a new page.

import 'dart:io';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

void main() {
  debugPaintSizeEnabled = true;
  runApp(MyApp());

}
const int a = 23;
//final int a =25;
/// This Widget is the main application widget.
class MyApp extends StatelessWidget {
  static const String _title = 'Flutter Code Sample';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: _title,
      home: MyStatelessWidget(),
    );
  }
}

/// This is the stateless widget that the main application instantiates.
class MyStatelessWidget extends StatelessWidget {
  MyStatelessWidget({Key key}) : super(key: key);
  final TextEditingController _controller = TextEditingController();
  @override
  Widget build(BuildContext context) {
    //比onChange回调函数更先被回调
    _controller.addListener((){
      print("_controller selection:${_controller.selection} - ${_controller.selection.start}");
      print("_controller text:${_controller.text}");
//      showDialog(context: context,
//        builder: (BuildContext context) => AlertDialog(
//          title: Text("您输入了"),
//          content: Text("${_controller.text}"),
//        ),
//      );
    });

    return Scaffold(
      appBar: AppBar(title: Text('文本输入'),),
      body: Center(
        child: TextField(
          //每次输入都能回调已经输入的文本
          onChanged:(String str){
            print("str:$str");
            debugDumpApp();
          } ,
          decoration: InputDecoration(
            hintText: '这是hint text a = $a'
          ),
          controller: _controller,
        ),
      )
    );
  }
}
