package com.tanmoydas.game.jumpinjava.data.statistics;

import com.tanmoydas.game.jumpinjava.model.statistics.StatisticsRecord;
import com.tanmoydas.game.jumpinjava.model.statistics.Winner;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/* Data Access Object for game statistics.
  Handles persistence only (JDBC / SQL).
 */
public class StatisticsDAO {

    private static final String DB_URL =
            "jdbc:postgresql://localhost:5432/jumpin_game";

    private static final String DB_USER =
            System.getenv().getOrDefault("JUMPIN_DB_USER", "tanmoy");

    private static final String DB_PASS =
            System.getenv().getOrDefault("JUMPIN_DB_PASS", "");

    // Save statistics
    public void saveGame(StatisticsRecord record) {
        String insertGame =
                "INSERT INTO game (date_played, winner, duration) VALUES (?, ?, ?)";

        String insertMove =
                "INSERT INTO move (game_id, move_number, duration) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);

            try (PreparedStatement gamePs =
                         conn.prepareStatement(insertGame, Statement.RETURN_GENERATED_KEYS)) {

                gamePs.setTimestamp(1, Timestamp.valueOf(record.getGameStartTime()));
                gamePs.setString(2, record.getWinner().name());
                gamePs.setLong(3, record.getTotalDuration().toMillis());

                gamePs.executeUpdate();

                ResultSet keys = gamePs.getGeneratedKeys();
                if (!keys.next()) {
                    throw new SQLException("Failed to retrieve game_id");
                }

                int gameId = keys.getInt(1);

                try (PreparedStatement movePs =
                             conn.prepareStatement(insertMove)) {

                    List<Duration> durations = record.getMoveDurations();
                    for (int i = 0; i < durations.size(); i++) {
                        movePs.setInt(1, gameId);
                        movePs.setInt(2, i + 1);
                        movePs.setLong(3, durations.get(i).toMillis());
                        movePs.addBatch();
                    }
                    movePs.executeBatch();
                }

                conn.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save statistics", e);
        }
    }

    // Load statistics

    public List<StatisticsRecord> getAllGames() {
        List<StatisticsRecord> games = new ArrayList<>();

        String gameQuery =
                "SELECT game_id, date_played, winner, duration FROM game ORDER BY game_id DESC";

        String moveQuery =
                "SELECT duration FROM move WHERE game_id = ? ORDER BY move_number";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement gameStmt = conn.prepareStatement(gameQuery);
             ResultSet rs = gameStmt.executeQuery()) {

            while (rs.next()) {
                int gameId = rs.getInt("game_id");
                LocalDateTime startTime =
                        rs.getTimestamp("date_played").toLocalDateTime();

                Duration totalDuration =
                        Duration.ofMillis(rs.getLong("duration"));

                Winner winner =
                        Winner.valueOf(rs.getString("winner"));

                List<Duration> moveDurations = new ArrayList<>();

                try (PreparedStatement moveStmt =
                             conn.prepareStatement(moveQuery)) {

                    moveStmt.setInt(1, gameId);
                    try (ResultSet moveRs = moveStmt.executeQuery()) {
                        while (moveRs.next()) {
                            moveDurations.add(
                                    Duration.ofMillis(moveRs.getLong("duration"))
                            );
                        }
                    }
                }

                int totalMoves = moveDurations.size();
                Duration avgMoveDuration =
                        totalMoves == 0
                                ? Duration.ZERO
                                : Duration.ofMillis(
                                (long) moveDurations.stream()
                                        .mapToLong(Duration::toMillis)
                                        .average()
                                        .orElse(0)
                        );

                games.add(
                        new StatisticsRecord(
                                startTime,
                                totalDuration,
                                totalMoves,
                                avgMoveDuration,
                                winner,
                                moveDurations
                        )
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load statistics", e);
        }

        return games;
    }

   // Driver initialization

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL driver not found", e);
        }
    }
}