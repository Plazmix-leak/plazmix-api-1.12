package net.plazmix.lobby.playertop.database;

import gnu.trove.map.TIntIntMap;

public interface PlayerTopsDatabase {

    TIntIntMap find(int limit);
}
