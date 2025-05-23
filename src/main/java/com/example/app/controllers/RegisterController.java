package com.example.app.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import com.example.app.util.SessionManager;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RegisterController {
    @FXML
    private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private AnchorPane registerPane;

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Валидация
        if (!validateInputs(name, email, password)) return;

        Task<String> registerTask = createRegisterTask(name, email, password);
        setupTaskHandlers(registerTask);
        new Thread(registerTask).start();
    }

    private boolean validateInputs(String name, String email, String password) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required");
            return false;
        }
        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Error", "Invalid email format");
            return false;
        }
        return true;
    }

    private Task<String> createRegisterTask(String name, String email, String password) {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                String role = "ROLE_USER";
                String json = String.format(
                        "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                        name, email, password, role
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8000/auth/sign-up"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    System.out.println("Request URL: " + request.uri());
                    System.out.println("Response Code: " + response.statusCode());
                    System.out.println("Response Body: " + response.body());
                    throw new IOException("Registration failed: " + response.body());
                }
                ObjectMapper mapper = new ObjectMapper();

                ObjectNode rootNode = mapper.createObjectNode();
                rootNode.put("name", name);

                ArrayNode skillSet = mapper.createArrayNode(); // Пустой массив
                rootNode.set("skillSet", skillSet);

                String jsonNeo4j = mapper.writeValueAsString(rootNode);

                HttpRequest requestNeo4j = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/Person/create"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonNeo4j))
                        .build();

                HttpResponse<String> responseNeo4j = client.send(requestNeo4j, HttpResponse.BodyHandlers.ofString());
                System.out.println("Request URL: " + request.uri());
                System.out.println(jsonNeo4j);
                System.out.println("Response Code: " + responseNeo4j.statusCode());
                System.out.println("Response Body: " + responseNeo4j.body());

                SessionManager.setUsername(new ObjectMapper().readTree(responseNeo4j.body()).get("name").asText());
                SessionManager.setUserId(new ObjectMapper().readTree(responseNeo4j.body()).get("id").asLong());
                System.out.println(SessionManager.getUserId());

                return new ObjectMapper().readTree(response.body()).get("token").asText();
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
                Stage stage = (Stage) registerPane.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/app/views/auth.fxml"));
        Stage stage = (Stage) registerPane.getScene().getWindow();
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