package com.tanmoydas.game.jumpinjava.model.statistics;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Immutable statistics snapshot of a finished game session.
 * This class is a DTO and does not contain game logic.
 */
public class StatisticsRecord {

    private final LocalDateTime gameStartTime;
    private final Duration totalDuration;
    private final int totalMoves;
    private final Duration averageMoveDuration;
    private final Winner winner;
    private final List<Duration> moveDurations;

    public StatisticsRecord(
            LocalDateTime gameStartTime,
            Duration totalDuration,
            int totalMoves,
            Duration averageMoveDuration,
            Winner winner,
            List<Duration> moveDurations
    ) {
        this.gameStartTime = gameStartTime;
        this.totalDuration = totalDuration;
        this.totalMoves = totalMoves;
        this.averageMoveDuration = averageMoveDuration;
        this.winner = winner;
        this.moveDurations = List.copyOf(moveDurations);
    }

    // Getters
    public LocalDateTime getGameStartTime() {
        return gameStartTime;
    }

    public Duration getTotalDuration() {
        return totalDuration;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public Duration getAverageMoveDuration() {
        return averageMoveDuration;
    }

    public Winner getWinner() {
        return winner;
    }

    public List<Duration> getMoveDurations() {
        return moveDurations;
    }
}