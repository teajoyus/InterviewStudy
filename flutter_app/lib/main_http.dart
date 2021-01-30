// Flutter code sample for material.AppBar.1

// This sample shows an [AppBar] with two simple actions. The first action
// opens a [SnackBar], while the second action navigates to a new page.

import 'dart:io';
import 'dart:convert';

import 'package:flutter/material.dart';

void main() => runApp(MyApp());

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

final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey<ScaffoldState>();
final SnackBar snackBar = const SnackBar(content: Text('Showing Snackbar'));

void openPage(BuildContext context) {
  Navigator.push(context, MaterialPageRoute(
    builder: (BuildContext context) {
      return Scaffold(
        appBar: AppBar(
          title: const Text('Next page'),
        ),
        body: const Center(
          child: Text(
            'This is the next page',
            style: TextStyle(fontSize: 24),
          ),
        ),
      );
    },
  ));
}

/// This is the stateless widget that the main application instantiates.
class MyStatelessWidget extends StatelessWidget {
  MyStatelessWidget({Key key}) : super(key: key);



  Future requestHttps() async {
    String dataURL = "https://jsonplaceholder.typicode.com/posts";
    var http = HttpClient();
    try {
      Uri uri = Uri.parse(dataURL);
      //到了这里就异步了，所以调用者不会等这个方法的返回，而是执行到这里后直接就跳过了这个方法
      var request = await http.getUrl(uri);
      var response = await request.close();
      if (response.statusCode != HttpStatus.ok) {
        print("status code != 200");
      }
      var json = await response.transform(utf8.decoder).join();
      print("esponse.transform");
      var data = jsonDecode(json);
      print("body:${data[1]['title']}");
    } catch (exception) {
 
    }
    //会等上面执行完才执行这里
    print("requestHttps method return");
  }

  @override
  Widget build(BuildContext context) {
    requestHttps();
    //遇到异步直接来到这里，不会等待那个方法执行完毕
    print("build");
    return Scaffold(
      key: scaffoldKey,
      appBar: AppBar(
        title: const Text('AppBar Demo'),
        actions: <Widget>[
          IconButton(
            icon: const Icon(Icons.add_alert),
            tooltip: 'Show Snackbar',
            onPressed: () {
              scaffoldKey.currentState.showSnackBar(snackBar);
            },
          ),
          IconButton(
            icon: const Icon(Icons.navigate_next),
            tooltip: 'Next page',
            onPressed: () {
              openPage(context);
            },
          ),
        ],
      ),
      body: const Center(
        child: Text(
          'This is the home page',
          style: TextStyle(fontSize: 24),
        ),
      ),
    );
  }
}
