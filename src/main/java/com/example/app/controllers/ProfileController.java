package com.example.app.controllers;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;


public class ProfileController implements Initializable {
    @FXML
    private CheckComboBox<String> checkComboBox;

    public void initialize(URL url, ResourceBundle rb) {
        // Заполняем данными
        checkComboBox.getItems().addAll("Элемент 1", "Элемент 2", "Элемент 3", "Элемент 4", "Элемент 5");

        // Слушатель на изменение выбранных элементов
        checkComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
            int count = checkComboBox.getCheckModel().getCheckedItems().size();
            if (count == 0) {
                checkComboBox.setTitle("Выберите значения из списка");
            } else if (count <= 3) {
                checkComboBox.setTitle(String.join(", ", checkComboBox.getCheckModel().getCheckedItems()));
            } else {
                checkComboBox.setTitle("Выбрано элементов: " + count);
            }
        });


    }
}

