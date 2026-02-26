package com.tanmoydas.game.jumpinjava.model.rulebasedsystem;

import com.tanmoydas.game.jumpinjava.model.Board;
import com.tanmoydas.game.jumpinjava.model.Player;

/* Context passed to rule actions
  Contains all information an action needs to execute safely
 */
public class ActionContext {

    private final Board board;
    private final Player player;

    public ActionContext(Board board, Player player) {
        this.board = board;
        this.player = player;
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer() {
        return player;
    }
}