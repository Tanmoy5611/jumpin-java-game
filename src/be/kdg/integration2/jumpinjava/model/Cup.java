package be.kdg.integration2.jumpinjava.model;

public class Cup extends Piece {
    public Cup(String owner) {
        super(owner);
    }

    @Override
    public boolean isMovable() {
        return true; // A cup can move independently (if legal)
    }

    @Override
    public String getType() {
        return "Cup";
    }
}