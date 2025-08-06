package be.kdg.integration2.jumpinjava.model;
import be.kdg.integration2.jumpinjava.model.statistics.StatisticsRecord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class GameSession {
    private final Board board;
    private final String player1 = "P1";
    private final String player2 = "P2";
    private String currentPlayer;
    private final List<MoveRecord> moveHistory;
    private final LocalDateTime startTime;
    private int lastCupMovedIndex = -1;
    private LocalDateTime endTime;
    private String winner = null;

    public GameSession() {
        this.board = new Board();
        this.currentPlayer = player1;
        this.moveHistory = new ArrayList<>();
        this.startTime = LocalDateTime.now();
    }

    public StatisticsRecord toStatisticsRecord() {
        int moveCount = 0;
        long totalDurationMillis = 0;
        List<Long> moveDurations = new ArrayList<>();

        LocalDateTime prevTime = startTime;
        for (MoveRecord move : moveHistory) {
            Duration duration = Duration.between(prevTime, move.getTimestamp());
            moveDurations.add(duration.toMillis());
            totalDurationMillis += duration.toMillis();
            prevTime = move.getTimestamp();
            moveCount++;
        }

        long avgDuration = moveCount == 0 ? 0 : totalDurationMillis / moveCount;

        // Convert startTime (LocalDateTime) to Timestamp for DB
        Timestamp gameTimestamp = Timestamp.valueOf(startTime);

        // Convert winner to readable string
        String readableWinner;
        if ("P1".equals(winner)) {
            readableWinner = "Player";
        } else if ("P2".equals(winner)) {
            readableWinner = "AI";
        } else {
            readableWinner = winner; // fallback, e.g., null or other
        }

        return new StatisticsRecord(
                gameTimestamp,
                getGameDuration().toMillis(),
                moveCount,
                avgDuration,
                readableWinner,
                moveDurations
        );
    }

    public Board getBoard() {
        return board;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setLastCupMovedIndex(int index) {
        this.lastCupMovedIndex = index;
    }

    public int getLastCupMovedIndex() {
        return this.lastCupMovedIndex;
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
    }

    public void recordMove(int from, int to) {
        moveHistory.add(new MoveRecord(currentPlayer, from, to, LocalDateTime.now()));
    }

    //  Only current player can win during their own turn
    public boolean isGameOver() {
        return checkWin(currentPlayer);
    }

    public void setWinner(String player) {
        this.winner = player;
    }

    public String getWinner() {
        return this.winner;
    }
    private boolean checkWin(String player) {
        int[] endTiles = player.equals(player1) ? new int[]{7, 8} : new int[]{0, 1};
        int cupCount = 0;
        int saucerCount = 0;

        for (int i : endTiles) {
            Tile tile = board.getTile(i);
            if (tile.getCup().isPresent() && tile.getCup().get().getOwner().equals(player)) cupCount++;
            if (tile.getSaucer().isPresent() && tile.getSaucer().get().getOwner().equals(player)) saucerCount++;
        }

        return cupCount == 2 && saucerCount == 2;
    }

    // Win Condition: Both cups must sit on own saucers on win tiles
    public boolean checkWinCondition(String playerToCheck) {
        int[] winTiles = playerToCheck.equals("P1") ? new int[]{7, 8} : new int[]{0, 1};

        for (int index : winTiles) {
            Tile tile = board.getTile(index);
            if (!(tile.hasBoth() &&
                    tile.getCup().get().getOwner().equals(playerToCheck) &&
                    tile.getSaucer().get().getOwner().equals(playerToCheck))) {
                return false;
            }
        }
        return true;
    }

    public boolean isAutoLoss(String player, int destinationIndex) {
        int[] finalTiles = player.equals("P1") ? new int[]{7, 8} : new int[]{0, 1};

        for (int tileIndex : finalTiles) {
            if (destinationIndex == tileIndex) {
                Tile tile = board.getTile(tileIndex);
                boolean hasCup = tile.getCup().isPresent() && tile.getCup().get().getOwner().equals(player);
                boolean hasOwnSaucer = tile.getSaucer().isPresent() && tile.getSaucer().get().getOwner().equals(player);

                // Cup reached end but no saucer? Auto-loss!
                return hasCup && !hasOwnSaucer;
            }
        }
        return false;
    }

    public void endGame() {
        this.endTime = LocalDateTime.now();
    }

    public Duration getGameDuration() {
        if (endTime == null) return Duration.between(startTime, LocalDateTime.now());
        return Duration.between(startTime, endTime);
    }



    public List<MoveRecord> getMoveHistory() {
        return moveHistory;
    }
}

