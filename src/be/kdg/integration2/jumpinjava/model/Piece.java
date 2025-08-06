package be.kdg.integration2.jumpinjava.model;

public abstract class Piece {
    private final String owner; // "P1" or "P2"

    public Piece(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public abstract boolean isMovable(); // overridden in Cup/Saucer

    public abstract String getType(); // "Cup" or "Saucer"
}