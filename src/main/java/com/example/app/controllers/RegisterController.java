package com.example.app.controllers;

import com.example.app.util.Alerts;
import com.example.app.util.requests.Requests;
import com.example.app.util.requests.RequestsNeo4j;
import com.example.app.util.SessionManager;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RegisterController {
    @FXML private AnchorPane registerPane;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (!validateInputs(name, email, password)) return;

        Task<String> registerTask = createRegisterTask(name, email, password);
        setupTaskHandlers(registerTask);
        new Thread(registerTask).start();
    }

    private boolean validateInputs(String name, String email, String password) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Alerts.alert("Предупреждение","Проверте правильность заполнения полей", Alert.AlertType.WARNING);
            return false;
        }
        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            Alerts.alert("Предупреждение","Некорректная почта", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private Task<String> createRegisterTask(String name, String email, String password) {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                String jwt = Requests.signUp(name, email, password);

                if (jwt != null) {
                    SessionManager.setUsername(name);
                    SessionManager.setEmail(email);
                    SessionManager.setUserId(RequestsNeo4j.createUser(name));

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
                Alerts.alert("Предупреждение", task.getException().getMessage(), Alert.AlertType.ERROR))
        );
    }

    private void loadMainView() {
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/app/views/main_view.fxml")));
                Stage stage = (Stage) registerPane.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/app/views/auth.fxml")));
        Stage stage = (Stage) registerPane.getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}