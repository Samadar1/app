package com.example.app.controllers;

import com.example.app.util.Requests;
import com.example.app.util.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.example.app.model.Project;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProjectsController {
    @FXML
    private GridPane projectGrid;

    @FXML
    private TextField inputField;
    ;

    public void initialize() throws IOException, InterruptedException {

        generateProjectCards(Requests.getAllProjectsFromDB());

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
                Requests.addProjectToDB(project);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            
            try {
                generateProjectCards(Requests.getAllProjectsFromDB());
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

            Label label = new Label(project.getName());
            label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            // Пример графического элемента
            Rectangle rectangle = new Rectangle(40, 20, Color.LIGHTBLUE);
            HBox graphicBox = new HBox(rectangle);
            graphicBox.setAlignment(javafx.geometry.Pos.CENTER);

            card.getChildren().addAll(graphicBox, label);

            // Добавляем на сетку
            projectGrid.add(card, col, row);

            col++;
            if (col == 2) {
                col = 0;
                row++;
            }
        }
    }
}