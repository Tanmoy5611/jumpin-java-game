package be.kdg.integration2.jumpinjava.view.startscreen;

import be.kdg.integration2.jumpinjava.model.statistics.StatisticsDAO;
import be.kdg.integration2.jumpinjava.model.statistics.StatisticsRecord;
import be.kdg.integration2.jumpinjava.view.gamescreen.GameScreenPresenter;
import be.kdg.integration2.jumpinjava.view.gamescreen.GameScreenView;
import be.kdg.integration2.jumpinjava.view.statsscreen.StatisticsPresenter;
import be.kdg.integration2.jumpinjava.view.statsscreen.StatisticsView;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class StartScreenPresenter {
    private final StartScreenView view;

    public StartScreenPresenter(StartScreenView view, Stage stage) {
        this.view = view;

        //  New Game Button
        view.getBtnNewGame().setOnAction(e -> {
            GameScreenView gameView = new GameScreenView(stage);
            new GameScreenPresenter(gameView, stage);
            stage.setScene(new Scene(gameView.getRoot(), 800, 600));
        });

        //  Statistics Button
        view.getBtnStats().setOnAction(e -> {
            List<StatisticsRecord> records = new StatisticsDAO().getAllGames();
            StatisticsView statsView = new StatisticsView(stage, records);
            new StatisticsPresenter(statsView, stage);
            stage.setScene(new Scene(statsView.getRoot(), 900, 700));
        });
    }
}