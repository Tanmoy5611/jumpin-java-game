package com.tanmoydas.game.jumpinjava.view.startscreen;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.ToggleGroup;

public class StartScreenView {

    private VBox root;
    private Button btnNewGame;
    private Button btnStats;
    private RadioButton rbSimple;
    private RadioButton rbAdvanced;

    public StartScreenView(Stage stage) {

        // Background
        StackPane background = new StackPane();
        background.setStyle("""
            -fx-background-color:
                radial-gradient(
                    center 50% 30%,
                    radius 85%,
                    #2b3140,
                    #0d0f14
                );
        """);

        // ambient stream
        Circle steam1 = createSteam(180, 0.06, -260, -200, 14);
        Circle steam2 = createSteam(240, 0.05, 240, -170, 18);
        Circle steam3 = createSteam(320, 0.04, 0, 260, 22);

        background.getChildren().addAll(steam1, steam2, steam3);

        // Main content
        VBox content = new VBox(28);
        content.setAlignment(Pos.CENTER);

        Text title = new Text("☕ Jumpin' Java");
        title.setStyle("""
            -fx-font-size: 36px;
            -fx-fill: #f5f5f5;
            -fx-font-weight: bold;
        """);

        DropShadow titleGlow = new DropShadow(18, Color.rgb(255, 190, 120));
        title.setEffect(titleGlow);

        animateTitleGlow(titleGlow);

        btnNewGame = new Button("▶ Start New Game");
        stylePrimaryButton(btnNewGame);
        animateButton(btnNewGame);

        btnStats = new Button("📊 View Statistics");
        styleSecondaryButton(btnStats);
        animateButton(btnStats);

        // Difficulty Selection
        ToggleGroup difficultyGroup = new ToggleGroup();

        rbSimple = new RadioButton("🟢 Simple AI");
        rbAdvanced = new RadioButton("🔥 Advanced AI");

        rbSimple.setToggleGroup(difficultyGroup);
        rbAdvanced.setToggleGroup(difficultyGroup);

        rbSimple.setSelected(true); // default

       // style
        rbSimple.setTextFill(Color.WHITE);
        rbAdvanced.setTextFill(Color.WHITE);

        rbSimple.setStyle("-fx-font-size: 13px;");
        rbAdvanced.setStyle("-fx-font-size: 13px;");

       // small animation on hover
        animateDifficulty(rbSimple);
        animateDifficulty(rbAdvanced);

        VBox difficultyBox = new VBox(6, rbSimple, rbAdvanced);
        difficultyBox.setAlignment(Pos.CENTER);

        content.getChildren().addAll(
                title,
                btnNewGame,
                difficultyBox,
                btnStats
        );

        // Entry animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        background.getChildren().add(content);

        // root
        VBox wrapper = new VBox(background);
        wrapper.setAlignment(Pos.CENTER);

        root = wrapper;
    }

    // Button styling
    private void stylePrimaryButton(Button btn) {
        btn.setStyle("""
            -fx-font-size: 15px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
            -fx-background-color: linear-gradient(#ff9800, #e65100);
            -fx-background-radius: 22;
            -fx-padding: 14 38;
        """);
        btn.setEffect(new DropShadow(14, Color.BLACK));
    }

    private void styleSecondaryButton(Button btn) {
        btn.setStyle("""
            -fx-font-size: 14px;
            -fx-text-fill: white;
            -fx-background-color: linear-gradient(#424242, #1c1c1c);
            -fx-background-radius: 20;
            -fx-padding: 12 34;
        """);
        btn.setEffect(new DropShadow(12, Color.BLACK));
    }

    // Button Animation

    private void animateButton(Button btn) {
        DropShadow glow = new DropShadow(20, Color.rgb(255, 180, 100, 0.6));

        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
            btn.setEffect(glow);
        });

        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            btn.setEffect(new DropShadow(12, Color.BLACK));
        });
    }

    // Steam Effect
    private Circle createSteam(double size, double opacity, double x, double y, double drift) {
        Circle steam = new Circle(size);
        steam.setFill(Color.rgb(255, 255, 255, opacity));
        steam.setTranslateX(x);
        steam.setTranslateY(y);
        steam.setEffect(new DropShadow(50, Color.rgb(255, 255, 255, opacity)));

        TranslateTransition floatUp = new TranslateTransition(Duration.seconds(drift), steam);
        floatUp.setByY(-40);
        floatUp.setAutoReverse(true);
        floatUp.setCycleCount(Animation.INDEFINITE);
        floatUp.play();

        return steam;
    }

    // Title glow
    private void animateTitleGlow(DropShadow glow) {
        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(glow.radiusProperty(), 18)),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(glow.radiusProperty(), 28))
        );
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
    }

    private void animateDifficulty(RadioButton rb) {
        rb.setOnMouseEntered(e -> {
            rb.setScaleX(1.08);
            rb.setScaleY(1.08);
        });

        rb.setOnMouseExited(e -> {
            rb.setScaleX(1.0);
            rb.setScaleY(1.0);
        });
    }

    // Getters
    public VBox getRoot() {
        return root;
    }

    public Button getBtnNewGame() {
        return btnNewGame;
    }

    public Button getBtnStats() {
        return btnStats;
    }

    public boolean isAdvancedSelected() {
        return rbAdvanced.isSelected();
    }
}