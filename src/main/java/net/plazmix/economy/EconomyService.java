package net.plazmix.economy;

public interface EconomyService {

    int get(String playerName);

    void set(String playerName, int value);

    void add(String playerName, int value);

    void take(String playerName, int value);

    void multiply(String playerName, int value);

    void divide(String playerName, int value);

    void increment(String playerName);

    void decrement(String playerName);
}
