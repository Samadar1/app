package com.example.app.controllers;

import com.example.app.model.Task;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class TaskController {

    @FXML
    public AnchorPane taskPane;

    @FXML
    public Label labelNameTask;

    @FXML
    public Label description;

    private Task selectedTask;

    public void initialize() {
        selectedTask = ProjectDetailsController.getSelectedTask();
        labelNameTask.setText("Подробная информация о задаче (" +selectedTask.getName() +")");
        if (selectedTask.getDescription() != "") {
            description.setText(selectedTask.getDescription());
        } else {
            description.setText("У задачи нет описания");
        }
    }

    @FXML
    public void clickedOnSaveTask(ActionEvent actionEvent) {
    }

    @FXML
    public void clickedOnDelTask(ActionEvent actionEvent) {
    }

    @FXML
    public void clickedOnBack(ActionEvent actionEvent) {
    }
}
