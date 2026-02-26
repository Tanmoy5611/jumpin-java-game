package com.tanmoydas.game.jumpinjava.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Represents the game board
   The board owns all tiles and is responsible for their lifecycle
 */
public class Board {

    private static final int BOARD_SIZE = 9;

    private final List<Tile> tiles = new ArrayList<>();

    public Board() {
        initializeBoard();
    }

    /* Returns the tile at the given index.
     tile position (0 to BOARD_SIZE - 1)
     returns the Tile at the given position
     throws IndexOutOfBoundsException if index is invalid
     */
    public Tile getTile(int index) {
        if (index < 0 || index >= BOARD_SIZE) {
            throw new IndexOutOfBoundsException("Invalid tile index: " + index);
        }
        return tiles.get(index);
    }

    /* Returns an unmodifiable view of the board tiles
       This prevents external code from breaking board invariants
     */
    public List<Tile> getTiles() {
        return Collections.unmodifiableList(tiles);
    }

    // Resets the board to its initial empty state
    public void reset() {
        tiles.clear();
        initializeBoard();
    }

    // Initializes the board with empty tiles
    // Extracted to avoid duplication and keep construction logic centralized
    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            tiles.add(new Tile(i));
        }
    }
}