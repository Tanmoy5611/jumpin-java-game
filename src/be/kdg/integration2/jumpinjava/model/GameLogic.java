package be.kdg.integration2.jumpinjava.model;

public class GameLogic {
    public static boolean isLegalMove(Tile from, Tile to, int fromIndex, int toIndex, String player, Board board) {
        // Direction check: must move forward only
        if ((player.equals("P1") && toIndex <= fromIndex) || (player.equals("P2") && toIndex >= fromIndex)) {
            return false;
        }

        int step = player.equals("P1") ? 1 : -1;

        // CASE 1: CUP ONLY
        if (from.getCup().isPresent() && !from.getSaucer().isPresent()) {
            String currentPlayer = player;
            boolean blocked = false;

            for (int i = fromIndex + step; (player.equals("P1") ? i <= toIndex : i >= toIndex); i += step) {
                Tile current = board.getTile(i);

                if (i != toIndex) {
                    // Block only if opponent pieces
                    boolean opponentCup = current.getCup().isPresent() &&
                            !current.getCup().get().getOwner().equals(currentPlayer);
                    boolean opponentSaucer = current.getSaucer().isPresent() &&
                            !current.getSaucer().get().getOwner().equals(currentPlayer);

                    if (opponentCup || opponentSaucer) {
                        blocked = true;
                    }
                    // Skip own pieces — do not block
                } else {
                    // At destination
                    if (!blocked) {
                        // Legal if empty or own saucer
                        return current.isEmpty() ||
                                (current.hasOnlySaucer() &&
                                        current.getSaucer().get().getOwner().equals(currentPlayer));
                    } else {
                        // If blocked, can only land if it's own saucer
                        return current.hasOnlySaucer() &&
                                current.getSaucer().get().getOwner().equals(currentPlayer);
                    }
                }
            }
            return false;
        }

        // CASE 2: SAUCER ONLY
        if (from.getSaucer().isPresent() && !from.getCup().isPresent()) {
            for (int i = fromIndex + step; (player.equals("P1") ? i <= 8 : i >= 0); i += step) {
                Tile current = board.getTile(i);

                if (current.isEmpty()) {
                    return i == toIndex; // Land only on first empty
                }
                // Saucer can pass anything
            }
            return false;
        }

        // CASE 3: CUP + SAUCER STACK
        if (from.hasBoth()) {
            boolean blocked = false;

            for (int i = fromIndex + step; i != toIndex + step; i += step) {
                Tile current = board.getTile(i);

                if (i != toIndex) {
                    // Block only if opponent pieces
                    boolean opponentCup = current.getCup().isPresent() &&
                            !current.getCup().get().getOwner().equals(player);
                    boolean opponentSaucer = current.getSaucer().isPresent() &&
                            !current.getSaucer().get().getOwner().equals(player);

                    if (opponentCup || opponentSaucer) {
                        blocked = true;
                    }
                } else {
                    // At destination
                    if (blocked) {
                        return current.hasOnlySaucer() &&
                                current.getSaucer().get().getOwner().equals(player);
                    } else {
                        return current.isEmpty() ||
                                (current.hasOnlySaucer() &&
                                        current.getSaucer().get().getOwner().equals(player));
                    }
                }
            }
            return false;
        }

        return false;
    }
}