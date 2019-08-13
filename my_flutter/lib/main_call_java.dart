
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
const platorm = const MethodChannel("samples.flutter.io/demo");

void main() => runApp(new MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return MaterialApp(
      home: MyHomePage(),
    );
  }
}
class MyHomePage extends StatelessWidget{
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Scaffold(
      appBar: AppBar(title: Text('我的Flutter集成'),),
      body: MyHomeContent(),
    );
  }
}
class MyHomeContent extends StatelessWidget{
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return MyFulWidget();
  }
}
class MyFulWidget extends StatefulWidget{
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return new MyState();
  }
}
class MyState extends State<MyFulWidget>{
  String str ;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    str = 'Button';
  }
  void _callJava() async{
    var result = await platorm.invokeMethod('getDemoString');
    setState(() {
      str = result;
    });
  }
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new Center(
        child: GestureDetector(
          onTap: _callJava,
          child: Container(
            height: 300,
            width: 300,
            color: Colors.blue,
            child:Center(
              child:Text(str,textAlign: TextAlign.center,) ,
            ),
          ),
        )
    );
  }

}