package com.tanmoydas.game.jumpinjava;

import com.tanmoydas.game.jumpinjava.view.startscreen.StartScreenPresenter;
import com.tanmoydas.game.jumpinjava.view.startscreen.StartScreenView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JumpinMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        StartScreenView view = new StartScreenView(primaryStage);
        new StartScreenPresenter(view, primaryStage);

        Scene scene = new Scene(view.getRoot(), 800, 600);

        scene.getStylesheets().add(
                getClass()
                        .getResource("/stylesheets/style.css")
                        .toExternalForm()
        );

        primaryStage.setTitle("Jumpin' Java game by Tanmoy");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}