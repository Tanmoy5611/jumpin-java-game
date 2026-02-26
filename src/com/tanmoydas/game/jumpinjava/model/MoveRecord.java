package com.tanmoydas.game.jumpinjava.model;

import java.time.LocalDateTime;

// Immutable value object representing a single move in the game
public class MoveRecord {

    private final Player player;
    private final int fromPosition;
    private final int toPosition;
    private final LocalDateTime timestamp;

    public MoveRecord(Player player, int fromPosition, int toPosition, LocalDateTime timestamp) {
        this.player = player;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.timestamp = timestamp;
    }

    public Player getPlayer() {
        return player;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}