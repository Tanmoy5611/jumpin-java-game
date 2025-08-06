package be.kdg.integration2.jumpinjava.model;

import java.time.LocalDateTime;

public class MoveRecord {
    private final String player;
    private final int from;
    private final int to;
    private final LocalDateTime moveTime;

    public MoveRecord(String player, int from, int to, LocalDateTime moveTime) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.moveTime = moveTime;
    }

    public String getPlayer() {
        return player;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public LocalDateTime getTimestamp() {
        return moveTime;
    }
}