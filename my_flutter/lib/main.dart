import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const platform = const MethodChannel('samples.flutter.io/counter_native33333');

void main() => runApp(new MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('Flutter集成'),
        ),
        body: new MyContent(),
      ),
    );
  }
}

class MyContent extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Container(
      height: 500,
      width: 500,
      color: Colors.blue,
      child: new MyWidget(),
    );
  }
}

class MyWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return new MyState('调用Android');
  }
}

class MyState extends State<MyWidget> {
  String str;

  MyState(String string) : super() {
    str = string;
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return RaisedButton(
      onPressed: getDemoString,
      child: Text(str),
    );
  }

  void getDemoString() async {
    var result = await platform.invokeMethod("getDemoString");
    setState(() {
      str = result;
    });
  }
}
