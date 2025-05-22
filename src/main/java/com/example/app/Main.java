package com.example.app;

import com.example.app.util.SessionManager;
import com.example.app.util.TextEncoderDecoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Objects;


public class Main extends Application {


    /**
     * Запуск приложения
     * проверка файла с JWT
     * если он пустой переходим на авторизацию
     * Если там есть JWT Token декодируем его и отправляем его на сервер
     * в ответ получаем имя пользователя
     * 200 открываем меню
     * 404 открываем авторизацию
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        String requestUrl = "http://localhost:8000/api/auth/check-jwt";
        if (!TextEncoderDecoder.is_empty()) {
            String decodedJWT = TextEncoderDecoder.decodeFromFile();

            String json = String.format(
                    "{\"token\":\"%s\"}",
                    decodedJWT
            );

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8000/auth/check-jwt"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String username = new ObjectMapper().readTree(response.body()).get("username").asText();
            SessionManager.setUsername(username);

            if (response.statusCode() == 200) {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/app/views/main_view.fxml")));
                primaryStage.setTitle("JavaFX App");
                primaryStage.setScene(new Scene(root, 800, 600));
                primaryStage.show();
            }
            else {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/app/views/auth.fxml")));
                primaryStage.setTitle("JavaFX App");
                primaryStage.setScene(new Scene(root, 800, 600));
                primaryStage.show();
            }

        }
        else {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/app/views/auth.fxml")));
            primaryStage.setTitle("JavaFX App");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
        }


    }

    public static void main(String[] args) {
        launch(args);
    }
}