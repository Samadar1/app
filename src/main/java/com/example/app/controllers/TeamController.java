package com.example.app.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class TeamController implements Initializable {
    @FXML
    private ListView<String> teamListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Инициализация списка участников
        ObservableList<String> teamMembers = FXCollections.observableArrayList(
                "Участник 1", "Участник 2", "Участник 3"
        );
        teamListView.setItems(teamMembers);
    }
}