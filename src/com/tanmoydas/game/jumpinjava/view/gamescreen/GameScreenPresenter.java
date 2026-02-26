package com.tanmoydas.game.jumpinjava.view.gamescreen;

import com.tanmoydas.game.jumpinjava.data.statistics.StatisticsDAO;
import com.tanmoydas.game.jumpinjava.model.*;
import com.tanmoydas.game.jumpinjava.model.rulebasedsystem.AIEngine;
import com.tanmoydas.game.jumpinjava.model.rulebasedsystem.AIMove;
import com.tanmoydas.game.jumpinjava.model.statistics.StatisticsRecord;
import com.tanmoydas.game.jumpinjava.model.statistics.Winner;
import com.tanmoydas.game.jumpinjava.view.startscreen.StartScreenPresenter;
import com.tanmoydas.game.jumpinjava.view.startscreen.StartScreenView;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameScreenPresenter {

    private static final Player HUMAN = Player.PLAYER_1;
    private static final Player AI = Player.PLAYER_2;

    private static final int BOARD_SIZE = 9;
    private static final Duration AI_DELAY = Duration.millis(700);

    private final GameScreenView view;
    private final Stage stage;

    private final GameSession session;
    private final AIEngine aiEngine;

    private Integer selectedIndex = null;

    public GameScreenPresenter(GameScreenView view, Stage stage, AIEngine aiEngine) {
        this.view = view;
        this.stage = stage;
        this.session = new GameSession();
        this.aiEngine = aiEngine;

        setupInitialBoard();
        wireBackButton();
        wireTiles();
        refreshAll();

        // If HUMAN has no moves at start (unlikely) -> draw
        if (!hasAnyValidMove(HUMAN)) endAsDraw();
    }

    // Initial board setup

    private void setupInitialBoard() {
        Board board = session.getBoard();

        placeStack(board.getTile(0), HUMAN);
        placeStack(board.getTile(1), HUMAN);

        placeStack(board.getTile(7), AI);
        placeStack(board.getTile(8), AI);
    }

    private void placeStack(Tile tile, Player owner) {
        tile.placeSaucer(new Saucer(owner));
        tile.placeCup(new Cup(owner));
    }

    //  Configures back button to return to start screen
    private void wireBackButton() {
        view.getBackButton().setOnAction(e -> {
            StartScreenView startView = new StartScreenView(stage);
            new StartScreenPresenter(startView, stage);
            stage.setScene(new Scene(startView.getRoot(), 800, 600));
        });
    }

    // wire tile clicks
    private void wireTiles() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            int index = i;
            getTileView(index).setOnMouseClicked(e -> onTileClicked(index));
        }
    }

    // Human turn

    private void onTileClicked(int index) {
        if (session.hasWinner()) return;
        if (session.getCurrentPlayer() != HUMAN) return;

        Tile clicked = session.getBoard().getTile(index);

        // select
        if (selectedIndex == null) {
            if (clicked.isCupOf(HUMAN) || clicked.isSaucerOf(HUMAN)) {
                selectedIndex = index;
                refreshAll();
            } else {
                view.animateIllegalMove(getTileView(index));
            }
            return;
        }

        int fromIndex = selectedIndex;
        int toIndex = index;

        if (fromIndex == toIndex) {
            selectedIndex = null;
            refreshAll();
            return;
        }

        if (!GameLogic.isLegalMove(session.getBoard(), fromIndex, toIndex, HUMAN)) {
            view.animateIllegalMove(getTileView(fromIndex));
            selectedIndex = null;
            refreshAll();
            return;
        }

        doMove(fromIndex, toIndex);

        view.highlightMove(getTileView(fromIndex), getTileView(toIndex));
        view.addMoveToHistory("🔴 " + fromIndex + " → " + toIndex);

        selectedIndex = null;
        refreshAll();

        // auto-loss check (Rule 13)
        if (session.isAutoLoss(HUMAN, toIndex)) {
            session.setWinner(AI);
            finishGameWithWinner(AI);
            return;
        }

        // win check (Rule 12)
        session.isGameOver();
        if (session.hasWinner()) {
            finishGameWithWinner(session.getWinner());
            return;
        }

        // DRAW rule: if AI has no valid moves now -> draw
        if (!hasAnyValidMove(AI)) {
            endAsDraw();
            return;
        }

        // switch to AI
        session.switchPlayer();
        updateTurnLabel();
        performAiTurn();
    }

    // AI turn with delay

    private void performAiTurn() {
        view.showAIThinking(true);

        PauseTransition delay = new PauseTransition(AI_DELAY);
        delay.setOnFinished(e -> {
            view.showAIThinking(false);

            AIMove move = aiEngine.decideMove(session);

            // If AI cannot move -> DRAW
            if (move == null) {
                endAsDraw();
                return;
            }

            if (!GameLogic.isLegalMove(session.getBoard(), move.getFromIndex(), move.getToIndex(), AI)) {
                // invalid AI move -> treat as draw (safe)
                endAsDraw();
                return;
            }

            int from = move.getFromIndex();
            int to = move.getToIndex();

            doMove(from, to);

            view.highlightMove(getTileView(from), getTileView(to));
            view.addMoveToHistory("🤖 " + from + " → " + to);

            refreshAll();

            // auto-loss check for AI move (Rule 13)
            if (session.isAutoLoss(AI, to)) {
                session.setWinner(HUMAN);
                finishGameWithWinner(HUMAN);
                return;
            }

            // win check (Rule 12)
            session.isGameOver();
            if (session.hasWinner()) {
                finishGameWithWinner(session.getWinner());
                return;
            }

            // DRAW rule: if HUMAN has no moves now -> draw
            if (!hasAnyValidMove(HUMAN)) {
                endAsDraw();
                return;
            }

            // switch back to human
            session.switchPlayer();
            updateTurnLabel();
        });

        delay.play();
    }

   // Execute move (Rule 8 implemented here)

    private void doMove(int fromIndex, int toIndex) {
        Tile from = session.getBoard().getTile(fromIndex);
        Tile to = session.getBoard().getTile(toIndex);

        // Rule 8: if there's a cup on top, only the CUP moves.
        // Saucer stays behind and becomes free.
        if (from.getCup().isPresent()) {
            Cup cup = from.getCup().get();
            from.removeCup();          // leaves saucer behind if present
            to.placeCup(cup);          // can stack if destination has own saucer (GameLogic ensures)

        } else if (from.hasOnlySaucer()) {
            Saucer saucer = from.getSaucer().get();
            from.removeSaucer();
            to.placeSaucer(saucer);
        }

        session.recordMove(fromIndex, toIndex);

        // visuals
        renderTile(fromIndex);

        StackPane dest = getTileView(toIndex);
        dest.getChildren().clear();
        to.getSaucer().ifPresent(s -> dest.getChildren().add(createSaucerNode(s)));

        if (to.getCup().isPresent()) {
            view.animatePieceDrop(dest, createCupNode(to.getCup().get()));
        } else if (to.hasOnlySaucer()) {
            view.animatePieceDrop(dest, createSaucerNode(to.getSaucer().get()));
        }
    }

    //  Draw & finish

    private void endAsDraw() {
        session.endGame();
        view.showWinOverlay("🤝 Draw!");
        wireEndGameButtons();
        view.getTurnLabel().setText("🏁 Game Over – 🤝 Draw!");
        saveStatisticsDraw();
    }

    private void finishGameWithWinner(Player winner) {
        session.endGame();

        String msg = (winner == HUMAN) ? "🏆 Player Wins!" : "🤖 AI Wins!";
        view.showWinOverlay(msg);
        wireEndGameButtons();

        view.getTurnLabel().setText(
                winner == HUMAN
                        ? "🏁 Game Over – 🔴 Player Wins!"
                        : "🏁 Game Over – 🤖 AI Wins!"
        );

        saveStatistics(winner);
    }

    // Valid move check (for DRAW rule)
    private boolean hasAnyValidMove(Player player) {
        Board board = session.getBoard();

        for (int from = 0; from < BOARD_SIZE; from++) {
            Tile fromTile = board.getTile(from);

            // selectable if it has a cup OR has a saucer-only
            boolean movable =
                    fromTile.isCupOf(player) ||
                            (fromTile.hasOnlySaucer() && fromTile.isSaucerOf(player));

            if (!movable) continue;

            for (int to = 0; to < BOARD_SIZE; to++) {
                if (GameLogic.isLegalMove(board, from, to, player)) {
                    return true;
                }
            }
        }
        return false;
    }

   // Refresh UI
    private void refreshAll() {
        updateBoardView();
        updateTurnLabel();
    }

    // update UI
    private void updateBoardView() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            renderTile(i);
        }

        if (selectedIndex != null) {
            getTileView(selectedIndex).setStyle("-fx-border-color: #04f8e1; -fx-border-width: 3;");
        }
    }

    private void renderTile(int index) {
        StackPane tileView = getTileView(index);
        tileView.getChildren().clear();

        Tile tile = session.getBoard().getTile(index);
        tile.getSaucer().ifPresent(s -> tileView.getChildren().add(createSaucerNode(s)));
        tile.getCup().ifPresent(c -> tileView.getChildren().add(createCupNode(c)));
    }

    private void updateTurnLabel() {
        if (session.hasWinner()) {
            view.getTurnLabel().setText(
                    session.getWinner() == HUMAN
                            ? "🏁 Game Over – 🔴 Player Wins!"
                            : "🏁 Game Over – 🤖 AI Wins!"
            );
            return;
        }

        view.getTurnLabel().setText(
                session.getCurrentPlayer() == HUMAN
                        ? "🔴 Player Turn"
                        : "🤖 AI Turn"
        );
    }


    private void wireEndGameButtons() {
        view.getPlayAgainButton().setOnAction(e -> {
            GameScreenView newView = new GameScreenView(stage);
            new GameScreenPresenter(newView, stage, aiEngine);
            stage.setScene(new Scene(newView.getRoot(), 1000, 700));
        });

        view.getBackToMenuButton().setOnAction(e -> {
            StartScreenView startView = new StartScreenView(stage);
            new StartScreenPresenter(startView, stage);
            stage.setScene(new Scene(startView.getRoot(), 800, 600));
        });
    }



     // Save Statistics
    private void saveStatistics(Player winner) {
        try {
            StatisticsRecord record = buildStatisticsRecord(winner);
            new StatisticsDAO().saveGame(record);
        } catch (Exception e) {
            System.out.println("Could not save statistics: " + e.getMessage());
        }
    }

    private void saveStatisticsDraw() {
        try {
            StatisticsRecord record = buildStatisticsRecordDraw();
            new StatisticsDAO().saveGame(record);
        } catch (Exception e) {
            System.out.println("Could not save statistics: " + e.getMessage());
        }
    }

    // build statistics record for PLAYER or AI
    private StatisticsRecord buildStatisticsRecord(Player winnerOverride) {
        List<MoveRecord> moves = session.getMoveHistory();
        List<java.time.Duration> durations = new ArrayList<>();

        LocalDateTime start = session.getStartTime();
        LocalDateTime prev = start;

        for (MoveRecord m : moves) {
            java.time.Duration d = java.time.Duration.between(prev, m.getTimestamp());
            durations.add(d.isNegative() ? java.time.Duration.ZERO : d);
            prev = m.getTimestamp();
        }

        java.time.Duration avg = durations.isEmpty()
                ? java.time.Duration.ZERO
                : java.time.Duration.ofMillis(
                (long) durations.stream().mapToLong(java.time.Duration::toMillis).average().orElse(0)
        );

        return new StatisticsRecord(
                start,
                session.getGameDuration(),
                moves.size(),
                avg,
                winnerOverride == HUMAN ? Winner.PLAYER : Winner.AI,
                durations
        );
    }

    // build statistics record for DRAW
    private StatisticsRecord buildStatisticsRecordDraw() {
        List<MoveRecord> moves = session.getMoveHistory();
        List<java.time.Duration> durations = new ArrayList<>();

        LocalDateTime start = session.getStartTime();
        LocalDateTime prev = start;

        for (MoveRecord m : moves) {
            java.time.Duration d = java.time.Duration.between(prev, m.getTimestamp());
            durations.add(d.isNegative() ? java.time.Duration.ZERO : d);
            prev = m.getTimestamp();
        }

        java.time.Duration avg = durations.isEmpty()
                ? java.time.Duration.ZERO
                : java.time.Duration.ofMillis(
                (long) durations.stream().mapToLong(java.time.Duration::toMillis).average().orElse(0)
        );

        return new StatisticsRecord(
                start,
                session.getGameDuration(),
                moves.size(),
                avg,
                Winner.DRAW,
                durations
        );
    }

   // Helpers UI nodes
    private StackPane getTileView(int index) {
        return (StackPane) view.getBoard().getChildren().get(index);
    }

    private Rectangle createSaucerNode(Saucer saucer) {
        return new Rectangle(50, 15,
                saucer.getOwner() == HUMAN ? Color.ROYALBLUE : Color.HOTPINK);
    }

    private Circle createCupNode(Cup cup) {
        Circle c = new Circle(20,
                cup.getOwner() == HUMAN ? Color.FIREBRICK : Color.DARKSLATEGRAY);
        c.setTranslateY(-10);
        return c;
    }
}