package com.tanmoydas.game.jumpinjava.model;

/* Represents a Cup piece in the game.
   A Cup is always movable, subject to game rules.
 */
public class Cup extends Piece {

    public Cup(Player owner) {
        super(owner);
    }

    @Override
    public boolean isMovable() {
        return true;
    }

    @Override
    public PieceType getType() {
        return PieceType.CUP;
    }
}