package net.plazmix.scoreboard;

import java.util.Collection;

public interface ScoreboardDisplayAnimation {

    Collection<String> getDisplayAnimation();

    String getCurrentDisplay();

    void nextDisplay();
}
