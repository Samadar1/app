package com.example.app.controllers;

import com.example.app.util.SessionManager;
import com.example.app.util.TextEncoderDecoder;
import com.example.app.util.requests.RequestsNeo4j;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SettingsController{
    @FXML
    private AnchorPane settingsPane;

    @FXML
    private CheckComboBox<String> checkComboBox;

    // Метод initialize будет автоматически вызван FXMLLoader
    public void initialize() {
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

    @FXML
    private void onLogoutButtonClick(ActionEvent actionEvent) throws IOException {
        SessionManager.removeAuthToken();
        TextEncoderDecoder.clearFileContent();
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/app/views/auth.fxml")));
                settingsPane.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    public void onUpdateProfile(ActionEvent actionEvent) throws IOException, InterruptedException {
        List<String> checkedItems = checkComboBox.getCheckModel().getCheckedItems();
        RequestsNeo4j.updateSkillSetById(checkedItems, SessionManager.getUserId());
    }
}