package com.tanmoydas.game.jumpinjava.model.rulebasedsystem;

import com.tanmoydas.game.jumpinjava.model.Player;

// Represents a single fact about the game state, used by the rule-based AI system.

public class Fact {

    private final FactType type;
    private final int position;
    private final Player owner;

    public Fact(FactType type, int position, Player owner) {
        this.type = type;
        this.position = position;
        this.owner = owner;
    }

    public FactType getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public Player getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return type + "(" + position + ", " + owner + ")";
    }
}