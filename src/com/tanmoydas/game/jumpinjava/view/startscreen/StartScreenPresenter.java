package com.tanmoydas.game.jumpinjava.view.startscreen;

import com.tanmoydas.game.jumpinjava.data.statistics.StatisticsDAO;
import com.tanmoydas.game.jumpinjava.model.rulebasedsystem.AIEngine;
import com.tanmoydas.game.jumpinjava.model.rulebasedsystem.AdvancedAIEngine;
import com.tanmoydas.game.jumpinjava.model.rulebasedsystem.SimpleAIEngine;
import com.tanmoydas.game.jumpinjava.model.statistics.StatisticsRecord;
import com.tanmoydas.game.jumpinjava.view.gamescreen.GameScreenPresenter;
import com.tanmoydas.game.jumpinjava.view.gamescreen.GameScreenView;
import com.tanmoydas.game.jumpinjava.view.statsscreen.StatisticsPresenter;
import com.tanmoydas.game.jumpinjava.view.statsscreen.StatisticsView;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

/* Presenter for the Start Screen.
   Responsible only for user interaction and navigation.
 */
public class StartScreenPresenter {

    private final StartScreenView view;
    private final Stage stage;
    private final StatisticsDAO statisticsDAO;

    public StartScreenPresenter(StartScreenView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.statisticsDAO = new StatisticsDAO();

        attachEventHandlers();
    }

    // Event wiring
    private void attachEventHandlers() {
        view.getBtnNewGame().setOnAction(e -> openGameScreen());
        view.getBtnStats().setOnAction(e -> openStatisticsScreen());
    }

   // Navigation logic
   private void openGameScreen() {

       AIEngine selectedAI;

       if (view.isAdvancedSelected()) {
           selectedAI = new AdvancedAIEngine();
       } else {
           selectedAI = new SimpleAIEngine();
       }

       GameScreenView gameView = new GameScreenView(stage);
       new GameScreenPresenter(gameView, stage, selectedAI);

       stage.setScene(new Scene(gameView.getRoot(), 1000, 700));
   }

    private void openStatisticsScreen() {
        List<StatisticsRecord> records =
                statisticsDAO.getAllGames();

        //  takes ONLY the data list
        StatisticsView statsView =
                new StatisticsView(records);

        new StatisticsPresenter(statsView, stage);

        stage.setScene(
                new Scene(statsView.getRoot(), 900, 700)
        );
    }
}