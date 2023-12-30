import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:horizontal_data_table/horizontal_data_table.dart';

import 'model/player.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // TRY THIS: Try running your application with "flutter run". You'll see
        // the application has a purple toolbar. Then, without quitting the app,
        // try changing the seedColor in the colorScheme below to Colors.green
        // and then invoke "hot reload" (save your changes or press the "hot
        // reload" button in a Flutter-supported IDE, or press "r" if you used
        // the command line to start the app).
        //
        // Notice that the counter didn't reset back to zero; the application
        // state is not lost during the reload. To reset the state, use hot
        // restart instead.
        //
        // This works for code too, not just values: Most code changes can be
        // tested with just a hot reload.
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(players: <Player>[]),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.players});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final List<Player> players;

  @override
  State<MyHomePage> createState() => _PlayersTableState();
}

class AddPlayerNotification extends Notification {
  final Player player;

  const AddPlayerNotification({required this.player});
}

class _PlayersTableState extends State<MyHomePage> {
  @override
  void dispose() {
    // Clean up the controller when the widget is disposed.
    playerInputControllers.forEach((key, value) {
      value.dispose();
    });
    super.dispose();
  }

  bool addPlayer(AddPlayerNotification addPlayerNotification) {
    log('#VR: Add player notification processed. $addPlayerNotification.player');
    setState(() {
      widget.players.add(addPlayerNotification.player);
    });
    return true;
  }

  final Map<String, TextEditingController> playerInputControllers = {
    'nickname': TextEditingController(),
    'firstname': TextEditingController(),
    'lastname': TextEditingController(),
    'position': TextEditingController(),
    'entry': TextEditingController(),
  };

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // TRY THIS: Try changing the color here to a specific color (to
        // Colors.amber, perhaps?) and trigger a hot reload to see the AppBar
        // change color while the other colors stay the same.
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text('Calculator'),
      ),
      body: NotificationListener<AddPlayerNotification>(
          onNotification: addPlayer,
          child: Center(
              // Center is a layout widget. It takes a single child and positions it
              // in the middle of the parent.
              child: HorizontalDataTable(
            leftHandSideColumnWidth: 100,
            rightHandSideColumnWidth: 300,
            isFixedHeader: true,
            headerWidgets: _createTitleWidget(),
            footerWidgets: _createFooterWidget(),
            isFixedFooter: true,
            rowSeparatorWidget: const Divider(
              color: Colors.black38,
              height: 1.0,
              thickness: 0.0,
            ),
            itemExtent: 55,
            leftSideItemBuilder: _generateFirstColumnRow,
            rightSideItemBuilder: _generateRightHandSideColumnRow,
            itemCount: widget.players.length,
          ))),
      floatingActionButton: FloatingActionButton(
        onPressed: () =>
            _displayPlayerInputDialog(playerInputControllers, context),
        tooltip: 'Add player',
        child: const Icon(Icons.add),
      ),
    );
  }

  List<Widget> _createTitleWidget() {
    return [
      _getTitleItemWidget('nickname', 100),
      _getTitleItemWidget('entries', 50),
      _getTitleItemWidget('position', 50),
      _getTitleItemWidget('firstname', 100),
      _getTitleItemWidget('lastname', 100),
    ];
  }

  List<Widget> _createFooterWidget() {
    return [
      Container(
        width: 100,
        height: 0,
        padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
        alignment: Alignment.centerLeft,
      ),
      Container(
        width: 50,
        height: 0,
        padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
        alignment: Alignment.centerLeft,
      ),
      Container(
        width: 50,
        height: 0,
        padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
        alignment: Alignment.centerLeft,
      ),
      Container(
        width: 100,
        height: 0,
        padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
        alignment: Alignment.centerLeft,
      ),
      Container(
        width: 100,
        height: 0,
        padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
        alignment: Alignment.centerLeft,
      ),
    ];
  }

  Widget _getTitleItemWidget(String label, double width) {
    return Container(
      width: width,
      height: 56,
      padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
      alignment: Alignment.centerLeft,
      child: Text(label, style: const TextStyle(fontWeight: FontWeight.bold)),
    );
  }

  Widget _generateFirstColumnRow(BuildContext context, int index) {
    return Container(
      width: 100,
      height: 52,
      padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
      alignment: Alignment.centerLeft,
      child: Text(widget.players[index].nickname),
    );
  }

  Widget _generateRightHandSideColumnRow(BuildContext context, int index) {
    return Row(
      children: <Widget>[
        Container(
          width: 200,
          height: 52,
          padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
          alignment: Alignment.centerLeft,
          child: Text(widget.players[index].entry as String),
        ),
        Container(
          width: 100,
          height: 52,
          padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
          alignment: Alignment.centerLeft,
          child: Text(widget.players[index].position as String),
        ),
        Container(
          width: 200,
          height: 52,
          padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
          alignment: Alignment.centerLeft,
          child: Text(widget.players[index].firstname),
        ),
        Container(
          width: 200,
          height: 52,
          padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
          alignment: Alignment.centerLeft,
          child: Text(widget.players[index].lastname),
        ),
      ],
    );
  }
}

Future<void> _displayPlayerInputDialog(
    Map<String, TextEditingController> playerInputControllers,
    BuildContext context) {
  Padding _createInput(String labelText, TextInputType textInputType,
      Map<String, TextEditingController> playerInputControllers) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 16),
      child: TextFormField(
        keyboardType: textInputType,
        controller: playerInputControllers[labelText],
        decoration: InputDecoration(
          border: UnderlineInputBorder(),
          labelText: labelText,
        ),
      ),
    );
  }

  Player _mapInputToPlayer(
      Map<String, TextEditingController> playerInputControllers) {
    return Player(
      // 1,
      // 2,
      int.parse(playerInputControllers['entry']!.value.text),
      int.parse(playerInputControllers['position']!.value.text),
      playerInputControllers['firstname']!.value.text,
      playerInputControllers['lastname']!.value.text,
      playerInputControllers['nickname']!.value.text,
    );
  }

  return showDialog<void>(
    context: context,
    builder: (BuildContext context) {
      return AlertDialog(
        title: const Text('Player data'),
        content: Container(
            child: SingleChildScrollView(
                child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            _createInput(
                'nickname', TextInputType.name, playerInputControllers),
            _createInput(
                'firstname', TextInputType.name, playerInputControllers),
            _createInput(
                'lastname', TextInputType.name, playerInputControllers),
            _createInput('entry', TextInputType.number, playerInputControllers),
            _createInput(
                'position', TextInputType.number, playerInputControllers),
          ],
        ))),
        actions: <Widget>[
          TextButton(
            style: TextButton.styleFrom(
              textStyle: Theme.of(context).textTheme.labelLarge,
            ),
            child: const Text('Cancel'),
            onPressed: () {
              Navigator.of(context).pop();
            },
          ),
          TextButton(
            style: TextButton.styleFrom(
              textStyle: Theme.of(context).textTheme.labelLarge,
            ),
            child: const Text('Add'),
            onPressed: () {
              var player = _mapInputToPlayer(playerInputControllers).nickname;
              log('#VR: Add player notification dispatched. $player');
              AddPlayerNotification(
                  player: _mapInputToPlayer(playerInputControllers))
                ..dispatch(context);
              Navigator.of(context).pop();
            },
          ),
        ],
      );
    },
  );
}
