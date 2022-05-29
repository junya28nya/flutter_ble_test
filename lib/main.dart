import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

Test test = Test();
class Test{
  Test(){
    print("Test.test()");
    j = 1;
  }
  int i = 0;
  int j = 0;
  void printTest(){
    print("printTest()");
  }
}
class StaticClass{
  static int i = 0;
  static void StaticInc(){
    i++;
  }
}
void main() {
  print("main()");
  print("StaticClass.i : " + StaticClass.i.toString());
  StaticClass.StaticInc();
  print("StaticClass.i : " + StaticClass.i.toString());
  test.printTest();
  runApp(const MyApp());
  print("main() ended.");
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'My Todo App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: TodoListPage(),
    );
  }
}


class TodoListPage extends StatefulWidget {
  TodoListPage(){
    print("TodoListPage.TodoListPage()");
  }
  @override
  State<TodoListPage> createState() => _TodoListPageState();
}

class _TodoListPageState extends State<TodoListPage> {
  Test test = Test();
  List<String> todoLIst = [];
  static const MethodChannel _channel = const MethodChannel('com.example.methodchannel/interop');

  String text = "aaa";

  Future<String> _myMethod() async{
    return await _channel.invokeMethod("test01");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title:Text("リスト一覧"),
      ),

      body: Container(
          //decoration: BoxDecoration(color:Colors.red),
        color: Colors.tealAccent,
        child: Center(
          child: Text(text)
          /*ListView.builder(
              itemCount: todoLIst.length,
              itemBuilder: (context, index){
                return Card(
                    child: ListTile(title: Text(todoLIst[index]))
              );
            })*/
/*          child: ListView(
            children: <Widget>[
              Card(child:ListTile(title:Text("ああああ"))),
              Card(child:ListTile(title:Text("いいいい"))),
              Card(child:ListTile(title:Text("うううう"))),
              Text("aaaaa"),
              Text("bbbbb"),
              Text("ccccc"),
            ],
          )*/
        )
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          /*final newListText =  await Navigator.of(context).push(
            MaterialPageRoute(builder: (context){
              return TodoAddPage();
            }),
          );
          print("newListText : " + newListText);
          */
          text = await _myMethod();
          setState((){
            //text = await _myMethod();
            //todoLIst.add(newListText);
          });
        },
        child: Text("ボタン"),
      ),
    );
  }
}

class TodoAddPage extends StatefulWidget {
  const TodoAddPage({Key? key}) : super(key: key);

  @override
  State<TodoAddPage> createState() => _TodoAddPageState();
}

class _TodoAddPageState extends State<TodoAddPage> {
  String _text = "";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("リスト追加"),
      ),
      body: Container(
        padding: EdgeInsets.all(64),
        child: Column(
          children: <Widget>[
            Text(_text, style: TextStyle(color: Colors.blue)),
            const SizedBox(height:8),
            TextField(
              onChanged: (String val){
                setState((){
                  _text = val;
                });
              }
            ),
            const SizedBox(height:8),
            Container(
              width: double.infinity,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  primary: Colors.blue,
                ),
                onPressed: (){
                  Navigator.of(context).pop(_text);
                },
                child: Text("リスト追加")
              ),
            ),
            const SizedBox(height: 8),
            Container(
              width:double.infinity,
              child: TextButton(
                onPressed: (){
                  Navigator.of(context).pop();
                },
                child:Text("キャンセル")
              )
            ),
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text("リスト追加画面（クリックで戻る）")
            ),
          ],

      ),
    ),

    );
  }
}
