package com.example.app.controllers;

import com.example.app.model.Task;

import com.example.app.util.Alerts;
import com.example.app.util.SessionManager;
import com.example.app.util.requests.RequestsNeo4j;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TaskController {

    @FXML
    public AnchorPane taskPane;

    //Связанное с именнем
    @FXML
    public Label labelNameTask;

    @FXML
    public Button nameButtonEdit;

    @FXML
    public Button nameSaveButton;

    @FXML
    public Button nameCancelButtonEdit;

    @FXML
    public TextField textFieldNameTask;

    //Связанное с описанием
    @FXML
    public VBox textContainer;

    @FXML
    public Button descriptionCancelButtonEdit;

    @FXML
    public Button descriptionButtonEdit;

    @FXML
    public Button descriptionSaveButton;

    @FXML
    public Label description;

    @FXML
    public TextArea editField;

    //Всё остальное
    private Task selectedTask;

    public void initialize() {
        selectedTask = ProjectDetailsController.getSelectedTask();
        labelNameTask.setText("Название задачи: " + selectedTask.getName());
        if (selectedTask.getDescription() != "") {
            description.setText(selectedTask.getDescription());
        } else {
            description.setText("У задачи нет описания");
        }
    }

    // Кнопки связанные с именнем
    @FXML
    public void onNameEditButtonClick(ActionEvent actionEvent) {
        // Скрываем Label
        labelNameTask.setVisible(false);
        labelNameTask.setManaged(false);

        // Показываем TextField и кнопку Сохранить
        textFieldNameTask.setText(selectedTask.getName());
        textFieldNameTask.setVisible(true);
        textFieldNameTask.setManaged(true);

        nameSaveButton.setVisible(true);
        nameSaveButton.setManaged(true);

        nameCancelButtonEdit.setVisible(true);
        nameCancelButtonEdit.setManaged(true);

        nameButtonEdit.setVisible(false);
        nameButtonEdit.setManaged(false);

        textFieldNameTask.requestFocus();
    }

    @FXML
    public void onNameSaveButtonClick(ActionEvent actionEvent) throws IOException, InterruptedException {
        String name = textFieldNameTask.getText();
        if (!name.isEmpty()) {
            labelNameTask.setText("Название задачи: " + name);

            selectedTask.setName(name);

            textFieldNameTask.setVisible(false);
            textFieldNameTask.setManaged(false);

            nameSaveButton.setVisible(false);
            nameSaveButton.setManaged(false);

            nameCancelButtonEdit.setVisible(false);
            nameCancelButtonEdit.setManaged(false);

            nameButtonEdit.setVisible(true);
            nameButtonEdit.setManaged(true);

            labelNameTask.setVisible(true);
            labelNameTask.setManaged(true);

            RequestsNeo4j.taskChangeTitle(selectedTask.getId(), SessionManager.getUserId(), name);
        } else {
            Alerts.alert("Предупреждение", "Название задачи не может быть пустым", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onNameCancelEdit(ActionEvent actionEvent) {
        textFieldNameTask.setVisible(false);
        textFieldNameTask.setManaged(false);

        nameSaveButton.setVisible(false);
        nameSaveButton.setManaged(false);

        nameCancelButtonEdit.setVisible(false);
        nameCancelButtonEdit.setManaged(false);

        nameButtonEdit.setVisible(true);
        nameButtonEdit.setManaged(true);

        labelNameTask.setVisible(true);
        labelNameTask.setManaged(true);
    }

    //Кнопки связаанные с описанием проекта
    @FXML
    public void onDescriptionEditButtonClick(ActionEvent actionEvent) {
        // Скрываем Label
        description.setVisible(false);
        description.setManaged(false);

        // Показываем TextField и кнопку Сохранить
        editField.setText(description.getText());
        editField.setVisible(true);
        editField.setManaged(true);
        descriptionSaveButton.setDisable(false);
        descriptionSaveButton.setVisible(true);
        editField.requestFocus(); // фокус на поле ввода

        descriptionButtonEdit.setVisible(false);
        descriptionButtonEdit.setManaged(false);

        descriptionCancelButtonEdit.setVisible(true);
    }

    @FXML
    public void onDescriptionSaveButtonClick(ActionEvent actionEvent) throws IOException, InterruptedException {
        String newText = editField.getText().trim();

        if (!newText.isEmpty()) {
            description.setText(newText);
            selectedTask.setDescription(newText);
        } else {
            selectedTask.setDescription("");
            description.setText("У задачи нет описания");
        }

        RequestsNeo4j.taskChangeContent(selectedTask.getId(), SessionManager.getUserId(), newText);

        // Возвращаем обратно Label
        editField.setVisible(false);
        editField.setManaged(false);

        description.setVisible(true);
        description.setManaged(true);

        descriptionCancelButtonEdit.setVisible(false);

        descriptionButtonEdit.setVisible(true);
        descriptionSaveButton.setVisible(false);
    }

    @FXML
    public void onDescriptionCancelEdit(ActionEvent actionEvent) {
        // Просто скрываем TextArea, не сохраняя изменения
        description.setVisible(true);
        description.setManaged(true);

        editField.setVisible(false);
        editField.setManaged(false);
        descriptionSaveButton.setDisable(true);
        descriptionSaveButton.setVisible(false);

        descriptionButtonEdit.setVisible(true);
        descriptionCancelButtonEdit.setVisible(false);
    }

    //Остальные кнопки
    @FXML
    public void clickedOnDelTask(ActionEvent actionEvent) {
    }

    @FXML
    public void clickedOnBack(ActionEvent actionEvent) {
        try {
            // Используем FXMLLoader для загрузки FXML и получения контроллера
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/project.fxml"));
            AnchorPane newView = loader.load();
            taskPane.getChildren().setAll(newView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
