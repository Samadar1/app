package com.example.app.controllers;

import com.example.app.util.SessionManager;
import com.example.app.util.TextEncoderDecoder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

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
                HttpClient client = HttpClient.newHttpClient();
                String json = String.format(
                        "{\"username\":\"%s\",\"password\":\"%s\"}",
                        login, password
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8000/auth/sign-in"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode()   != 200) {
                    System.out.println("Request URL: " + request.uri());
                    System.out.println("Response Code: " + response.statusCode());
                    System.out.println("Response Body: " + response.body());
                    throw new IOException("Login failed: " + response.body());
                }
                String jwt = new ObjectMapper().readTree(response.body()).get("token").asText();
                SessionManager.setUsername(login);



                HttpRequest requestNeo4j = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/Person/get-person-by-name/"+ SessionManager.getUsername()))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();


                HttpResponse<String> responseNeo4j = client.send(requestNeo4j, HttpResponse.BodyHandlers.ofString());

                ObjectMapper objectMapper = new ObjectMapper();

                List<Map<String, Object>> userList = objectMapper.readValue(
                        responseNeo4j.body(),
                        new TypeReference<List<Map<String, Object>>>() {}
                );

                Map<String, Object> user = userList.get(0);


                long id = Long.parseLong(user.get("id").toString());
                SessionManager.setUserId(id);


                String baseUrl = "http://localhost:8000/auth/"+ SessionManager.getUsername() + "/email";

                HttpRequest requestEmail = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl))
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> responseEmail = client.send(requestEmail, HttpResponse.BodyHandlers.ofString());
                SessionManager.setEmail(responseEmail.body());


                if (rememberMe.isSelected()){
                    TextEncoderDecoder.encodeAndSave(jwt);
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