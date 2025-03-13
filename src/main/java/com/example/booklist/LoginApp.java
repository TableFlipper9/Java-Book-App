package com.example.booklist;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class LoginApp{

    public void start(Stage primaryStage) {

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(200);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(200);

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Button backButton = new Button("Back to Main Page");

        registerButton.setOnAction(e -> openRegisterWindow());

        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(), primaryStage));

        backButton.setOnAction(e -> primaryStage.setScene(createMainPageScene(primaryStage)));

        VBox loginLayout = new VBox(10);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, backButton, registerButton);

        Scene loginScene = new Scene(loginLayout, 300, 250);
        primaryStage.setScene(loginScene);
    }

    private void handleLogin(String username, String password, Stage primaryStage) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = ConnectionFactory.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    int userID = rs.getInt("id");

                    UserID.setUserID(userID);

                    openSearchPage(primaryStage);
                } else {
                    showLoginError();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showDatabaseError();
        }
    }


    private void showLoginError() {
        Alert errorAlert = new Alert(AlertType.ERROR, "Invalid Username or Password", ButtonType.OK);
        errorAlert.showAndWait();
    }

    private void showDatabaseError() {
        Alert errorAlert = new Alert(AlertType.ERROR, "Database Connection Error", ButtonType.OK);
        errorAlert.showAndWait();
    }

    private void openSearchPage(Stage primaryStage) {
        SearchPage searchPage = new SearchPage();
        searchPage.showSearchPage(primaryStage);
    }

    private Scene createMainPageScene(Stage primaryStage) {
        Button goToLoginButton = new Button("Go to Login Page");

        goToLoginButton.setOnAction(e -> start(primaryStage));

        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().add(goToLoginButton);

        return new Scene(mainLayout, 300, 200);
    }

    private void openRegisterWindow() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Register New User");

        Label newUsernameLabel = new Label("New Username:");
        TextField newUsernameField = new TextField();
        newUsernameField.setPrefWidth(200);

        Label newNameLabel = new Label("Enter Name:");
        TextField newNameField = new TextField();
        newUsernameField.setPrefWidth(200);

        Label newEmailLabel = new Label("Enter email:");
        TextField newEmailField = new TextField();
        newUsernameField.setPrefWidth(200);

        Label newPasswordLabel = new Label("New Password:");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPrefWidth(200);

        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPrefWidth(200);

        Button registerButton = new Button("Register");

        registerButton.setOnAction(e -> {
            try {
                handleRegister(newUsernameField.getText(), newPasswordField.getText(), confirmPasswordField.getText(),newNameField.getText(), newEmailField.getText());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox registerLayout = new VBox(10);
        registerLayout.setAlignment(Pos.CENTER);
        registerLayout.getChildren().addAll(newNameLabel, newNameField, newEmailLabel, newEmailField, newUsernameLabel, newUsernameField, newPasswordLabel, newPasswordField,
                confirmPasswordLabel, confirmPasswordField, registerButton);

        Scene registerScene = new Scene(registerLayout, 300, 300);
        registerStage.setScene(registerScene);
        registerStage.show();
    }

    private void handleRegister(String username, String password, String confirmPassword,String name,String email) throws SQLException {
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "No field can be empty.", ButtonType.OK);
            errorAlert.showAndWait();
        } else if (!password.equals(confirmPassword)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Passwords do not match.", ButtonType.OK);
            errorAlert.showAndWait();
        } else {


            String emailCheckQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement psEmail = ConnectionFactory.getConnection().prepareStatement(emailCheckQuery)) {
                psEmail.setString(1, email);
                try (ResultSet rs = psEmail.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Email is already registered.", ButtonType.OK);
                        errorAlert.showAndWait();
                        return;
                    }
                }
            }

            String nameCheckQuery = "SELECT COUNT(*) FROM users WHERE name = ?";
            try (PreparedStatement psName = ConnectionFactory.getConnection().prepareStatement(nameCheckQuery)) {
                psName.setString(1, name);
                try (ResultSet rs = psName.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Name is already taken.", ButtonType.OK);
                        errorAlert.showAndWait();
                        return;
                    }
                }
            }

            String getMaxIdQuery = "SELECT MAX(id) FROM users";
            int newId = 1;
            try (PreparedStatement psMaxId = ConnectionFactory.getConnection().prepareStatement(getMaxIdQuery);
                 ResultSet rsMaxId = psMaxId.executeQuery()) {
                if (rsMaxId.next()) {
                    newId = rsMaxId.getInt(1) + 1;
                }
            }

            String insertQuery = "INSERT INTO users (id, username, password, name, email) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psInsert = ConnectionFactory.getConnection().prepareStatement(insertQuery)) {
                psInsert.setInt(1, newId);
                psInsert.setString(2, username);
                psInsert.setString(3, password);
                psInsert.setString(4, name);
                psInsert.setString(5, email);

                int rowsAffected = psInsert.executeUpdate();
                if (rowsAffected > 0) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Registration Successful!", ButtonType.OK);
                    successAlert.showAndWait();
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("account_check_email")) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Email format is invalid. Please provide a valid email.", ButtonType.OK);
                    errorAlert.showAndWait();
                } else {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "An error occurred during registration.", ButtonType.OK);
                    errorAlert.showAndWait();
                }
            }
        }
    }
}
