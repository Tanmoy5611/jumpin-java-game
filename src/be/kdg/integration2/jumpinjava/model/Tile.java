package be.kdg.integration2.jumpinjava.model;

import java.util.Optional;

public class Tile {
    private final int position; // 0 to 8
    private Piece cup;
    private Piece saucer;

    public Tile(int position) {
        this.position = position;
    }

    // Create Getters
    public int getPosition() {
        return position;
    }

    public Optional<Piece> getCup() {
        return Optional.ofNullable(cup);
    }

    public Optional<Piece> getSaucer() {
        return Optional.ofNullable(saucer);
    }

    public void placeCup(Cup cup) {
        this.cup = cup;
        if (saucer instanceof Saucer) {
            ((Saucer) saucer).setHasCupOnTop(true);
        }
    }

    public void placeSaucer(Saucer saucer) {
        this.saucer = saucer;
    }

    public void removeCup() {
        this.cup = null;
        if (saucer instanceof Saucer) {
            ((Saucer) saucer).setHasCupOnTop(false);
        }
    }

    public void removeSaucer() {
        this.saucer = null;
    }

    public boolean isEmpty() {
        return cup == null && saucer == null;
    }

    public boolean hasOnlyCup() {
        return cup != null && saucer == null;
    }

    public boolean hasOnlySaucer() {
        return saucer != null && cup == null;
    }

    public boolean hasBoth() {
        return cup != null && saucer != null;
    }

    // Check if this tile has an opponent's saucer (to prevent cup landing)
    public boolean isOpponentSaucer(String currentPlayer) {
        return hasOnlySaucer() && saucer != null && !saucer.getOwner().equals(currentPlayer);
    }

    // Check if this tile is a valid landing spot for a cup (empty or own saucer)
    public boolean canCupMoveTo(String player) {
        return isEmpty() || (hasOnlySaucer() && saucer.getOwner().equals(player));
    }

    // Check if this saucer is movable (no cup on top)
    public boolean isMovableSaucer(String player) {
        return saucer != null &&
                saucer.getOwner().equals(player) &&
                (!((Saucer) saucer).hasCupOnTop());
    }

    // Check if this tile has any piece that belongs to player
    public boolean belongsTo(String player) {
        return (cup != null && cup.getOwner().equals(player)) ||
                (saucer != null && saucer.getOwner().equals(player));
    }

    // Check if this tile has an obstacle
    public boolean isBlocked() {
        return cup != null || saucer != null;
    }

    // Return owner's cup (if present)
    public boolean isCupOf(String player) {
        return cup != null && cup.getOwner().equals(player);
    }

    // Return owner's saucer (if present)
    public boolean isSaucerOf(String player) {
        return saucer != null && saucer.getOwner().equals(player);
    }
}