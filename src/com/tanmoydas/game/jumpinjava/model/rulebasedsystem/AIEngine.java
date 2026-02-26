package com.tanmoydas.game.jumpinjava.model.rulebasedsystem;

import com.tanmoydas.game.jumpinjava.model.GameSession;

public interface AIEngine {
    AIMove decideMove(GameSession session);
}