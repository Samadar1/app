package com.example.app.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ProjectController {
    @FXML
    private Label projectName;

    @FXML
    private TextField projectNameInputField;

    @FXML
    private ListView<String> teamListView;

    public void initialize() {
        projectName.setText("Project Name");

        projectNameInputField.setText("Project Name");

        ObservableList<String> teamMembers = FXCollections.observableArrayList(

        );

        teamListView.setItems(teamMembers);

    }
}
