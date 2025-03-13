package com.example.booklist;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class Profile {

    private TextField nameField;

    public void profilePage(Stage primaryStage) {
        Label profileLabel = new Label("Profile");

        nameField = new TextField();
        nameField.setPromptText("Enter new name");

        Button changeNameButton = new Button("Change Name");
        changeNameButton.setOnAction(e -> changeUserName());

        Button deleteAccountButton = new Button("Delete Account");
        deleteAccountButton.setOnAction(e -> deleteUserAccount(primaryStage));

        Button searchButton = new Button("Close");
        searchButton.setOnAction(e -> openSearchPage(primaryStage));

        VBox profileLayout = new VBox(10);
        profileLayout.setAlignment(Pos.CENTER);
        profileLayout.getChildren().addAll(profileLabel, nameField, changeNameButton, deleteAccountButton, searchButton);

        Scene profileScene = new Scene(profileLayout, 400, 300);
        primaryStage.setScene(profileScene);
    }


    private void changeUserName() {
        String newName = nameField.getText();
        if (newName.isEmpty()) {
            showError("Name cannot be empty.");
            return;
        }

        int userID = UserID.getUserID();
        String checkNameQuery = "SELECT COUNT(*) FROM users WHERE name = ? AND id != ?";
        String updateNameQuery = "UPDATE users SET name = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkNameQuery)) {


            checkStmt.setString(1, newName);
            checkStmt.setInt(2, userID);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {

                    showError("The name is already taken by another user.");
                    return;
                }
            }


            try (PreparedStatement updateStmt = conn.prepareStatement(updateNameQuery)) {
                updateStmt.setString(1, newName);
                updateStmt.setInt(2, userID);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    showSuccess("Name changed successfully.");
                } else {
                    showError("Failed to change name.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error updating name.");
        }
    }


    private void deleteUserAccount(Stage primaryStage) {
        int userID = UserID.getUserID();
        String deleteQuery = "DELETE FROM users WHERE id = ?";
        String deleteFromReadListQuery = "DELETE FROM read_list WHERE user_id = ?";
        String deleteFromReviewQuery = "DELETE FROM review WHERE user_id = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement deleteFromReadListStmt = conn.prepareStatement(deleteFromReadListQuery)) {
                deleteFromReadListStmt.setInt(1, userID);
                deleteFromReadListStmt.executeUpdate();
            }

            try (PreparedStatement deleteFromReviewStmt = conn.prepareStatement(deleteFromReviewQuery)) {
                deleteFromReviewStmt.setInt(1, userID);
                deleteFromReviewStmt.executeUpdate();
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, userID);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    conn.commit();
                    showSuccess("Your account has been deleted.");
                    openLoginPage(primaryStage);
                } else {
                    conn.rollback();
                    showError("Error deleting account.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                ConnectionFactory.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            showError("Error deleting account.");
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

    private void openSearchPage(Stage primaryStage) {
        SearchPage searchPage = new SearchPage();
        searchPage.showSearchPage(primaryStage);
    }

    private void openLoginPage(Stage primaryStage) {
        LoginApp loginApp = new LoginApp();
        loginApp.start(primaryStage);
    }
}
