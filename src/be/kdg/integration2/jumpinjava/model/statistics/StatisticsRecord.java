package be.kdg.integration2.jumpinjava.model.statistics;

import java.util.List;
import java.sql.Timestamp;


public class StatisticsRecord {
    private Timestamp gameDate;  // change from String to Timestamp
    private long totalDuration;
    private int totalMoves;
    private long avgMoveDuration;
    private String winner;
    private List<Long> moveDurations;

    public StatisticsRecord(Timestamp gameDate, long totalDuration, int totalMoves, long avgMoveDuration, String winner, List<Long> moveDurations) {
        this.gameDate = gameDate;
        this.totalDuration = totalDuration;
        this.totalMoves = totalMoves;
        this.avgMoveDuration = avgMoveDuration;
        this.winner = winner;
        this.moveDurations = moveDurations;
    }

    public Timestamp getGameDate() {
        return gameDate;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public long getAvgMoveDuration() {
        return avgMoveDuration;
    }

    public String getWinner() {
        return winner;
    }

    public List<Long> getMoveDurations() {
        return moveDurations;
    }
}