package com.example.app.controllers;

import com.example.app.model.Project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class ProjectDetailsController {
    @FXML
    public AnchorPane projectPane;

    @FXML
    private Label projectName;

    @FXML
    private TextField projectNameInputField;

    @FXML
    private ListView<String> teamListView;

   private Project selectedProject;


    public void initialize() {
        selectedProject= ProjectsController.getSelectedProject();
        projectName.setText(selectedProject.getName());
        projectNameInputField.setText(selectedProject.getName());

        ObservableList<String> teamMembers = FXCollections.observableArrayList(

        );

        teamListView.setItems(teamMembers);

    }

    @FXML
    public void clickedOnBack(ActionEvent actionEvent) {
        try {
            // Используем FXMLLoader для загрузки FXML и получения контроллера
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/projects.fxml"));
            AnchorPane newView = loader.load();
            projectPane.getChildren().setAll(newView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
