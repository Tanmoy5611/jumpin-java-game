package be.kdg.integration2.jumpinjava.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final List<Tile> tiles;

    public Board() {
        tiles = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            tiles.add(new Tile(i)); // 0 to 8 = coasters 1 to 9
        }
    }

    public Tile getTile(int index) {
        if (index < 0 || index >= tiles.size()) throw new IndexOutOfBoundsException("Invalid tile index");
        return tiles.get(index);
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void resetBoard() {
        tiles.clear();
        for (int i = 0; i < 9; i++) {
            tiles.add(new Tile(i));
        }
    }

    public void printBoardState() {
        for (Tile tile : tiles) {
            System.out.print("[" + (tile.getCup().map(c -> "C").orElse(" ")) +
                    (tile.getSaucer().map(s -> "S").orElse(" ")) + "] ");
        }
        System.out.println();
    }
}