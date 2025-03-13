package com.example.booklist;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPanel extends HBox {

    private Label label;
    private Button button;
    private Button profile;

    public UserPanel(Stage primaryStage) {

        label = new Label("Name: " + UserID.getUserID());
        int userID = UserID.getUserID();

        String query = "SELECT name FROM users WHERE id = ?";

        try (PreparedStatement stmt = ConnectionFactory.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userName = rs.getString("name");

                    label.setText("Name: " + userName);
                } else {
                    label.setText("Name: Not found");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            label.setText("Error retrieving name");
        }

        button = new Button("Go to read list");
        profile = new Button("Profile");

        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);

        this.getChildren().addAll(label, button, profile);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        this.getChildren().add(spacer);

        button.setOnAction(e -> {
            openReadList(primaryStage);
        });

        profile.setOnAction(e -> {
            openProfile(primaryStage);
        });
    }

    private void openReadList(Stage primaryStage) {
        ReadList resultPage = new ReadList();
        resultPage.showResultPage(primaryStage);
    }

    private void openProfile(Stage primaryStage) {
        Profile profilePage = new Profile();
        profilePage.profilePage(primaryStage);
    }

    public Label getLabel() {
        return label;
    }

    public Button getButton() {
        return button;
    }
}
