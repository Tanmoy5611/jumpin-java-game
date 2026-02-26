package com.tanmoydas.game.jumpinjava.model;

import java.util.Optional;

/* Represents a single tile on the board.
   A tile may contain a Cup, a Saucer, or both (Cup stacked on Saucer).
 */

public class Tile {

    private final int position;

    private Cup cup;
    private Saucer saucer;

    public Tile(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public Optional<Cup> getCup() {
        return Optional.ofNullable(cup);
    }

    public Optional<Saucer> getSaucer() {
        return Optional.ofNullable(saucer);
    }

    // Placement operations

    public void placeCup(Cup cup) {
        this.cup = cup;
    }

    public void placeSaucer(Saucer saucer) {
        this.saucer = saucer;
    }

    public void removeCup() {
        this.cup = null;
    }

    public void removeSaucer() {
        this.saucer = null;
    }

    //  State queries

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

    public boolean isBlocked() {
        return cup != null || saucer != null;
    }

    //  Ownership & rule checks

    public boolean hasOpponentSaucer(Player player) {
        return hasOnlySaucer() && saucer.getOwner() != player;
    }

    public boolean canCupMoveTo(Player player) {
        return isEmpty() ||
                (hasOnlySaucer() && saucer.getOwner() == player);
    }

    public boolean isMovableSaucer(Player player) {
        return saucer != null &&
                saucer.getOwner() == player &&
                cup == null;
    }

    public boolean belongsTo(Player player) {
        return (cup != null && cup.getOwner() == player) ||
                (saucer != null && saucer.getOwner() == player);
    }

    public boolean isCupOf(Player player) {
        return cup != null && cup.getOwner() == player;
    }

    public boolean isSaucerOf(Player player) {
        return saucer != null && saucer.getOwner() == player;
    }
}