package be.kdg.integration2.jumpinjava.view.startscreen;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartScreenView {
    private VBox root;
    private Button btnNewGame;
    private Button btnStats;

    public StartScreenView(Stage stage) {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        Text title = new Text("☕ Jumpin' Java");
        title.setStyle("-fx-font-size: 24px;");
        btnNewGame = new Button("Start New Game");
        btnStats = new Button("View Statistics");

        root.getChildren().addAll(title, btnNewGame, btnStats);
    }

    // Getters for Presenter
    public VBox getRoot() {
        return root;
    }

    Button getBtnNewGame() {
        return btnNewGame;
    }

    Button getBtnStats() {
        return btnStats;
    }
}