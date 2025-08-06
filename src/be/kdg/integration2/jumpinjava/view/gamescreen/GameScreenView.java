package be.kdg.integration2.jumpinjava.view.gamescreen;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameScreenView {
    private VBox root;
    private GridPane board;
    private Button backButton;
    private Label turnLabel;

    public GameScreenView(Stage stage) {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Text title = new Text("Jumpin' Java – Game Screen");
        title.setStyle("-fx-font-size: 20px;");

        turnLabel = new Label("🔴 Player Turn");
        turnLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: darkgreen;");

        board = new GridPane();
        board.setHgap(10);
        board.setVgap(10);
        board.setAlignment(Pos.CENTER);

        for (int i = 0; i < 9; i++) {
            StackPane tile = new StackPane();
            tile.setPrefSize(90, 80);
            tile.setStyle("-fx-border-color: black; -fx-background-color: #9c6c2d;");
            board.add(tile, i, 0);
        }

        backButton = new Button("Back to Menu");

        root.getChildren().addAll(title, turnLabel, board, backButton);
    }

    public VBox getRoot() {
        return root;
    }

    public GridPane getBoard() {
        return board;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Label getTurnLabel() {
        return turnLabel;
    }
}