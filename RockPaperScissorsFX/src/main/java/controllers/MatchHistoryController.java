package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.MatchHistoryEntry;
import models.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class MatchHistoryController {
    @FXML private TableView<MatchHistoryEntry> historyTable;
    @FXML private TableColumn<MatchHistoryEntry, String> player1Column;
    @FXML private TableColumn<MatchHistoryEntry, String> player2Column;
    @FXML private TableColumn<MatchHistoryEntry, String> move1Column;
    @FXML private TableColumn<MatchHistoryEntry, String> move2Column;
    @FXML private TableColumn<MatchHistoryEntry, String> winnerColumn;
    @FXML private TableColumn<MatchHistoryEntry, String> matchDateColumn;

    private ObservableList<MatchHistoryEntry> matchHistoryData = FXCollections.observableArrayList();
    private String username;

    @FXML
    public void initialize() {
        // Set up the table columns
        player1Column.setCellValueFactory(new PropertyValueFactory<>("player1"));
        player2Column.setCellValueFactory(new PropertyValueFactory<>("player2"));
        move1Column.setCellValueFactory(new PropertyValueFactory<>("player1Move"));
        move2Column.setCellValueFactory(new PropertyValueFactory<>("player2Move"));
        winnerColumn.setCellValueFactory(new PropertyValueFactory<>("winner"));
        matchDateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));

        historyTable.setItems(matchHistoryData);
    }

    public void setUsername(String username) {
        this.username = username;
        loadMatchHistory();
    }

    @FXML
    private void loadMatchHistory() {
        if (username == null || username.isEmpty()) {
            System.err.println("Username is not set");
            return;
        }

        matchHistoryData.clear();

        String sql = "SELECT player1, player2, player1_move, player2_move, winner, match_date " +
                "FROM match_history WHERE player1 = ? OR player2 = ? " +
                "ORDER BY match_date DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, username);

            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                MatchHistoryEntry entry = new MatchHistoryEntry(
                        rs.getString("player1"),
                        rs.getString("player2"),
                        rs.getString("player1_move"),
                        rs.getString("player2_move"),
                        rs.getString("winner"),
                        dateFormat.format(rs.getTimestamp("match_date"))
                );
                matchHistoryData.add(entry);
            }
        } catch (SQLException e) {
            System.err.println("Error loading match history:");
            e.printStackTrace();
            // You could show an alert to the user here
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) historyTable.getScene().getWindow();
        stage.close();
    }
}