package com.example.app.controllers;

import com.example.app.util.SessionManager;
import com.example.app.util.TextEncoderDecoder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SettingsController{

    @FXML private AnchorPane settingsPane;

    @FXML
    private void onLogoutButtonClick(ActionEvent actionEvent) throws IOException {
        SessionManager.removeAuthToken();
        TextEncoderDecoder.clearFileContent();
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/app/views/auth.fxml")));
                settingsPane.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}