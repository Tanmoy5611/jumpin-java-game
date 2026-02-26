package com.tanmoydas.game.jumpinjava.model.rulebasedsystem;

import com.tanmoydas.game.jumpinjava.model.*;

// For playing against the simple AI version of the game
public class SimpleAIEngine implements AIEngine {

    private static final Player AI = Player.PLAYER_2;
    private static final int[] FINAL_TILES = {0, 1};

    public AIMove decideMove(GameSession session) {
        Board board = session.getBoard();

        // 1. Place saucer on final tiles FIRST (mandatory strategy)
        AIMove saucerSetup = placeSaucerOnFinalTile(board);
        if (saucerSetup != null) return saucerSetup;

        // 2. Safe cup moves (NO auto-loss)
        AIMove safeCup = safeCupMove(board);
        if (safeCup != null) return safeCup;

        // 3. Any other saucer move
        AIMove saucerMove = anySaucerMove(board);
        if (saucerMove != null) return saucerMove;

        // 4. No legal move → DRAW
        return null;
    }

    // Strategies

    private AIMove placeSaucerOnFinalTile(Board board) {
        for (int from = 8; from >= 0; from--) {
            Tile tile = board.getTile(from);
            if (tile.hasOnlySaucer() && tile.isSaucerOf(AI)) {
                for (int target : FINAL_TILES) {
                    if (GameLogic.isLegalMove(board, from, target, AI)) {
                        return new AIMove(from, target, AI);
                    }
                }
            }
        }
        return null;
    }

    private AIMove safeCupMove(Board board) {
        for (int from = 8; from >= 0; from--) {
            Tile tile = board.getTile(from);
            if (tile.isCupOf(AI)) {
                for (int to = from - 1; to >= 0; to--) {

                    if (!GameLogic.isLegalMove(board, from, to, AI)) continue;

                    // Prevent suicidal move
                    if (causesAutoLoss(board, from, to)) continue;

                    return new AIMove(from, to, AI);
                }
            }
        }
        return null;
    }

    private AIMove anySaucerMove(Board board) {
        for (int from = 8; from >= 0; from--) {
            Tile tile = board.getTile(from);
            if (tile.hasOnlySaucer() && tile.isSaucerOf(AI)) {
                for (int to = from - 1; to >= 0; to--) {
                    if (GameLogic.isLegalMove(board, from, to, AI)) {
                        return new AIMove(from, to, AI);
                    }
                }
            }
        }
        return null;
    }

    // Auto loss simulation

    private boolean causesAutoLoss(Board board, int from, int to) {
        if (to != 0 && to != 1) return false;

        Tile destination = board.getTile(to);

        // SAFE: landing on own saucer
        if (destination.hasOnlySaucer() && destination.isSaucerOf(AI)) {
            return false;
        }

        // AUTO-LOSS: cup lands alone
        return destination.isEmpty();
    }
}