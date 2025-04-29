package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private AnchorPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Загружаем профиль по умолчанию
        loadView("profile");
    }

    private void loadView(String viewName) {
        try {
            AnchorPane newView = (AnchorPane) FXMLLoader.load(
                    getClass().getResource("/com/example/app/views/" + viewName + ".fxml")
            );
            contentArea.getChildren().setAll(newView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToProfile() { loadView("profile"); }

    @FXML
    private void switchToSettings() { loadView("settings"); }

    @FXML
    private void switchToTeam() { loadView("team"); }

    @FXML
    private void switchToProjects() { loadView("projects"); }
}