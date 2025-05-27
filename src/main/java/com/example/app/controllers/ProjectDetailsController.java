package com.example.app.controllers;

import com.example.app.model.DTO.PersonDTO;
import com.example.app.model.DTO.PersonTableDTO;
import com.example.app.model.DTO.TaskTableDTO;
import com.example.app.model.Project;

import com.example.app.util.Alerts;
import com.example.app.util.SessionManager;
import com.example.app.util.requests.RequestsNeo4j;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public Button addMembers;

    @FXML
    public Button createTask;

    @FXML
    public Button getTask;

    @FXML
    public Button delTask;

    @FXML
    public Tab setting;

    @FXML
    public TabPane tabPane;

    @FXML
    public ListView openTasks;

    @FXML
    public TableView<TaskTableDTO> taskInProgressTableView;

    @FXML
    public TableColumn<TaskTableDTO, String> taskNameInProgress;

    @FXML
    public TableColumn<TaskTableDTO, String> userNameInProgress;

    @FXML
    public TableView<TaskTableDTO> taskClosedTableView;

    @FXML
    public TableColumn<TaskTableDTO, String> taskNameClose;

    @FXML
    public TableColumn<TaskTableDTO, String> userNameClose;

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

        ContextMenu contextMenu = new ContextMenu();

        MenuItem upRolePerson = new MenuItem("Повысить пользователя");
        MenuItem downRolePerson = new MenuItem("Понизить пользователя");
        MenuItem delPerson = new MenuItem("Удалить пользователя");

        contextMenu.getItems().addAll(upRolePerson, downRolePerson, delPerson);


        List<Long> personIds = RequestsNeo4j.getProjectById(selectedProject.getId());
        if (!personIds.contains(SessionManager.getUserId())) {
            addMembers.setVisible(false);
            tabPane.getTabs().remove(setting);
        }

        if (personIds.contains(SessionManager.getUserId())) {
            teamTableView.setRowFactory(tv -> {
                TableRow<PersonTableDTO> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty()) {
                        PersonTableDTO rowData = row.getItem();
                        String name = rowData.getName();
                        String role = rowData.getRole();
                        if (event.getButton() == MouseButton.SECONDARY && !Objects.equals(name, SessionManager.getUsername())) {



                            upRolePerson.setOnAction(e -> {
                                try {
                                    RequestsNeo4j.upRolePersonInProject(selectedProject.getId(), RequestsNeo4j.getPersonIdByUserName(name));
                                } catch (IOException | InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                try {
                                    renderMembers(selectedProject.getId());
                                } catch (IOException | InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                            });

                            downRolePerson.setOnAction(e -> {
                                try {
                                    RequestsNeo4j.downRolePersonInProject(selectedProject.getId(), RequestsNeo4j.getPersonIdByUserName(name));
                                } catch (IOException | InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                try {
                                    renderMembers(selectedProject.getId());
                                } catch (IOException | InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                            });

                            delPerson.setOnAction(event1 -> {
                                long nodeId;
                                try {
                                    nodeId = RequestsNeo4j.getPersonIdByUserName(name);
                                } catch (IOException | InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                int statusCode;

                                try {
                                    statusCode = RequestsNeo4j.deleteMembersInProject(selectedProject.getId(), nodeId, SessionManager.getUserId());
                                } catch (IOException | InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                if (statusCode == 200) {
                                    Alerts.alert("Уведомление", "Пользователь успешно удалён", Alert.AlertType.INFORMATION);
                                } else {
                                    Alerts.alert("Уведомление", "У вас нет прав что бы удалить данного пользователя", Alert.AlertType.INFORMATION);
                                }


                                try {
                                    renderMembers(selectedProject.getId());
                                } catch (IOException | InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                            contextMenu.show(teamTableView, event.getScreenX(), event.getScreenY());
                        }
                    }
                });

                return row;
            });
        }

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
        dialog.setTitle("Добавление участника");
        dialog.setHeaderText("Введите имя пользователя");

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
    public void clickedOnDelProject(ActionEvent actionEvent) {
        showAlert("Test", "clickedOnDelProject");
    }

    @FXML
    public void clickedOnSaveProject(ActionEvent actionEvent) {
        showAlert("Test", "clickedOnSaveProject");
    }

    @FXML
    public void clickedOnCreateTask(ActionEvent actionEvent) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Добавление задачи");
        dialog.setHeaderText("Введите название и описание задачи");

        // Кнопки
        ButtonType saveButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Поле ввода
        TextField textFieldName = new TextField();
        textFieldName.setPromptText("Название задачи");
        TextField textFieldDescription = new TextField();
        textFieldDescription.setPromptText("Описание задачи");


        VBox content = new VBox(10, textFieldName, textFieldDescription);
        dialog.getDialogPane().setContent(content);

        // Преобразование результата
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                List<String> task= new ArrayList<>();
                task.add(textFieldName.getText());
                task.add(textFieldDescription.getText());
                return task;
            }
            return null;
        });

        // Показываем диалог и обрабатываем результат
        Optional<List<String>> result = dialog.showAndWait();

        result.ifPresent(task -> {
            try {
                long taskId = RequestsNeo4j.createTask(task.get(0),task.get(1));
                if (taskId != -1) {
                    RequestsNeo4j.connectTaskToProject(selectedProject.getId(), taskId);
                    Alerts.alert("Добавление задачи", "задача добавленна", Alert.AlertType.INFORMATION);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
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
