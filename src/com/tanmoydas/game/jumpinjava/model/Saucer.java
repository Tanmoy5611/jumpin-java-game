package com.tanmoydas.game.jumpinjava.model;

/* Represents a Saucer piece.
   A Saucer can only move when no Cup is stacked on top of it.
 */
public class Saucer extends Piece {

    public Saucer(Player owner) {
        super(owner);
    }

    /* A Saucer's movability depends on the board state.
       The actual check is handled by the Tile that contains this Saucer.
     */

    @Override
    public boolean isMovable() {
        return true;
    }

    @Override
    public PieceType getType() {
        return PieceType.SAUCER;
    }
}