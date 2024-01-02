import 'dart:collection';

import 'package:flutter/cupertino.dart';

import 'player.dart';

class PlayersList extends ChangeNotifier {
  final List<Player> _players = [];

  UnmodifiableListView<Player> get items => UnmodifiableListView(_players);

  void add(Player player) {
    _players.add(player);
    notifyListeners();
  }
}
