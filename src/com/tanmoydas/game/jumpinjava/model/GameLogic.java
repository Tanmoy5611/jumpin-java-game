package com.tanmoydas.game.jumpinjava.model;

/* Own pieces NEVER block path
   Opponent pieces block unless landing on own saucer
   Cup can overtake own cup / own saucer / own stack
   Saucer lands on FIRST empty tile only
   Stack does NOT move as a unit (cup moves off first)
 */
public final class GameLogic {

    private GameLogic() {}

    public static boolean isLegalMove(Board board, int from, int to, Player player) {
        if (from == to) return false;

        Tile fromTile = board.getTile(from);
        if (fromTile.isEmpty()) return false;

        if (!isForwardMove(from, to, player)) return false;

        int step = (player == Player.PLAYER_1) ? 1 : -1;

        // CUP move (cup alone OR cup on saucer)
        if (fromTile.getCup().isPresent()) {
            return isLegalCupMove(board, from, to, step, player);
        }

        // SAUCER move (only if no cup on top)
        if (fromTile.hasOnlySaucer()) {
            return isLegalSaucerMove(board, from, to, step);
        }

        return false;
    }

    private static boolean isForwardMove(int from, int to, Player player) {
        return (player == Player.PLAYER_1 && to > from)
                || (player == Player.PLAYER_2 && to < from);
    }

    // Cup move rules
    private static boolean isLegalCupMove(Board board, int from, int to, int step, Player player) {
        boolean opponentBlocked = false;

        for (int i = from + step; i != to + step; i += step) {
            Tile tile = board.getTile(i);

            if (i != to) {
                // ONLY opponent pieces affect blocking
                if (hasOpponentPiece(tile, player)) {
                    opponentBlocked = true;
                }
                // Own pieces NEVER block
            } else {
                // DESTINATION
                if (opponentBlocked) {
                    // Rescue jump ONLY onto own saucer
                    return tile.hasOnlySaucer() && tile.isSaucerOf(player);
                } else {
                    // Normal landing
                    return tile.isEmpty()
                            || (tile.hasOnlySaucer() && tile.isSaucerOf(player));
                }
            }
        }
        return false;
    }

    // Sauce move rules
    private static boolean isLegalSaucerMove(Board board, int from, int to, int step) {
        // Must land on FIRST empty tile
        for (int i = from + step; i >= 0 && i < 9; i += step) {
            if (board.getTile(i).isEmpty()) {
                return i == to;
            }
        }
        return false;
    }

    private static boolean hasOpponentPiece(Tile tile, Player player) {
        return (tile.getCup().isPresent() && tile.getCup().get().getOwner() != player)
                || (tile.getSaucer().isPresent() && tile.getSaucer().get().getOwner() != player);
    }
}