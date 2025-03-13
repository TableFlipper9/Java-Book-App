package com.example.booklist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {

        Button goToLoginButton = new Button("Go to Login Page");

        goToLoginButton.setOnAction(e -> openLoginPage(primaryStage));

        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().add(goToLoginButton);

        Scene mainScene = new Scene(mainLayout, 300, 200);

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Main Page");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void openLoginPage(Stage primaryStage) {
        LoginApp loginPage = new LoginApp();
        loginPage.start(primaryStage);
    }
}

//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//
//        Button goToLoginButton = new Button("Go to Login Page");
//
//
//        goToLoginButton.setOnAction(e -> openLoginPage(stage));
//
//        stage.show();
//    }

//String sql = "SELECT * FROM book WHERE id = ?";
//
//        try (PreparedStatement stmt = ConnectionFactory.getConnection().prepareStatement(sql)) {
//
//        stmt.setInt(1, 1);
//
//
//            try (ResultSet rs = stmt.executeQuery()) {
//        if (rs.next()) {
//
//String booktitle = rs.getString("book_title");
//                    System.out.println("Person created with ID: " + booktitle);
//                }
//                        }
//                        } catch (SQLException e) {
//        e.printStackTrace();
//        }