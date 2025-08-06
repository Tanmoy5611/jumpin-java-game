package be.kdg.integration2.jumpinjava.model.statistics;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class StatisticsDAO {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/jumpin_game";
    private static final String DB_USER = "tanmoy";
    private static final String DB_PASS = "whyMe5";


    public void saveGame(StatisticsRecord record) {
        System.out.println("[DAO] Saving game to database...");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);

            // 1. Insert into games table
            String insertGame = "INSERT INTO game (date_played, winner, duration) VALUES (?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(insertGame, Statement.RETURN_GENERATED_KEYS)) {
                ps.setTimestamp(1, record.getGameDate()); //  date_played
                ps.setString(2, record.getWinner());       //  winner
                ps.setLong(3, record.getTotalDuration());  //  duration
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int gameId = keys.getInt(1);

                    // 2. Insert move durations

                    String insertMove = "INSERT INTO move (game_id, move_number, duration) VALUES (?, ?, ?)";
                    try (PreparedStatement movePs = conn.prepareStatement(insertMove)) {
                        List<Long> durations = record.getMoveDurations();
                        for (int i = 0; i < durations.size(); i++) {
                            movePs.setInt(1, gameId);
                            movePs.setInt(2, i + 1);
                            movePs.setLong(3, durations.get(i));
                            movePs.addBatch();
                        }
                        movePs.executeBatch();
                        System.out.println("📊 Move durations inserted: " + durations.size() + " moves.");

                    }
                }

                conn.commit();
                System.out.println("✅ [DAO] Commit successful. Game saved.");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StatisticsRecord> getAllGames() {
        List<StatisticsRecord> games = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT * FROM game ORDER BY game_id DESC";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Timestamp date = rs.getTimestamp("date_played");
                    long totalDur = rs.getLong("duration");
                    String winner = rs.getString("winner");

                    // Get move durations
                    List<Long> durations = new ArrayList<>();
                    int gameId = rs.getInt("game_id");
                    String moveQuery = "SELECT duration FROM move WHERE game_id = ? ORDER BY move_number";
                    try (PreparedStatement ps = conn.prepareStatement(moveQuery)) {
                        ps.setInt(1, gameId);
                        try (ResultSet rsMove = ps.executeQuery()) {
                            while (rsMove.next()) {
                                durations.add(rsMove.getLong("duration"));
                            }
                        }
                    }
                    int totalMoves = durations.size();
                    long avgDur = totalMoves > 0
                            ? (long) durations.stream().mapToLong(Long::longValue).average().orElse(0)
                            : 0;

                    games.add(new StatisticsRecord(date, totalDur, totalMoves, avgDur, winner, durations));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return games;
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}