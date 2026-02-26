package com.tanmoydas.game.jumpinjava.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Represents a single game session.
   Owns the board, turn state, move history, timing, and win conditions.
 */
public class GameSession {

    private final Board board;
    private Player currentPlayer;

    private final List<MoveRecord> moveHistory = new ArrayList<>();

    private final LocalDateTime startTime;
    private LocalDateTime endTime;

    private Player winner;

    public GameSession() {
        this.board = new Board();
        this.currentPlayer = Player.PLAYER_1;
        this.startTime = LocalDateTime.now();
    }

    // Core state access

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean hasWinner() {
        return winner != null;
    }

    // Turn & move handling

    public void switchPlayer() {
        currentPlayer =
                (currentPlayer == Player.PLAYER_1)
                        ? Player.PLAYER_2
                        : Player.PLAYER_1;
    }

    // Move history
    public void recordMove(int fromPosition, int toPosition) {
        moveHistory.add(
                new MoveRecord(
                        currentPlayer,
                        fromPosition,
                        toPosition,
                        LocalDateTime.now()
                )
        );
    }

    public List<MoveRecord> getMoveHistory() {
        return Collections.unmodifiableList(moveHistory);
    }

    // Game progress & timing
    public void endGame() {
        this.endTime = LocalDateTime.now();
    }

    public Duration getGameDuration() {
        return (endTime == null)
                ? Duration.between(startTime, LocalDateTime.now())
                : Duration.between(startTime, endTime);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    // Win conditions

    /* Evaluates the game-over condition AFTER a move
       Must be called explicitly by the presenter
     */
    public boolean isGameOver() {
        // Prevent win on initial setup
        if (moveHistory.size() < 2) {
            return false;
        }

        if (checkWinCondition(Player.PLAYER_1)) {
            winner = Player.PLAYER_1;
            return true;
        }

        if (checkWinCondition(Player.PLAYER_2)) {
            winner = Player.PLAYER_2;
            return true;
        }

        return false;
    }

    /* A player wins if BOTH of their stacks (Cup + Saucer)
       are present on their target tiles
     */
    private boolean checkWinCondition(Player player) {
        int[] winTiles = (player == Player.PLAYER_1)
                ? new int[]{7, 8}
                : new int[]{0, 1};

        for (int index : winTiles) {
            Tile tile = board.getTile(index);

            if (!tile.hasBoth()
                    || !tile.isCupOf(player)
                    || !tile.isSaucerOf(player)) {
                return false;
            }
        }
        return true;
    }

   // Auto-loss rule

    /* A player loses if their Cup reaches the final tile
        without their Saucer.
     */
    public boolean isAutoLoss(Player player, int destinationIndex) {
        int[] finalTiles = (player == Player.PLAYER_1)
                ? new int[]{7, 8}
                : new int[]{0, 1};

        for (int tileIndex : finalTiles) {
            if (destinationIndex == tileIndex) {
                Tile tile = board.getTile(tileIndex);
                return tile.isCupOf(player) && !tile.isSaucerOf(player);
            }
        }
        return false;
    }

    public void setWinner(Player winner) { this.winner = winner; }
    public boolean isDraw() { return winner == null && endTime != null; }
}