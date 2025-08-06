package be.kdg.integration2.jumpinjava.view.statsscreen;

import be.kdg.integration2.jumpinjava.model.statistics.StatisticsRecord;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class StatisticsView {
    private final TableView<StatisticsRecord> table;
    private final LineChart<Number, Number> lineChart;
    private final Button backButton;
    private final VBox root;

    public StatisticsView(Stage stage, List<StatisticsRecord> allGames) {
        root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("📊 Game Statistics");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Table setup
        table = new TableView<>();
        TableColumn<StatisticsRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("gameDate"));

        TableColumn<StatisticsRecord, Long> durationCol = new TableColumn<>("Total Duration");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("totalDuration"));

        TableColumn<StatisticsRecord, Integer> moveCol = new TableColumn<>("Total Moves");
        moveCol.setCellValueFactory(new PropertyValueFactory<>("totalMoves"));

        TableColumn<StatisticsRecord, Long> avgCol = new TableColumn<>("Avg Move Duration");
        avgCol.setCellValueFactory(new PropertyValueFactory<>("avgMoveDuration"));

        TableColumn<StatisticsRecord, String> winnerCol = new TableColumn<>("Winner");
        winnerCol.setCellValueFactory(new PropertyValueFactory<>("winner"));

        table.getColumns().addAll(dateCol, durationCol, moveCol, avgCol, winnerCol);
        table.getItems().addAll(allGames);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Line chart setup
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Move #");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Duration (ms)");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Move Durations - Last Game");

        // Last game move duration plot
        if (!allGames.isEmpty()) {
            StatisticsRecord lastGame = allGames.get(0);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Move Durations");

            List<Long> durations = lastGame.getMoveDurations();

            double average = durations.stream().mapToLong(Long::longValue).average().orElse(0);
            double stdDev = Math.sqrt(
                    durations.stream().mapToDouble(d -> Math.pow(d - average, 2)).sum() / durations.size());

            for (int i = 0; i < durations.size(); i++) {
                long value = durations.get(i);
                XYChart.Data<Number, Number> point = new XYChart.Data<>(i + 1, value);
                series.getData().add(point);

                point.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null && Math.abs(value - average) > 2 * stdDev) {
                        newNode.setStyle("-fx-background-color: red; -fx-stroke: red;");
                    }
                });
            }

            lineChart.getData().add(series);
        }

        backButton = new Button("🔙 Back to Start");
        backButton.setPrefWidth(200);

        BorderPane layout = new BorderPane();
        layout.setTop(title);
        layout.setCenter(table);
        layout.setBottom(lineChart);
        BorderPane.setMargin(lineChart, new Insets(10, 0, 10, 0));

        root.getChildren().addAll(layout, backButton);
        stage.setScene(new Scene(root, 900, 700));
    }

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