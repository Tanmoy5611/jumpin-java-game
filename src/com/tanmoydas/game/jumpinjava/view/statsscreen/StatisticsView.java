package com.tanmoydas.game.jumpinjava.view.statsscreen;

import com.tanmoydas.game.jumpinjava.model.statistics.StatisticsRecord;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatisticsView {

    private final VBox root;
    private final TableView<StatisticsRecord> table;
    private final LineChart<Number, Number> lineChart;
    private final Button backButton;

    public StatisticsView(List<StatisticsRecord> games) {

        // root
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // title
        Label title = new Label("📊 Game Statistics");
        title.setStyle("""
                -fx-font-size: 24px;
                -fx-font-weight: bold;
        """);

        // Table
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StatisticsRecord, String> dateCol = new TableColumn<>("Game Start");
        dateCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue()
                                .getGameStartTime()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                )
        );

        TableColumn<StatisticsRecord, String> durationCol = new TableColumn<>("Total Duration");
        durationCol.setCellValueFactory(cell ->
                new SimpleStringProperty(formatDuration(cell.getValue().getTotalDuration()))
        );

        TableColumn<StatisticsRecord, Integer> movesCol = new TableColumn<>("Moves");
        movesCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getTotalMoves()).asObject()
        );

        TableColumn<StatisticsRecord, String> avgMoveCol = new TableColumn<>("Avg Move Time");
        avgMoveCol.setCellValueFactory(cell ->
                new SimpleStringProperty(formatDuration(cell.getValue().getAverageMoveDuration()))
        );

        TableColumn<StatisticsRecord, String> winnerCol = new TableColumn<>("Winner");
        winnerCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getWinner().name())
        );

        table.getColumns().addAll(dateCol, durationCol, movesCol, avgMoveCol, winnerCol);
        table.getItems().addAll(games);

        // Line chart
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Move #");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Duration (ms)");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Move Durations (Last Game)");
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(true);
        lineChart.setMinHeight(300);

        if (!games.isEmpty()) {
            plotLastGame(games.get(0));
        }

        /* =====================
           BACK BUTTON
           ===================== */
        backButton = new Button("⬅ Back to Start");
        backButton.setPrefWidth(220);
        backButton.setStyle("""
                -fx-font-size: 14px;
                -fx-font-weight: bold;
        """);

        // layout
        BorderPane centerPane = new BorderPane();
        centerPane.setTop(table);
        centerPane.setBottom(lineChart);
        BorderPane.setMargin(lineChart, new Insets(15, 0, 0, 0));

        root.getChildren().addAll(title, centerPane, backButton);
    }

    // chart helper

    private void plotLastGame(StatisticsRecord record) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        List<Duration> durations = record.getMoveDurations();

        for (int i = 0; i < durations.size(); i++) {
            series.getData().add(
                    new XYChart.Data<>(i + 1, durations.get(i).toMillis())
            );
        }

        lineChart.getData().add(series);
    }

    private static String formatDuration(Duration d) {
        long seconds = d.toSeconds();
        long millis = d.toMillisPart();
        return seconds + "." + String.format("%03d", millis) + " s";
    }

    // Getters

    public VBox getRoot() {
        return root;
    }

    public Button getBackButton() {
        return backButton;
    }

    public TableView<StatisticsRecord> getTable() {
        return table;
    }

    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }
}