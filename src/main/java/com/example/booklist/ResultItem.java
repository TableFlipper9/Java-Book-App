package com.example.booklist;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Pos;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ResultItem extends VBox {
    private ImageView imageView;
    private Text titleText;
    private Text descriptionText;
    private Button actionButton;

    public ResultItem(int bookID, String title, String description, String imageUrl, String buttonText) {
        titleText = new Text(title);
        titleText.setFont(new Font("Arial", 14));
        titleText.setFill(Color.BLACK);
        titleText.setWrappingWidth(250);

        descriptionText = new Text(description);
        descriptionText.setFont(new Font("Arial", 12));
        descriptionText.setFill(Color.DARKGRAY);
        descriptionText.setWrappingWidth(250);

        Image image = new Image(imageUrl);
        imageView = new ImageView(image);
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);

        actionButton = new Button(buttonText);
        actionButton.setOnAction(e -> onActionButtonClicked(bookID));

        this.setSpacing(8);
        this.setStyle("-fx-padding: 8px; -fx-background-color: lightgray; -fx-border-radius: 5px; -fx-border-color: gray;");
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(350);
        this.setPrefWidth(50);
        this.getChildren().addAll(imageView, titleText, descriptionText, actionButton);
    }

    private void onActionButtonClicked(int bookID) {
        int userID = UserID.getUserID();

        String checkQuery = "SELECT * FROM read_list WHERE user_id = ? AND book = ?";

        String insertQuery = "INSERT INTO read_list (user_id, book) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, userID);
            checkStmt.setInt(2, bookID);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    showError("This book is already in your read list.");
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, userID);
                        insertStmt.setInt(2, bookID);
                        insertStmt.executeUpdate();

                        showSuccess("Book added to your read list.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while processing your request.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}
