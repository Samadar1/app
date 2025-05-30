package com.example.app;

import com.example.app.util.requests.Requests;
import com.example.app.util.requests.*;
import com.example.app.util.SessionManager;
import com.example.app.util.TextEncoderDecoder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    /*
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
        if (!TextEncoderDecoder.is_empty()) {
            String decodedJWT = TextEncoderDecoder.decodeFromFile();
            String username = Requests.checkJWT(decodedJWT);

            if (username != null) {
                SessionManager.setUsername(username);
                SessionManager.setUserId(RequestsNeo4j.getPersonIdByUserName(username));
                SessionManager.setEmail(Requests.getEmailByUserName(username));

                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/app/views/main_view.fxml")));
                primaryStage.setScene(new Scene(root, 800, 600));
            }

        } else {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/app/views/auth.fxml")));
            primaryStage.setScene(new Scene(root, 800, 600));
        }

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/favicon.png")));

        primaryStage.setTitle("Horizon Code");
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}