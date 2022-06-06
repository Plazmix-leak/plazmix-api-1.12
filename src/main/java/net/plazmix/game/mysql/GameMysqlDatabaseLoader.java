package net.plazmix.game.mysql;

public interface GameMysqlDatabaseLoader<V> {

    void handleLoad(V value) throws Exception;
}
