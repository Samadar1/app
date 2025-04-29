package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AuthController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private AnchorPane authPane;

    @FXML
    private void handleLogin() throws Exception {
        // Мнимая авторизация
        Parent mainView = FXMLLoader.load(getClass().getResource("/com/example/app/views/main_view.fxml"));
        authPane.getScene().setRoot(mainView);
    }

    @FXML
    private void handleRegister() {
        // Мнимая регистрация
        System.out.println("Registration clicked");
    }
}