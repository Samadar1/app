package com.example.app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.example.app.model.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectsController {
    @FXML
    private GridPane projectGrid;

    // Пример данных
    private List<Project> projects = new ArrayList<>();

    public void initialize() {
        // Заполнение тестовыми данными
        for (int i = 1; i <= 100; i++) {
            projects.add(new Project("Проект #" + i));
        }

        generateProjectCards();
    }

    @FXML
    public void clickedOnCreateProject(ActionEvent actionEvent) {
        
    }

    private void generateProjectCards() {
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