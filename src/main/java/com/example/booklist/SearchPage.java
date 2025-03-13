package com.example.booklist;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import javafx.stage.Stage;

public class SearchPage {

    public void showSearchPage(Stage primaryStage) {
        Label searchLabel = new Label("Search:");
        TextField searchField = new TextField();

        searchField.setPrefWidth(200);

        Button searchButton = new Button("Search");
        Button logOutButton = new Button("LogOut");

        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER);
        searchContainer.getChildren().addAll(searchLabel, searchField, searchButton);

        searchButton.setOnAction(e -> handleSearch(searchField.getText(), primaryStage));
        logOutButton.setOnAction(e -> openLoginPage(primaryStage));

        UserPanel userPanel = new UserPanel(primaryStage);

        VBox searchLayout = new VBox(10);
        searchLayout.setAlignment(Pos.CENTER);
        searchLayout.getChildren().addAll(userPanel,searchContainer,logOutButton);

        Scene searchScene = new Scene(searchLayout, 400, 300);
        primaryStage.setScene(searchScene);
    }

    private void handleSearch(String query, Stage primaryStage) {
        if (query != null && !query.trim().isEmpty()) {
            openResultPage(primaryStage, query);
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Please enter a search query.", ButtonType.OK);
            errorAlert.showAndWait();
        }
    }

    private void openResultPage(Stage primaryStage, String query) {
        ResultPage resultPage = new ResultPage();
        resultPage.showResultPage(primaryStage, query);
    }

    private void openLoginPage(Stage primaryStage) {
        LoginApp loginPage = new LoginApp();
        loginPage.start(primaryStage);
    }
}
