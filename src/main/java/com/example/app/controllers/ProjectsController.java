package com.example.app.controllers;

import com.example.app.util.requests.RequestsNeo4j;
import com.example.app.util.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.example.app.model.Project;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProjectsController {
    @FXML
    private GridPane projectGrid;

    @FXML
    private AnchorPane projectsPane;

    @FXML
    private TextField inputField;

    private static Project selectedProject;

    public void initialize() throws IOException, InterruptedException {
        generateProjectCards(RequestsNeo4j.getAllProjectsFromDB());
    }

    @FXML
    public void clickedOnCreateProject(ActionEvent actionEvent) throws Exception{
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Новый проект");
        dialog.setHeaderText("Введите название проекта");

        // Кнопки
        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Поле ввода
        TextField textField = new TextField();
        textField.setPromptText("Название проекта");

        VBox content = new VBox(10, textField);
        dialog.getDialogPane().setContent(content);

        // Преобразование результата
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return textField.getText();
            }
            return null;
        });

        // Показываем диалог и обрабатываем результат
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(projectName -> {
              Project project = new Project(projectName);
            try {
                RequestsNeo4j.addProjectToDB(project , SessionManager.getUserId());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                generateProjectCards(RequestsNeo4j.getAllProjectsFromDB());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void generateProjectCards(List<Project> projects) {
        int row = 0;
        int col = 0;

        for (Project project : projects) {
            // Создаем контейнер для карточки
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 15; -fx-border-color: #ccc;");
            card.setMinSize(200, 100);
            card.setMaxSize(200, 100);

            Label label = new Label(project.getName());
            label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:#000;");

            Button button = getButton(project);

            card.setAlignment(javafx.geometry.Pos.CENTER);
            card.getChildren().addAll(label, button);

            // Добавляем на сетку
            projectGrid.add(card, col, row);

            col++;
            if (col == 2) {
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