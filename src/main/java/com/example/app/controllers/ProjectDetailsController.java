package com.example.app.controllers;

import com.example.app.model.PersonDTO;
import com.example.app.model.PersonTableDTO;
import com.example.app.model.Project;

import com.example.app.util.SessionManager;
import com.example.app.util.requests.RequestsNeo4j;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectDetailsController {
    @FXML
    public AnchorPane projectPane;

    @FXML
    public TableView<PersonTableDTO> teamTableView;

    @FXML
    public TableColumn<PersonTableDTO, String>  usernameColum;

    @FXML
    public TableColumn<PersonTableDTO, String>  roleColum;

    @FXML
    private Label projectName;

    @FXML
    private TextField projectNameInputField;

    private Project selectedProject;

    public void initialize() throws IOException, InterruptedException {
        selectedProject = ProjectsController.getSelectedProject();
        projectName.setText(selectedProject.getName());
        projectNameInputField.setText(selectedProject.getName());

        usernameColum.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleColum.setCellValueFactory(new PropertyValueFactory<>("role"));

        usernameColum.setResizable(false);
        roleColum.setResizable(false);

        renderMembers(selectedProject.getId());
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

    @FXML
    public void clickedOnAddMember(ActionEvent actionEvent) {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Новый проект");
        dialog.setHeaderText("Введите название проекта");

        // Кнопки
        ButtonType saveButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Поле ввода
        TextField textField = new TextField();
        textField.setPromptText("Имя пользователя");

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

        result.ifPresent(memberName -> {
            long memberId;
            try {
                memberId = RequestsNeo4j.getPersonIdByUserName(memberName);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (memberId != -1){
                try {
                    RequestsNeo4j.addMembersToProject(selectedProject.getId(),memberId);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                showAlert("error 404", "пользователь не найден");
            }

            try {
                renderMembers(selectedProject.getId());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void clickedOnDelMember(ActionEvent actionEvent) {
        showAlert("Test", "clickedOnDelMember");
    }

    @FXML
    public void clickedOnDelProject(ActionEvent actionEvent) {
        showAlert("Test", "clickedOnDelProject");
    }

    @FXML
    public void clickedOnSaveProject(ActionEvent actionEvent) {
        showAlert("Test", "clickedOnSaveProject");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void renderMembers(long projectId) throws IOException, InterruptedException {
        List<PersonDTO> members = RequestsNeo4j.getAllMembersInProjectByProjectId(projectId);
        List<Long> personIds = RequestsNeo4j.getProjectById(projectId);
        List<PersonTableDTO> memberTableDTOs = new ArrayList<>();

        for (PersonDTO member : members) {
            if (personIds.get(0) == member.getId()) {
                memberTableDTOs.add(new PersonTableDTO(member.getName(), "Создатель"));
            } else if (personIds.contains(member.getId())) {
                memberTableDTOs.add(new PersonTableDTO(member.getName(), "Админ"));
            } else {
                memberTableDTOs.add(new PersonTableDTO(member.getName(), "Участник"));
            }

        }
        ObservableList<PersonTableDTO> observableList = FXCollections.observableList(memberTableDTOs);

        teamTableView.setItems(observableList);
    }
}
