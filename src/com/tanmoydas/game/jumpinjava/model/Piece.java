package com.tanmoydas.game.jumpinjava.model;

/* Base class for all game pieces.
   A piece always belongs to a player and defines its own movement capability.
 */
public abstract class Piece {

    private final Player owner;

    protected Piece(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    // Indicates whether this piece is currently movable.
    public abstract boolean isMovable();

    // returns the type of piece
    public abstract PieceType getType();
}