package be.kdg.integration2.jumpinjava.view.gamescreen;

import be.kdg.integration2.jumpinjava.model.*;
import be.kdg.integration2.jumpinjava.model.rulebasedsystem.AIEngine;
import be.kdg.integration2.jumpinjava.model.GameLogic;
import be.kdg.integration2.jumpinjava.view.startscreen.StartScreenPresenter;
import be.kdg.integration2.jumpinjava.view.startscreen.StartScreenView;
import be.kdg.integration2.jumpinjava.model.statistics.StatisticsDAO;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameScreenPresenter {
    private final GameScreenView view;
    private final GameSession session;
    private Tile selectedTile = null;
    private final AIEngine ai;
    private static final String WOOD_COLOR = "#deb887"; // burlywood (wood-like color)

    public GameScreenPresenter(GameScreenView view, Stage stage) {
        this.view = view;
        this.session = new GameSession();
        this.ai = new AIEngine(session);

        Board board = session.getBoard();

        // Player 1 setup (Red - Left Side)
        Saucer p1Saucer1 = new Saucer("P1");
        Cup p1Cup1 = new Cup("P1");
        p1Saucer1.setHasCupOnTop(true);
        board.getTile(0).placeSaucer(p1Saucer1);
        board.getTile(0).placeCup(p1Cup1);

        Saucer p1Saucer2 = new Saucer("P1");
        Cup p1Cup2 = new Cup("P1");
        p1Saucer2.setHasCupOnTop(true);
        board.getTile(1).placeSaucer(p1Saucer2);
        board.getTile(1).placeCup(p1Cup2);

        // Player 2 setup (Gray - Right Side)
        Saucer p2Saucer1 = new Saucer("P2");
        Cup p2Cup1 = new Cup("P2");
        p2Saucer1.setHasCupOnTop(true);
        board.getTile(7).placeSaucer(p2Saucer1);
        board.getTile(7).placeCup(p2Cup1);

        Saucer p2Saucer2 = new Saucer("P2");
        Cup p2Cup2 = new Cup("P2");
        p2Saucer2.setHasCupOnTop(true);
        board.getTile(8).placeSaucer(p2Saucer2);
        board.getTile(8).placeCup(p2Cup2);
        updateBoardView();
        updateTurnLabel();

        view.getBackButton().setOnAction(e -> {
            StartScreenView startView = new StartScreenView(stage);
            new StartScreenPresenter(startView, stage);
            stage.setScene(new Scene(startView.getRoot(), 800, 600));
        });

        for (int i = 0; i < view.getBoard().getChildren().size(); i++) {
            final int index = i;
            StackPane tileView = (StackPane) view.getBoard().getChildren().get(i);

            tileView.setOnMouseClicked(e -> {
                if (!session.getCurrentPlayer().equals("P1") || session.isGameOver()) return;

                Tile clickedTile = session.getBoard().getTile(index);

                if (selectedTile == null) {
                    if (!clickedTile.isEmpty() && clickedTile.belongsTo("P1")) {
                        selectedTile = clickedTile;
                        tileView.setStyle("-fx-border-color: #04f8e1; -fx-border-width: 3;");
                    }
                } else {
                    int fromIndex = selectedTile.getPosition();
                    int toIndex = clickedTile.getPosition();
                    Tile fromTile = selectedTile;
                    Tile toTile = clickedTile;

                    if (GameLogic.isLegalMove(fromTile, toTile, fromIndex, toIndex, session.getCurrentPlayer(), session.getBoard())) {
                        if (selectedTile.getCup().isPresent() && !selectedTile.getSaucer().isPresent()) {
                            clickedTile.placeCup((Cup) selectedTile.getCup().get());
                            selectedTile.removeCup();
                        } else if (selectedTile.getSaucer().isPresent() && !selectedTile.getCup().isPresent()) {
                            clickedTile.placeSaucer((Saucer) selectedTile.getSaucer().get());
                            selectedTile.removeSaucer();
                        } else if (selectedTile.hasBoth()) {
                            clickedTile.placeCup((Cup) selectedTile.getCup().get());
                            selectedTile.removeCup();
                        }

                        session.recordMove(fromIndex, toIndex);

                        //  Check if Player 1 just won
                        if (session.checkWinCondition("P1")) {
                            session.setWinner("P1");
                            updateBoardView();       // show last move
                            session.endGame();
                            saveGameStatistics();
                            updateTurnLabel();       // updated with correct winner
                            return;
                        }

                        if (session.isAutoLoss(session.getCurrentPlayer(), toIndex)) {
                            // First set the winner based on who did NOT make the mistake
                            String winner = session.getCurrentPlayer().equals("P1") ? "P2" : "P1";
                            session.setWinner(winner); // Set actual winner first
                            session.endGame();         // Mark game end
                            saveGameStatistics();      // Then save it

                            updateBoardView();

                            // Show game over message
                            String message = winner.equals("P2") ? "🤖 AI Wins!" : "🔴 Player Wins!";
                            view.getTurnLabel().setText("🏁 Game Over – " + message);
                            return;
                        }

                        session.switchPlayer();
                        updateBoardView();
                        updateTurnLabel();

                        // New check: Does Player 1 have any valid moves? If not, Player 2 wins automatically.
                        if (session.getCurrentPlayer().equals("P1") && !hasAnyValidPlayerMove()) {
                            System.out.println("🛑 Player 1 has no valid moves. Game Over.");
                            session.setWinner("P2"); // AI wins
                            session.endGame();
                            saveGameStatistics();
                            updateBoardView();
                            view.getTurnLabel().setText("🏁 Game Over – 🟠 AI Wins!");
                            return;
                        }


                        if (!session.isGameOver()) {
                            boolean aiMoved = ai.makeMove();
                            updateBoardView();

                            // Always check if AI has now won
                            if (session.checkWinCondition("P2")) {
                                session.setWinner("P2");
                                session.endGame();
                                saveGameStatistics();
                                updateBoardView();    //  update final move
                                showGameOverLabel(); //  THIS will actually display “AI Wins!”
                                return;
                            }


                            // AI failed to move
                            if (!aiMoved) {
                                System.out.println("🔁 AI had no valid moves. Double-checking manually...");

                                if (!hasAnyValidAIMove()) {
                                    System.out.println("🛑 Confirmed: AI has no valid moves. Game Over.");
                                    session.setWinner("P1");
                                    session.endGame();
                                    saveGameStatistics();
                                    updateBoardView();
                                    view.getTurnLabel().setText("🏁 Game Over – 🔴 Player Wins!");
                                } else {
                                    System.out.println("⚠️ AI skipped move even though valid options exist. Investigate AI logic.");
                                }
                            }

                            // Update label if game not ended above
                            if (!session.isGameOver()) {
                                updateTurnLabel();
                            }
                        }



                    }

                    StackPane fromTileView = (StackPane) view.getBoard().getChildren().get(fromIndex);
                    fromTileView.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: " + WOOD_COLOR + ";");
                    selectedTile = null;
                }
            });
        }
    }


    private void updateBoardView() {
        String woodColor = "#deb887";  // Wooden color (BurlyWood)

        for (int i = 0; i < 9; i++) {
            StackPane tileView = (StackPane) view.getBoard().getChildren().get(i);
            tileView.getChildren().clear();
            Tile tile = session.getBoard().getTile(i);

            // Always keep the wooden background color
            tileView.setStyle("-fx-background-color: " + woodColor + "; -fx-border-width: 1;");

            // If this tile is selected, add green border but keep wooden background
            if (selectedTile != null && selectedTile.getPosition() == i) {
                tileView.setStyle("-fx-background-color: " + WOOD_COLOR + "; -fx-border-color: #04f8e1; -fx-border-width: 3;");
            } else {
                tileView.setStyle("-fx-background-color: " + woodColor + "; -fx-border-color: black; -fx-border-width: 1;");
            }

            if (tile.getSaucer().isPresent()) {
                Rectangle saucer = new Rectangle(50, 15,
                        tile.getSaucer().get().getOwner().equals("P1") ? Color.web("#4169E1") : Color.web("#FF69B4"));
                tileView.getChildren().add(saucer);
            }
            if (tile.getCup().isPresent()) {
                Circle cup = new Circle(20,
                        tile.getCup().get().getOwner().equals("P1") ? Color.web("#B22222") : Color.web("#2F4F4F"));
                cup.setTranslateY(-10);
                tileView.getChildren().add(cup);
            }
        }
    }

    private void updateTurnLabel() {
        if (session.isGameOver()) {
            String winner = session.getWinner().equals("P1") ? "🔴 Player Wins!" : "🤖 AI Wins!";
            view.getTurnLabel().setText("🏁 Game Over – " + winner);
        } else {
            if (session.getCurrentPlayer().equals("P1")) {
                view.getTurnLabel().setText("\uD83D\uDD34 Player Turn");
            } else {
                view.getTurnLabel().setText("\uD83E\uDD16 AI Turn");
            }
        }


    }

    // This line marks the end of your class methods
    private boolean hasAnyValidAIMove() {
        Board board = session.getBoard();

        for (int i = 8; i >= 0; i--) {
            Tile from = board.getTile(i);

            // Check Cup moves
            if (from.getCup().isPresent() && from.getCup().get().getOwner().equals("P2")) {
                for (int j = i - 1; j >= 0; j--) {
                    Tile to = board.getTile(j);
                    if (GameLogic.isLegalMove(from, to, i, j, "P2", board)) {
                        return true;
                    }
                }
            }

            // Check Saucer moves
            if (from.getSaucer().isPresent()
                    && from.getSaucer().get().getOwner().equals("P2")
                    && !((Saucer) from.getSaucer().get()).hasCupOnTop()) {
                for (int j = i - 1; j >= 0; j--) {
                    Tile to = board.getTile(j);
                    if (GameLogic.isLegalMove(from, to, i, j, "P2", board)) {
                        return true;
                    }
                }
            }
        }

        return false;


    }

    private boolean hasAnyValidPlayerMove() {
        Board board = session.getBoard();

        for (int i = 0; i < 9; i++) {
            Tile from = board.getTile(i);

            // Check Cup moves
            if (from.getCup().isPresent() && from.getCup().get().getOwner().equals("P1")) {
                for (int j = i + 1; j < 9; j++) {
                    Tile to = board.getTile(j);
                    if (GameLogic.isLegalMove(from, to, i, j, "P1", board)) {
                        return true;
                    }
                }
            }

            // Check Saucer moves
            if (from.getSaucer().isPresent()
                    && from.getSaucer().get().getOwner().equals("P1")
                    && !((Saucer) from.getSaucer().get()).hasCupOnTop()) {
                for (int j = i + 1; j < 9; j++) {
                    Tile to = board.getTile(j);
                    if (GameLogic.isLegalMove(from, to, i, j, "P1", board)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void showGameOverLabel() {
        String winner = session.getWinner();
        if ("P1".equals(winner)) {
            view.getTurnLabel().setText("🏁 Game Over – 🔴 Player Wins!");
        } else if ("P2".equals(winner)) {
            view.getTurnLabel().setText("🏁 Game Over – 🟠 AI Wins!");
        }
    }

    private void saveGameStatistics() {
        System.out.println("Attempting to save game statistics...");
        StatisticsDAO dao = new StatisticsDAO();
        dao.saveGame(session.toStatisticsRecord());
        System.out.println(" Game statistics should now be saved.");
    }

}
