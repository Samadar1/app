package com.example.app.controllers;

import com.example.app.util.Alerts;
import com.example.app.util.requests.RequestsNeo4j;
import com.example.app.util.SessionManager;
import com.example.app.model.Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectsController {
    @FXML private GridPane projectGrid;
    @FXML private AnchorPane projectsPane;

    private static Project selectedProject;

    public void initialize() throws IOException, InterruptedException {
        generateProjectCards(RequestsNeo4j.getAllUsersProjectsByUserId(SessionManager.getUserId()));
    }

    @FXML
    public void clickedOnCreateProject(ActionEvent actionEvent) throws Exception{
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Новый проект");
        dialog.setHeaderText("Введите название проекта");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        TextField textField = new TextField();
        textField.setPromptText("Название проекта");

        VBox content = new VBox(10, textField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return textField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(projectName -> {
              Project project = new Project(projectName);
              long id;
            try {
                id = RequestsNeo4j.createProject(project , SessionManager.getUserId());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (id != -1) {
                try {
                    generateProjectCards(RequestsNeo4j.getAllUsersProjectsByUserId(SessionManager.getUserId()));
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Alerts.alert("Предупреждение","Такой проект уже есть", Alert.AlertType.WARNING);
            }
        });
    }

    private void generateProjectCards(List<Project> projects) throws IOException, InterruptedException {
        int row = 0;
        int col = 0;

        for (Project project : projects) {
            // Создаем контейнер для карточки
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #365073; -fx-padding: 15; -fx-background-radius: 10;");
            card.setMinSize(200, 100);
            card.setMaxSize(200, 100);

            Label label = new Label(project.getName());
            label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:#F1FCFF;");

            Label labelRole = new Label();
            labelRole.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill:#F1FCFF;");

            List<Long> PersonIds = new ArrayList<>();
            PersonIds = RequestsNeo4j.getProjectById(project.getId());
            if (PersonIds.get(0) == SessionManager.getUserId()) {
                labelRole.setText("Создатель");
            } else if (PersonIds.contains(SessionManager.getUserId())) {
                labelRole.setText("Админ");
            } else {
                labelRole.setText("Участник");
            }

            Button button = getButton(project);

            card.setAlignment(javafx.geometry.Pos.CENTER);
            card.getChildren().addAll(label, labelRole, button);

            // Добавляем на сетку
            projectGrid.add(card, col, row);

            col++;
            if (col == 5) {
                col = 0;
                row++;
            }
        }
    }

    private Button getButton(Project project) {
        Button button = new Button("Подробнее");
        button.setUserData(project);
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        button.setOnAction(event -> {
            selectedProject = project;

            try {
                // Используем FXMLLoader для загрузки FXML и получения контроллера
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/project.fxml"));
                AnchorPane newView = loader.load();
                // Обновляем содержимое
                projectsPane.getChildren().setAll(newView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return button;
    }

    public static Project getSelectedProject() {
        return selectedProject;
    }
}