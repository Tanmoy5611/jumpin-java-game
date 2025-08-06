package be.kdg.integration2.jumpinjava;

import be.kdg.integration2.jumpinjava.view.startscreen.StartScreenPresenter;
import be.kdg.integration2.jumpinjava.view.startscreen.StartScreenView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JumpinMain extends Application {
    public static void main(String[] args) {
        launch(args);                             // App Starts
    }

    @Override
    public void start(Stage primaryStage) {
        StartScreenView view = new StartScreenView(primaryStage);  // Pass primaryStage
        new StartScreenPresenter(view, primaryStage);
        primaryStage.setTitle("Jumpin' Java");
        primaryStage.setScene(new Scene(view.getRoot(), 800, 600));
        primaryStage.show();
    }

}