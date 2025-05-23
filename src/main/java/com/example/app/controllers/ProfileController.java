package com.example.app.controllers;


import com.example.app.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileController {

    @FXML
    Label Email;

    @FXML
    Label Username;

    public void initialize() {
        Username.setText("Имя пользователя: " + SessionManager.getUsername());
        Email.setText("Email: " + SessionManager.getEmail());
    }


}

