package be.kdg.integration2.jumpinjava.model;

public class Saucer extends Piece {
    private boolean hasCupOnTop;

    public Saucer(String owner) {
        super(owner);
        this.hasCupOnTop = false;
    }

    public boolean hasCupOnTop() {
        return hasCupOnTop;
    }

    public void setHasCupOnTop(boolean hasCupOnTop) {
        this.hasCupOnTop = hasCupOnTop;
    }

    @Override
    public boolean isMovable() {
        return !hasCupOnTop; // Saucer can only move if there's no cup on top
    }

    @Override
    public String getType() {
        return "Saucer";
    }
}