package com.example.booklist;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class BookPage {

    private static final int userID = UserID.getUserID();

    public BookPage(Stage primaryStage, int bookID, String title, String description, String imageUrl) {

        VBox vbox = new VBox(5);
        vbox.setStyle("-fx-padding: 10; -fx-alignment: center;");


        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label descriptionLabel = new Label(description);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-wrap-text: true; -fx-max-width: 300px;");

        Label languageLabel = new Label("Language: " + getBookDetail(bookID, "language"));
        Label authorLabel = new Label("Author: " + getBookDetail(bookID, "author"));
        Label publisherLabel = new Label("Publisher: " + getBookDetail(bookID, "publisher"));

        ImageView imageView = new ImageView(new Image(imageUrl));
        imageView.setFitWidth(150);
        imageView.setFitHeight(10);

        TextField commentField = new TextField();
        commentField.setPromptText("Enter your review...");
        commentField.setStyle("-fx-max-width: 300px;");

        ComboBox<String> scoreComboBox = new ComboBox<>();
        loadScores(scoreComboBox);

        Button submitButton = new Button("Submit Review");
        submitButton.setOnAction(event -> {
            String comment = commentField.getText();
            String selectedScore = scoreComboBox.getValue();
            if (!comment.isEmpty() && selectedScore != null) {
                int scoreID = getScoreID(selectedScore);
                if (scoreID != -1) {
                    submitReview(bookID, comment, scoreID);
                    commentField.clear();
                } else {
                    showAlert("Error", "Selected score is not valid.");
                }
            } else {
                showAlert("Error", "Please fill in all fields before submitting.");
            }
        });

        VBox reviewsVBox = new VBox(5);
        reviewsVBox.setStyle("-fx-padding: 10; -fx-spacing: 5;");

        ScrollPane scrollPane = new ScrollPane(reviewsVBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setMinHeight(250);

        loadReviews(bookID, reviewsVBox);

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {openReadList(primaryStage);});
        vbox.getChildren().addAll(titleLabel, descriptionLabel, languageLabel, authorLabel, publisherLabel, imageView,
                commentField, scoreComboBox, submitButton, scrollPane, backButton);

        Scene scene = new Scene(vbox, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Book Details");
        primaryStage.show();
    }

    private String getBookDetail(int bookID, String field) {
        String detail = "N/A";
        String sql = "SELECT " + field + " FROM " + field + " WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int fieldID = getFieldID(bookID, field);
            stmt.setInt(1, fieldID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    detail = rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return detail;
    }

    private int getFieldID(int bookID, String field) {
        int fieldID = -1;
        String sql = "SELECT " + field + "_id FROM book WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    fieldID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fieldID;
    }

    private void loadScores(ComboBox<String> scoreComboBox) {
        String sql = "SELECT score FROM score";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String scoreValue = rs.getString("score");
                scoreComboBox.getItems().add(scoreValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getScoreID(String scoreValue) {
        int scoreID = -1;
        String sql = "SELECT id FROM score WHERE score = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, scoreValue);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    scoreID = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scoreID;
    }

    private void submitReview(int bookID, String comment, int scoreID) {
        String sql = "INSERT INTO review (book, user_id, comment, score) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookID);
            stmt.setInt(2, userID);
            stmt.setString(3, comment);
            stmt.setInt(4, scoreID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Your review has been submitted successfully.");
            } else {
                showAlert("Error", "An error occurred while submitting your review.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while submitting your review.");
        }
    }

    private void loadReviews(int bookID, VBox reviewsVBox) {
        String sql = "SELECT r.comment, u.username, r.score FROM review r JOIN users u ON r.user_id = u.id WHERE r.book = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    String comment = rs.getString("comment");
                    int scoreID = rs.getInt("score");
                    String scoreValue = getScoreValue(scoreID);

                    String reviewText = username + ": " + comment + " (Score: " + scoreValue + ")";

                    Label reviewLabel = new Label(reviewText);
                    reviewLabel.setStyle("-fx-font-weight: bold; -fx-wrap-text: true;");
                    reviewsVBox.getChildren().add(reviewLabel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getScoreValue(int scoreID) {
        String scoreValue = "N/A";
        String sql = "SELECT score FROM score WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scoreID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    scoreValue = rs.getString("score");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scoreValue;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openReadList(Stage primaryStage) {
        ReadList resultPage = new ReadList();
        resultPage.showResultPage(primaryStage);
    }
}
