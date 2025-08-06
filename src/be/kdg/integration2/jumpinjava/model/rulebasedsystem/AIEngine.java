package be.kdg.integration2.jumpinjava.model.rulebasedsystem;

import be.kdg.integration2.jumpinjava.model.*;
import be.kdg.integration2.jumpinjava.model.GameLogic;

public class AIEngine {
    private final GameSession session;

    public AIEngine(GameSession session) {
        this.session = session;
        System.out.println("AI is ready...");
    }

    public boolean makeMove() {
        Board board = session.getBoard();

        System.out.println("AI is thinking...");

        if (tryWin(board)) return true;
        if (tryCupMoves(board)) return true;
        if (trySaucerMoves(board)) return true;
        if (tryFallbackMove(board)) return true; // New fallback

        System.out.println("No valid AI moves.");
        return false;
    }

    private boolean tryFallbackMove(Board board) {
        System.out.println(" AI using fallback strategy...");

        for (int i = 8; i >= 0; i--) {
            Tile from = board.getTile(i);
            if (from.getCup().isPresent() && from.getCup().get().getOwner().equals("P2")) {
                for (int j = i - 1; j >= 0; j--) {
                    Tile to = board.getTile(j);

                    if (GameLogic.isLegalMove(from, to, i, j, "P2", board)) {
                        // Skip risky auto-loss moves
                        if ((j == 0 || j == 1) && to.isEmpty()) continue;

                        return moveCup(from, to, i, j);
                    }
                }
            }
        }

        // Try saucer fallback if no cup fallback possible
        for (int i = 8; i >= 0; i--) {
            Tile from = board.getTile(i);
            if (from.getSaucer().isPresent()
                    && from.getSaucer().get().getOwner().equals("P2")
                    && !((Saucer) from.getSaucer().get()).hasCupOnTop()) {

                for (int j = i - 1; j >= 0; j--) {
                    Tile to = board.getTile(j);
                    if (GameLogic.isLegalMove(from, to, i, j, "P2", board)) {
                        to.placeSaucer((Saucer) from.getSaucer().get());
                        from.removeSaucer();
                        session.recordMove(i, j);
                        session.switchPlayer();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean tryWin(Board board) {
        System.out.println("AI trying to WIN...");

        for (int i = 8; i >= 0; i--) {
            Tile from = board.getTile(i);
            if (from.getCup().isPresent() && from.getCup().get().getOwner().equals("P2")) {
                for (int j = i - 1; j >= 0; j--) {
                    Tile to = board.getTile(j);

                    if (GameLogic.isLegalMove(from, to, i, j, "P2", board)) {
                        if ((j == 0 || j == 1) &&
                                to.hasOnlySaucer() &&
                                to.getSaucer().get().getOwner().equals("P2")) {
                            return moveCup(from, to, i, j);
                        }
                    }
                }
            }
        }
        return false;
    }


    private boolean tryCupMoves(Board board) {
        System.out.println(" AI trying cup moves...");

        for (int i = 8; i >= 0; i--) {
            Tile from = board.getTile(i);
            if (from.getCup().isPresent() && from.getCup().get().getOwner().equals("P2")) {
                for (int j = i - 1; j >= 0; j--) {
                    Tile to = board.getTile(j);

                    if (GameLogic.isLegalMove(from, to, i, j, "P2", board)) {
                        return moveCup(from, to, i, j);
                    }
                }
            }
        }
        return false;
    }

    private boolean trySaucerMoves(Board board) {
        System.out.println(" AI trying saucer moves...");

        for (int i = 8; i >= 0; i--) {
            Tile from = board.getTile(i);
            if (from.getSaucer().isPresent()
                    && from.getSaucer().get().getOwner().equals("P2")
                    && !((Saucer) from.getSaucer().get()).hasCupOnTop()) {

                for (int j = i - 1; j >= 0; j--) {
                    Tile to = board.getTile(j);

                    // Use shared rule logic
                    if (GameLogic.isLegalMove(from, to, i, j, "P2", board)) {
                        to.placeSaucer((Saucer) from.getSaucer().get());
                        from.removeSaucer();
                        session.recordMove(i, j);
                        session.switchPlayer();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean moveCup(Tile from, Tile to, int fromIndex, int toIndex) {
        //  Smart blocking check — avoid stacking unless winning or escaping danger
        boolean isFinalTile = (toIndex == 0 || toIndex == 1);
        boolean isOwnSaucer = to.hasOnlySaucer() && to.getSaucer().get().getOwner().equals("P2");

        if (isFinalTile && to.isEmpty()) {
            System.out.println("Avoiding auto-loss move to final tile " + toIndex);
            return false;
        }

        if (isOwnSaucer) {
            // Allow only if we’re close to winning or stuck
            int saucerCount = countFreeSaucers("P2");
            if (saucerCount == 0 || isFinalTile) {
                System.out.println("Stacking on own saucer at " + toIndex);
            } else {
                System.out.println("Skipping early stacking on own saucer at " + toIndex);
                return false;
            }
        }

        to.placeCup((Cup) from.getCup().get());
        from.removeCup();
        if (from.getSaucer().isPresent()) {
            ((Saucer) from.getSaucer().get()).setHasCupOnTop(false);
        }
        session.recordMove(fromIndex, toIndex);
        session.switchPlayer();
        return true;
    }

    private boolean isForward(int fromIndex, int toIndex) {
        return toIndex < fromIndex; // P2 moves right to left
    }

    private int countFreeSaucers(String player) {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            Tile tile = session.getBoard().getTile(i);
            if (tile.hasOnlySaucer() && tile.getSaucer().get().getOwner().equals(player)) {
                count++;
            }
        }
        return count;
    }
}
