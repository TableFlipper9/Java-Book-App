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
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class ListItem extends VBox {

    private ImageView imageView;
    private Text titleText;
    private Text descriptionText;
    private Button actionButton;
    private Button bookPageButton;

    public ListItem(Stage primaryStage, int bookID, String title, String description, String imageUrl, String buttonText) {

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
        actionButton.setOnAction(e -> onActionButtonClicked(bookID, primaryStage));

        bookPageButton = new Button("See book details");
        bookPageButton.setOnAction(e -> openBookButtonClicked(primaryStage, bookID, title, description, imageUrl));

        this.setSpacing(8);
        this.setStyle("-fx-padding: 8px; -fx-background-color: lightgray; -fx-border-radius: 5px; -fx-border-color: gray;");
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(350);
        this.setPrefWidth(50);
        this.getChildren().addAll(imageView, titleText, descriptionText, actionButton, bookPageButton);
    }

    private void onActionButtonClicked(int bookID, Stage primaryStage) {
        int userID = UserID.getUserID();

        String deleteQuery = "DELETE FROM read_list WHERE user_id = ? AND book = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, userID);
            stmt.setInt(2, bookID);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {

                System.out.println("Successfully removed the book from your read list.");
                showSuccess("The book has been removed from your read list.");
            } else {
                System.out.println("No such book is linked to your read list.");
                showError("This book is not in your read list.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while trying to remove the book.");
        }

        openReadList(primaryStage);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void openReadList(Stage primaryStage) {
        ReadList resultPage = new ReadList();
        resultPage.showResultPage(primaryStage);
    }

    private void openBookButtonClicked(Stage primaryStage, int bookID, String title, String description, String imageUrl) {
        BookPage bookPage = new BookPage(primaryStage, bookID, title, description, imageUrl);
    }
}
