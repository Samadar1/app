package com.example.app.controllers;

import com.example.app.util.requests.Requests;
import com.example.app.util.SessionManager;
import com.example.app.util.TextEncoderDecoder;
import com.example.app.util.requests.RequestsNeo4j;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthController {
    @FXML private CheckBox rememberMe;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private AnchorPane authPane;

    @FXML
    private void handleLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill all fields");
            return;
        }

        Task<String> loginTask = createLoginTask(login, password);
        setupTaskHandlers(loginTask);
        new Thread(loginTask).start();
    }

    private Task<String> createLoginTask(String login, String password) {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                String jwt = Requests.signIn(login, password);

                if (jwt != null) {
                    SessionManager.setUsername(login);
                    SessionManager.setUserId(RequestsNeo4j.getPersonIdByUserName(login));
                    SessionManager.setEmail(Requests.getEmailByUserName(login));
                    if (rememberMe.isSelected()){
                        TextEncoderDecoder.encodeAndSave(jwt);
                    }
                } else {
                    throw new IOException("Login failed: " );
                }
                return jwt;
            }
        };
    }

    private void setupTaskHandlers(Task<String> task) {
        task.setOnSucceeded(e -> {
            SessionManager.setAuthToken(task.getValue());
            loadMainView();
        });

        task.setOnFailed(e -> Platform.runLater(() ->
                showAlert("Error", task.getException().getMessage()))
        );
    }

    private void loadMainView() {
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/app/views/main_view.fxml"));
                authPane.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void handleRegister() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/app/views/register_view.fxml"));
        Stage stage = (Stage) authPane.getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}