package be.kdg.integration2.jumpinjava.model.rulebasedsystem;

public class Fact {
    private final String type;     // Example: "CupAt", "TileEmpty", "CanMove"
    private final int position;    // Tile index (0–8)
    private final String owner;    // "P1" or "P2"

    public Fact(String type, int position, String owner) {
        this.type = type;
        this.position = position;
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return type + "(" + position + ", " + owner + ")";
    }
}