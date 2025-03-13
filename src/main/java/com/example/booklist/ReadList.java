package com.example.booklist;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.sql.*;

public class ReadList {

    public void showResultPage(Stage primaryStage) {
        Label resultLabel = new Label("Read List");

        VBox resultListLayout = new VBox(10);
        resultListLayout.setAlignment(Pos.TOP_CENTER);
        resultListLayout.setPrefWidth(400);

        int userID = UserID.getUserID();

        String sql = "SELECT b.id, b.book_title, b.book_description FROM book b " +
                "JOIN read_list r ON b.id = r.book WHERE r.user_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    Label noResultsLabel = new Label("No books in your read list.");
                    resultListLayout.getChildren().add(noResultsLabel);
                } else {
                    do {
                        String title = rs.getString("book_title");
                        String description = rs.getString("book_description");
                        int bookID = rs.getInt("id");

                        ListItem listItem = new ListItem(
                                primaryStage,
                                bookID,
                                title,
                                description,
                                "https://via.placeholder.com/100",
                                "Delete from read list"
                        );
                        resultListLayout.getChildren().add(listItem);
                    } while (rs.next());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error retrieving your read list.");
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

        Scene resultScene = new Scene(resultLayout, 400, 350);
        primaryStage.setScene(resultScene);
    }

    private void createSearchPageScene(Stage primaryStage) {
        SearchPage searchPage = new SearchPage();
        searchPage.showSearchPage(primaryStage);
    }
}
