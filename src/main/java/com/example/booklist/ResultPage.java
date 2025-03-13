package com.example.booklist;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;

import java.sql.*;

public class ResultPage {

    public void showResultPage(Stage primaryStage, String query) {
        Label resultLabel = new Label("Search Results for: " + query);

        VBox resultListLayout = new VBox(10);
        resultListLayout.setAlignment(Pos.TOP_CENTER);
        resultListLayout.setPrefWidth(350);

        String sql = "SELECT id, book_title, book_description FROM book WHERE book_title LIKE ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + query + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    do {
                        String title = rs.getString("book_title");
                        String description = rs.getString("book_description");
                        int bookID = rs.getInt("id");

                        ResultItem resultItem = new ResultItem(
                                bookID,
                                title,
                                description,
                                "https://via.placeholder.com/100",
                                "Add to read list"
                        );
                        resultListLayout.getChildren().add(resultItem);
                    } while (rs.next());
                } else {
                    Label noResultsLabel = new Label("No books found");
                    resultListLayout.getChildren().add(noResultsLabel);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error retrieving books from database.");
            resultListLayout.getChildren().add(errorLabel);
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(resultListLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setMaxWidth(350);
        scrollPane.setStyle("-fx-padding: 20px;");

        Button backButton = new Button("Back to Search Page");
        backButton.setOnAction(e -> createSearchPageScene(primaryStage));

        VBox resultLayout = new VBox(10);
        resultLayout.setAlignment(Pos.CENTER);
        resultLayout.getChildren().addAll(resultLabel, scrollPane, backButton);

        resultLayout.setAlignment(Pos.CENTER);

        Scene resultScene = new Scene(resultLayout, 400, 300);
        primaryStage.setScene(resultScene);
    }

    private void createSearchPageScene(Stage primaryStage) {
        SearchPage searchPage = new SearchPage();
        searchPage.showSearchPage(primaryStage);
    }

}
