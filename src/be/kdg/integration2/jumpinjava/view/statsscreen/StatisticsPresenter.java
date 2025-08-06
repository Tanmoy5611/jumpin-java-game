package be.kdg.integration2.jumpinjava.view.statsscreen;

import be.kdg.integration2.jumpinjava.view.startscreen.StartScreenPresenter;
import be.kdg.integration2.jumpinjava.view.startscreen.StartScreenView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StatisticsPresenter {
    private final StatisticsView view;
    private final Stage stage;

    public StatisticsPresenter(StatisticsView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        setupBackButton();
    }

    private void setupBackButton() {
        view.getBackButton().setOnAction(e -> {
            StartScreenView startView = new StartScreenView(stage);
            new StartScreenPresenter(startView, stage);
            stage.setScene(new Scene(startView.getRoot(), 800, 600));
        });
    }
}