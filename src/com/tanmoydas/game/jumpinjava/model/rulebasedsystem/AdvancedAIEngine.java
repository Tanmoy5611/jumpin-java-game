package com.tanmoydas.game.jumpinjava.model.rulebasedsystem;

import com.tanmoydas.game.jumpinjava.model.*;

public class AdvancedAIEngine implements AIEngine {

    private static final Player AI = Player.PLAYER_2;
    private static final Player HUMAN = Player.PLAYER_1;
    private static final int[] FINAL_TILES = {0, 1};

    public AIMove decideMove(GameSession session) {
        Board board = session.getBoard();

        // 1. Win immediately if possible
        AIMove win = tryWinningMove(board);
        if (win != null) return win;

        // 2.  Block human final tile access
        AIMove block = blockHumanFinalTiles(board);
        if (block != null) return block;

        // 3. Secure final tiles with saucer
        AIMove saucerSetup = placeSaucerOnFinalTiles(board);
        if (saucerSetup != null) return saucerSetup;

        // 4. Safe cup advance (never suicidal)
        AIMove safeCup = safeCupAdvance(board);
        if (safeCup != null) return safeCup;

        // 5. Use saucer to block paths
        AIMove saucerBlock = blockingSaucerMove(board);
        if (saucerBlock != null) return saucerBlock;

        // 6. Any legal saucer move
        AIMove fallback = anySaucerMove(board);
        if (fallback != null) return fallback;

        // No legal move then draw
        return null;
    }

    // Winning move
    private AIMove tryWinningMove(Board board) {
        for (int from = 8; from >= 0; from--) {
            Tile tile = board.getTile(from);
            if (tile.isCupOf(AI)) {
                for (int to : FINAL_TILES) {
                    if (GameLogic.isLegalMove(board, from, to, AI)
                            && board.getTile(to).hasOnlySaucer()
                            && board.getTile(to).isSaucerOf(AI)) {
                        return new AIMove(from, to, AI);
                    }
                }
            }
        }
        return null;
    }

    // Block human final
    private AIMove blockHumanFinalTiles(Board board) {
        for (int hFrom = 0; hFrom < 9; hFrom++) {
            Tile humanTile = board.getTile(hFrom);
            if (!humanTile.isCupOf(HUMAN)) continue;

            for (int hTo = hFrom + 1; hTo < 9; hTo++) {
                if (!GameLogic.isLegalMove(board, hFrom, hTo, HUMAN)) continue;

                for (int aiFrom = 8; aiFrom >= 0; aiFrom--) {
                    Tile aiTile = board.getTile(aiFrom);
                    if (aiTile.hasOnlySaucer() && aiTile.isSaucerOf(AI)) {
                        if (GameLogic.isLegalMove(board, aiFrom, hTo, AI)) {
                            return new AIMove(aiFrom, hTo, AI);
                        }
                    }
                }
            }
        }
        return null;
    }

    // Final tile saucer placement
    private AIMove placeSaucerOnFinalTiles(Board board) {
        for (int from = 8; from >= 0; from--) {
            Tile tile = board.getTile(from);
            if (tile.hasOnlySaucer() && tile.isSaucerOf(AI)) {
                for (int to : FINAL_TILES) {
                    if (GameLogic.isLegalMove(board, from, to, AI)) {
                        return new AIMove(from, to, AI);
                    }
                }
            }
        }
        return null;
    }

    // Safe cup advance
    private AIMove safeCupAdvance(Board board) {
        for (int from = 8; from >= 0; from--) {
            Tile tile = board.getTile(from);
            if (!tile.isCupOf(AI)) continue;

            for (int to = from - 1; to >= 0; to--) {
                if (!GameLogic.isLegalMove(board, from, to, AI)) continue;

                // Prevent Rule-13 auto-loss
                if ((to == 0 || to == 1) && board.getTile(to).isEmpty()) continue;

                return new AIMove(from, to, AI);
            }
        }
        return null;
    }

    // Saucer blocking
    private AIMove blockingSaucerMove(Board board) {
        for (int from = 8; from >= 0; from--) {
            Tile tile = board.getTile(from);
            if (!tile.hasOnlySaucer() || !tile.isSaucerOf(AI)) continue;

            for (int to = from - 1; to >= 0; to--) {
                if (!GameLogic.isLegalMove(board, from, to, AI)) continue;

                // Prefer central blocking positions
                if (to >= 2 && to <= 6) {
                    return new AIMove(from, to, AI);
                }
            }
        }
        return null;
    }

   // Fallback
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
}