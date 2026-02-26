package com.tanmoydas.game.jumpinjava.view.gamescreen;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameScreenView {

    // core UI elements
    private VBox root;
    private VBox moveHistoryPanel;
    private VBox moveList;
    private GridPane board;
    private Button backButton;
    private Label turnLabel;

    // Track tiles in an array so the Presenter can access them by index
    private final StackPane[] tiles = new StackPane[9];

    // Overlays
    private StackPane overlayLayer;
    private VBox aiThinkingOverlay;
    private VBox winOverlay;

    private final Button btnPlayAgain = new Button("🔁 Play Again");
    private final Button btnBackToMenu = new Button("⬅ Back to Menu");

    // Sounds
    private final AudioClip clickSound;
    private final AudioClip dropSound;
    private final AudioClip winSound;

    public GameScreenView(Stage stage) {
        // Load sounds safely
        clickSound = loadSound("/sounds/click.wav");
        dropSound = loadSound("/sounds/drop.wav");
        winSound = loadSound("/sounds/win.wav");

        // Backgrounds
        StackPane background = new StackPane();
        background.setStyle("""
            -fx-background-color:
                radial-gradient(
                    center 50% 30%,
                    radius 90%,
                    #2f3642,
                    #0f1117
                );
        """);

        background.getChildren().addAll(
                createRing(240, -420, 120, 20),
                createRing(180, 420, -80, 22),
                createSteam(300, 0.03, 0, 260, 18),
                createSteam(220, 0.04, -360, -200, 14),
                createSteam(260, 0.035, 360, -180, 16)
        );

       // Main content
        VBox content = new VBox(26);
        content.setAlignment(Pos.CENTER);

       // Header
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setStyle("""
            -fx-background-color: rgba(255,255,255,0.06);
            -fx-background-radius: 22;
            -fx-padding: 18 34;
            -fx-border-color: rgba(255,255,255,0.12);
            -fx-border-radius: 22;
        """);

        DropShadow headerShadow = new DropShadow(18, Color.rgb(0, 0, 0, 0.75));
        header.setEffect(headerShadow);
        animateBreathingShadow(headerShadow);

        Text title = new Text("☕ Jumpin' Java");
        title.setStyle("-fx-font-size: 32px; -fx-fill: #f5f5f5; -fx-font-weight: bold;");

        turnLabel = new Label("🔴 Player Turn");
        turnLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #00e676; -fx-font-weight: bold;");
        animateTurnPulse(turnLabel);

        header.getChildren().addAll(title, turnLabel);

        // Board
        StackPane boardWrapper = new StackPane();
        boardWrapper.setStyle("-fx-background-color: rgba(0,0,0,0.20); -fx-background-radius: 26; -fx-padding: 18;");
        boardWrapper.setEffect(new DropShadow(36, Color.rgb(0, 0, 0, 0.85)));

        board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setHgap(10);
        board.setVgap(10);
        board.setStyle("""
            -fx-background-color: #5a3e1b;
            -fx-padding: 22;
            -fx-background-radius: 22;
            -fx-border-radius: 22;
            -fx-border-color: rgba(255,255,255,0.10);
            -fx-border-width: 1;
        """);

        createTiles();
        boardWrapper.getChildren().add(board);

        // Move history panel
        moveHistoryPanel = createMoveHistoryPanel();

        HBox gameArea = new HBox(20, boardWrapper, moveHistoryPanel);
        gameArea.setAlignment(Pos.CENTER);

        // Back button
        backButton = new Button("⬅ Back to Menu");
        styleSecondaryButton(backButton);
        animateButtonHover(backButton, true);

        content.getChildren().addAll(header, gameArea, backButton);

        // Overlays
        overlayLayer = new StackPane();
        overlayLayer.setPickOnBounds(false);

        aiThinkingOverlay = createAIThinkingOverlay();
        aiThinkingOverlay.setVisible(false);
        aiThinkingOverlay.setMouseTransparent(true);

        winOverlay = createWinOverlay();
        winOverlay.setVisible(false);
        winOverlay.setMouseTransparent(true);

        overlayLayer.getChildren().addAll(aiThinkingOverlay, winOverlay);
        background.getChildren().addAll(content, overlayLayer);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        root = new VBox(background);
        root.setAlignment(Pos.CENTER);
    }


    private void styleSecondaryButton(Button btn) {
        btn.setStyle("""
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-color: linear-gradient(#3a3a3a, #1f1f1f);
            -fx-text-fill: white;
            -fx-background-radius: 18;
            -fx-padding: 10 26;
            -fx-border-color: rgba(255,255,255,0.12);
            -fx-border-radius: 18;
        """);
        btn.setEffect(new DropShadow(12, Color.BLACK));
    }

    // Sound helpers
    private AudioClip loadSound(String path) {
        try {
            AudioClip clip = new AudioClip(
                    getClass().getResource(path).toExternalForm()
            );
            clip.setVolume(1.0);
            return clip;
        } catch (Exception e) {
            System.err.println("Failed to load sound: " + path);
            return null;
        }
    }

    private void play(AudioClip clip) {
        if (clip != null) clip.play();
    }

    // Tile Creation
    private void createTiles() {
        for (int i = 0; i < 9; i++) {
            StackPane tile = new StackPane();
            tile.setPrefSize(90, 80);

            String base = """
                -fx-background-color: #deb887;
                -fx-background-radius: 14;
                -fx-border-radius: 14;
                -fx-border-color: rgba(0,0,0,0.65);
                -fx-border-width: 1.2;
            """;

            String hover = """
                -fx-background-color: #f2c98c;
                -fx-background-radius: 14;
                -fx-border-radius: 14;
                -fx-border-color: rgba(255,213,79,0.85);
                -fx-border-width: 1.6;
            """;

            tile.setStyle(base);

            TranslateTransition up = new TranslateTransition(Duration.millis(120), tile);
            up.setToY(-4);
            TranslateTransition down = new TranslateTransition(Duration.millis(140), tile);
            down.setToY(0);

            tile.setOnMouseEntered(e -> {
                tile.setStyle(hover);
                up.playFromStart();
                play(clickSound);
            });

            tile.setOnMouseExited(e -> {
                tile.setStyle(base);
                down.playFromStart();
            });

            tiles[i] = tile; // Store reference
            board.add(tile, i, 0);
        }
    }

    public StackPane getTile(int index) {
        return tiles[index];
    }

    // piece drop animation
    public void animatePieceDrop(StackPane tile, Node piece) {
        piece.setTranslateY(-40);
        piece.setOpacity(0);
        tile.getChildren().add(piece);

        play(dropSound);

        TranslateTransition drop = new TranslateTransition(Duration.millis(260), piece);
        drop.setToY(0);

        FadeTransition fade = new FadeTransition(Duration.millis(200), piece);
        fade.setToValue(1);

        ScaleTransition bounce = new ScaleTransition(Duration.millis(140), piece);
        bounce.setFromX(1.15);
        bounce.setFromY(1.15);
        bounce.setToX(1.0);
        bounce.setToY(1.0);

        new SequentialTransition(new ParallelTransition(drop, fade), bounce).play();
    }

    // Ai thinking overlay
    private VBox createAIThinkingOverlay() {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(0,0,0,0.55);");
        box.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Text txt = new Text("🤖 AI is thinking...");
        txt.setStyle("-fx-font-size: 20px; -fx-fill: white; -fx-font-weight: bold;");

        HBox dots = new HBox(8, createPulseDot(), createPulseDot(), createPulseDot());
        dots.setAlignment(Pos.CENTER);

        box.getChildren().addAll(txt, dots);
        return box;
    }

    // AI thinks animation
    private Circle createPulseDot() {
        Circle c = new Circle(5, Color.WHITE);
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.6), c);
        pulse.setFromX(0.6); pulse.setFromY(0.6);
        pulse.setToX(1.2); pulse.setToY(1.2);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
        return c;
    }

    public void showAIThinking(boolean show) {
        aiThinkingOverlay.setVisible(show);
        aiThinkingOverlay.setMouseTransparent(!show);
    }

    // create win overlay
    private VBox createWinOverlay() {
        VBox box = new VBox(25);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(0,0,0,0.75);");
        box.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Text text = new Text("🏆 YOU WIN!");
        text.setStyle("-fx-font-size: 42px; -fx-fill: #ffd54f; -fx-font-weight: bold;");
        text.setEffect(new DropShadow(28, Color.GOLD));

        styleSecondaryButton(btnPlayAgain);
        styleSecondaryButton(btnBackToMenu);

        HBox row = new HBox(15, btnPlayAgain, btnBackToMenu);
        row.setAlignment(Pos.CENTER);

        box.getChildren().addAll(text, row);
        return box;
    }

    // Show win overlay
    public void showWinOverlay(String message) {
        ((Text) winOverlay.getChildren().get(0)).setText(message);
        winOverlay.setVisible(true);
        winOverlay.setMouseTransparent(false);

        play(winSound);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.6), winOverlay);
        fade.setFromValue(0); fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.6), winOverlay);
        scale.setFromX(0.85); scale.setFromY(0.85);
        scale.setToX(1.0); scale.setToY(1.0);

        new ParallelTransition(fade, scale).play();
    }

    // create move history
    private VBox createMoveHistoryPanel() {
        Label title = new Label("📜 Move History");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        moveList = new VBox(6);
        moveList.setPadding(new Insets(10));

        VBox panel = new VBox(10, title, moveList);
        panel.setPrefWidth(220);
        panel.setStyle("""
            -fx-background-color: rgba(0,0,0,0.40);
            -fx-padding: 12;
            -fx-background-radius: 16;
            -fx-border-color: rgba(255,255,255,0.10);
            -fx-border-radius: 16;
        """);

        return panel;
    }

    // Add move to history
    public void addMoveToHistory(String text) {
        Label move = new Label(text);
        move.setTextFill(Color.WHITE);
        move.setStyle("-fx-font-size: 12px;");
        moveList.getChildren().add(move);
    }


    // ambient and UI helpers
    private Circle createSteam(double size, double opacity, double x, double y, double seconds) {
        Circle c = new Circle(size, Color.rgb(255, 255, 255, opacity));
        c.setTranslateX(x); c.setTranslateY(y);
        TranslateTransition t = new TranslateTransition(Duration.seconds(seconds), c);
        t.setByY(-40); t.setAutoReverse(true); t.setCycleCount(Animation.INDEFINITE);
        t.play();
        return c;
    }

    // animation for creating rings
    private Circle createRing(double radius, double x, double y, double seconds) {
        Circle r = new Circle(radius);
        r.setFill(Color.TRANSPARENT);
        r.setStroke(Color.rgb(255, 255, 255, 0.04));
        r.setStrokeWidth(6);
        r.setTranslateX(x); r.setTranslateY(y);
        TranslateTransition t = new TranslateTransition(Duration.seconds(seconds), r);
        t.setByX(20); t.setAutoReverse(true); t.setCycleCount(Animation.INDEFINITE);
        t.play();
        return r;
    }

    // animation helpers for breathing shadow
    private void animateBreathingShadow(DropShadow shadow) {
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(shadow.radiusProperty(), 18)),
                new KeyFrame(Duration.seconds(6), new KeyValue(shadow.radiusProperty(), 26))
        );
        t.setAutoReverse(true); t.setCycleCount(Animation.INDEFINITE);
        t.play();
    }

    // animation helpers
    private void animateTurnPulse(Label label) {
        FadeTransition f = new FadeTransition(Duration.seconds(1.4), label);
        f.setFromValue(1); f.setToValue(0.6);
        f.setAutoReverse(true); f.setCycleCount(Animation.INDEFINITE);
        f.play();
    }

    // animations
    private void animateButtonHover(Button btn, boolean withSound) {
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.05); btn.setScaleY(1.05);
            if (withSound) play(clickSound);
        });
        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0); btn.setScaleY(1.0);
        });
    }

    // Highlight move
    public void highlightMove(StackPane from, StackPane to) {
        highlightTile(from, Color.RED);
        highlightTile(to, Color.LIMEGREEN);
    }

    // Highlight tile
    private void highlightTile(StackPane tile, Color color) {
        DropShadow glow = new DropShadow(25, color);
        tile.setEffect(glow);
        Timeline t = new Timeline(new KeyFrame(Duration.seconds(0.6), new KeyValue(glow.radiusProperty(), 0)));
        t.setOnFinished(e -> tile.setEffect(null));
        t.play();
    }

    // Illegal move feedback
    public void animateIllegalMove(StackPane tile) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), tile);
        shake.setByX(6); shake.setAutoReverse(true); shake.setCycleCount(4);
        DropShadow redGlow = new DropShadow(18, Color.RED);
        tile.setEffect(redGlow);
        shake.setOnFinished(e -> tile.setEffect(null));
        shake.play();
    }

    // Getters
    public VBox getRoot() { return root; }
    public GridPane getBoard() { return board; }
    public Button getBackButton() { return backButton; }
    public Label getTurnLabel() { return turnLabel; }
    public Button getPlayAgainButton() { return btnPlayAgain; }
    public Button getBackToMenuButton() { return btnBackToMenu; }
}