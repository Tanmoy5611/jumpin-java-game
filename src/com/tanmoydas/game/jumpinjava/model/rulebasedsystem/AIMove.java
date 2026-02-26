package com.tanmoydas.game.jumpinjava.model.rulebasedsystem;

import com.tanmoydas.game.jumpinjava.model.Player;

public class AIMove {
    private final int fromIndex;
    private final int toIndex;
    private final Player player;

    public AIMove(int fromIndex, int toIndex, Player player) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.player = player;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public Player getPlayer() {
        return player;
    }
}