package com.example.app.controllers;

import com.example.app.model.DTO.PersonDTO;
import com.example.app.model.DTO.PersonTableDTO;
import com.example.app.model.Project;
import com.example.app.model.Task;
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
    @FXML public AnchorPane projectPane;

    @FXML public TableView<PersonTableDTO> teamTableView;
    @FXML public TableColumn<PersonTableDTO, String>  usernameColum;
    @FXML public TableColumn<PersonTableDTO, String>  roleColum;

    @FXML public ListView<Task> openTasks;

    @FXML public TableView<Task> taskInProgressTableView;
    @FXML public TableColumn<Task , String> taskNameInProgress;
    @FXML public TableColumn<Task , String> userNameInProgress;

    @FXML public TableView<Task> taskClosedTableView;
    @FXML public TableColumn<Task , String> taskNameClose;
    @FXML public TableColumn<Task , String> userNameClose;

    @FXML public Button addMembers;
    @FXML public Button createTask;
    @FXML public Button taskAI;

    @FXML public Tab setting;
    @FXML public TabPane tabPane;

    @FXML private Label projectName;
    @FXML private TextField projectNameInputField;

    private Project selectedProject;
    private static Task selectedTask;
    private static String statusSelectedTask;

    private final ContextMenu contextMenuTeam = new ContextMenu();
    private final MenuItem upRolePerson = new MenuItem("Повысить пользователя");
    private final MenuItem downRolePerson = new MenuItem("Понизить пользователя");
    private final MenuItem delPerson = new MenuItem("Удалить пользователя");

    private final ContextMenu contextMenuOpenTask = new ContextMenu();
    private final MenuItem setMember = new MenuItem("Назначить исполнителя");
    private final MenuItem takeTask = new MenuItem("Взять задачу");
    private final MenuItem deleteOpenTask = new MenuItem("Удалить");

    private final ContextMenu contextMenuInProgressTask = new ContextMenu();
    private final MenuItem closeTask = new MenuItem("Сдать задачу");
    private final MenuItem dropTask = new MenuItem("Отказаться от задачи");
    private final MenuItem deleteTask = new MenuItem("Удалить");

    public void initialize() throws IOException, InterruptedException {
        selectedProject = ProjectsController.getSelectedProject();
        List<Long> personIds = RequestsNeo4j.getProjectById(selectedProject.getId());
        
        projectName.setText(selectedProject.getName());
        projectNameInputField.setText(selectedProject.getName());

        if (!personIds.contains(SessionManager.getUserId())) {
            addMembers.setVisible(false);
            tabPane.getTabs().remove(setting);
            createTask.setVisible(false);
            taskAI.setVisible(false);
        }

        setupTeamCellFactory(personIds);

        setupOpenTaskCellFactory();
        setupOpenTaskClickHandlers(personIds);

        setupInProgressTaskClickHandlers(personIds);

        setupCloseTaskCellFactory();

        renderMembers(selectedProject.getId());
        renderOpenTasksListView(selectedProject.getId());
        renderCloseTasksTableView(selectedProject.getId());
        renderInProgressTasksTableView(selectedProject.getId());
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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return textField.getText();
            }
            return null;
        });

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
                Alerts.alert("Уведомление", "Пользователь не найден", Alert.AlertType.INFORMATION);
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
        Alerts.alert("123", "123", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void clickedOnSaveProject(ActionEvent actionEvent) {
        Alerts.alert("123", "123", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void clickedOnTaskAI(ActionEvent actionEvent) throws IOException, InterruptedException {
        RequestsNeo4j.generateTask(selectedProject.getId());

        renderOpenTasksListView(selectedProject.getId());
        renderInProgressTasksTableView(selectedProject.getId());
    }

    @FXML
    public void clickedOnCreateTask(ActionEvent actionEvent) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Добавление задачи");
        dialog.setHeaderText("Введите название и описание задачи");

        ButtonType saveButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        TextField textFieldName = new TextField();
        textFieldName.setPromptText("Название задачи");
        TextField textFieldDescription = new TextField();
        textFieldDescription.setPromptText("Описание задачи");

        VBox content = new VBox(10, textFieldName, textFieldDescription);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                List<String> task= new ArrayList<>();
                task.add(textFieldName.getText());
                task.add(textFieldDescription.getText());
                return task;
            }
            return null;
        });

        Optional<List<String>> result = dialog.showAndWait();

        result.ifPresent(task -> {
            try {
                long taskId = RequestsNeo4j.createTask(task.get(0),task.get(1));
                if (taskId != -1) {
                    RequestsNeo4j.connectTaskToProject(selectedProject.getId(), taskId);
                    renderOpenTasksListView(selectedProject.getId());
                    Alerts.alert("Добавление задачи", "задача добавленна", Alert.AlertType.INFORMATION);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void renderMembers(long projectId) throws IOException, InterruptedException {
        List<PersonDTO> members = RequestsNeo4j.getAllMembersInProjectByProjectId(projectId);
        List<Long> personIds = RequestsNeo4j.getProjectById(projectId);
        List<PersonTableDTO> memberTableDTOs = new ArrayList<>();

        for (PersonDTO member : members) {
            if (Objects.equals(personIds.get(0), member.getId())) {
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

    private void renderOpenTasksListView(long projectId) throws IOException, InterruptedException {
        openTasks.getItems().clear();
        List<Task> tasks = RequestsNeo4j.getAllOpenTasksByProjectId(projectId);
        if (tasks != null) {
            openTasks.getItems().addAll(tasks);
        }
    }

    private void renderInProgressTasksTableView(long projectId) throws IOException, InterruptedException {
        List<Task> tasks = RequestsNeo4j.getAllInProgressTasksByProjectId(projectId);
        taskInProgressTableView.getItems().clear();
        if (tasks != null) {
            ObservableList<Task> taskList = FXCollections.observableArrayList(tasks);
            taskInProgressTableView.setItems(taskList);
        }
    }
    
    private void renderCloseTasksTableView(long projectId) throws IOException, InterruptedException {
        List<Task> tasks = RequestsNeo4j.getAllClosedTasksByProjectId(projectId);
        taskClosedTableView.getItems().clear();
        if (tasks != null) {
            ObservableList<Task> taskList = FXCollections.observableArrayList(tasks);
            taskClosedTableView.setItems(taskList);
        }
    }

    private void renderTaskPane(Task task) {
        selectedTask = task;
        try {
            // Используем FXMLLoader для загрузки FXML и получения контроллера
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/task.fxml"));
            AnchorPane newView = loader.load();
            // Обновляем содержимое
            projectPane.getChildren().setAll(newView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Task getSelectedTask() {
        return selectedTask;
    }

    public static String getStatusSelectedTask() {
        return statusSelectedTask;
    }


    /*
    ##################################################
    ###         Методы контексного меню            ###
    ##################################################
    */

    //////------contextMenuTeam------//////

    private void upRolePerson(String name) throws IOException, InterruptedException {
        RequestsNeo4j.upRolePersonInProject(selectedProject.getId(), RequestsNeo4j.getPersonIdByUserName(name));
        renderMembers(selectedProject.getId());
    }

    private void downRolePerson(String name) throws IOException, InterruptedException {
        RequestsNeo4j.downRolePersonInProject(selectedProject.getId(), RequestsNeo4j.getPersonIdByUserName(name));
        renderMembers(selectedProject.getId());
    }

    private void delPersonInProject(String name) throws IOException, InterruptedException {
        long nodeId = RequestsNeo4j.getPersonIdByUserName(name);
        int statusCode = RequestsNeo4j.deleteMembersInProject(selectedProject.getId(), nodeId, SessionManager.getUserId());

        if (statusCode != 200) {
            Alerts.alert("Уведомление", "У вас нет прав что бы удалить данного пользователя", Alert.AlertType.INFORMATION);
        }

        Alerts.alert("Уведомление", "Пользователь успешно удалён", Alert.AlertType.INFORMATION);
        renderMembers(selectedProject.getId());
    }

    //////------contextMenuOpenTask------//////

    private void takeTask() throws IOException, InterruptedException {
            RequestsNeo4j.setTaskMember(SessionManager.getUserId(), selectedTask.getId());
            renderOpenTasksListView(selectedProject.getId());
            renderInProgressTasksTableView(selectedProject.getId());
    }

    private void setMember() throws IOException, InterruptedException {

    }

    private void deleteTask(Task task) throws IOException, InterruptedException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтвердите действие");
        alert.setHeaderText("Вы точно хотите удалить задачу?");
        alert.setContentText("Это действие нельзя отменить.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            RequestsNeo4j.deleteTask(SessionManager.getUserId(), task.getId());
            Alerts.alert("Оповешение", "Задача была удалена" , Alert.AlertType.INFORMATION);

            renderOpenTasksListView(selectedProject.getId());
            renderInProgressTasksTableView(selectedProject.getId());
            renderCloseTasksTableView(selectedProject.getId());
        }
    }
    
    //////------contextMenuInProgressTask------//////
    
    private void dropTask(Task task) throws IOException, InterruptedException {
        if (Objects.equals(task.getMemberName(), SessionManager.getUsername())){
            RequestsNeo4j.openTask(task.getId(), SessionManager.getUserId());
            renderOpenTasksListView(selectedProject.getId());
            renderInProgressTasksTableView(selectedProject.getId());
            Alerts.alert("Уведомление", "Вы отказались от задачи", Alert.AlertType.INFORMATION);
        } else {
            Alerts.alert("Уведомление", "Вы не являетесь исполнителем задачи", Alert.AlertType.INFORMATION);
        }
    }
    
    private void closeTask(Task task) throws IOException, InterruptedException {
        if (Objects.equals(task.getMemberName(), SessionManager.getUsername())){
            RequestsNeo4j.closeTask(task.getId(), SessionManager.getUserId());
            renderInProgressTasksTableView(selectedProject.getId());
            renderCloseTasksTableView(selectedProject.getId());
            Alerts.alert("Уведомление", "Задача успешно сдана", Alert.AlertType.INFORMATION);
        } else {
            Alerts.alert("Уведомление", "Вы не являетесь исполнителем задачи", Alert.AlertType.INFORMATION);
        }
    }
    
    /*
    ##################################################
    ###         Отображение таблиц                 ###
    ##################################################
   */

    //////------TeamTableView------//////

    private void setupTeamCellFactory(List<Long> personIds) {
        usernameColum.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleColum.setCellValueFactory(new PropertyValueFactory<>("role"));

        usernameColum.setResizable(false);
        roleColum.setResizable(false);

        contextMenuTeam.getItems().addAll(upRolePerson, downRolePerson, delPerson);

        teamTableView.setRowFactory(tv -> {
            TableRow<PersonTableDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    PersonTableDTO rowData = row.getItem();
                    String name = rowData.getName();

                    if (event.getButton() == MouseButton.SECONDARY && !Objects.equals(name, SessionManager.getUsername())) {
                        upRolePerson.setOnAction(e -> {
                            try {
                                upRolePerson(name);
                            } catch (IOException | InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        });

                        downRolePerson.setOnAction(e -> {
                            try {
                                downRolePerson(name);
                            } catch (IOException | InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        });

                        delPerson.setOnAction(e -> {
                            try {
                                delPersonInProject(name);
                            } catch (IOException | InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        if (personIds.contains(SessionManager.getUserId())) {
                            contextMenuTeam.show(teamTableView, event.getScreenX(), event.getScreenY());
                        }
                    }
                }
            });
            return row;
        });
    }

    //////------OpenTaskListView------//////
    
    private void setupOpenTaskCellFactory() {
        openTasks.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    setText(task.getName());
                }
            }
        });
    }

    private void setupOpenTaskClickHandlers(List<Long> personIds) {
        if (personIds.contains(SessionManager.getUserId())){
            contextMenuOpenTask.getItems().addAll(setMember, takeTask, deleteOpenTask);
        } else {
            contextMenuOpenTask.getItems().addAll(takeTask);
        }

        openTasks.setOnMouseClicked(event -> {
            Task selected = openTasks.getSelectionModel().getSelectedItem();
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (selected != null) {
                    selectedTask = selected;
                    statusSelectedTask = "OPEN";
                    renderTaskPane(selectedTask);
                }
            }

            if (event.getButton() == MouseButton.SECONDARY && openTasks.getSelectionModel().getSelectedItem() != null) {
                selectedTask = selected;

                setMember.setOnAction(e -> {
                    try {
                        setMember();
                    } catch (IOException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                takeTask.setOnAction(e -> {
                    try {
                        takeTask();
                    } catch (IOException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                deleteOpenTask.setOnAction(e -> {
                    try {
                        deleteTask(selectedTask);
                    } catch (IOException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                contextMenuOpenTask.show(openTasks, event.getScreenX(), event.getScreenY());
            }
        });
    }

    //////------InProgressTaskTableView------//////
    
    private void setupInProgressTaskClickHandlers(List<Long> personIds) {
        if (personIds.contains(SessionManager.getUserId())){
            contextMenuInProgressTask.getItems().addAll(closeTask, dropTask, deleteTask);
        } else {
            contextMenuInProgressTask.getItems().addAll(dropTask, closeTask);
        }

        taskNameInProgress.setCellValueFactory(new PropertyValueFactory<>("name"));
        userNameInProgress.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        taskNameInProgress.setResizable(false);
        userNameInProgress.setResizable(false);

        taskInProgressTableView.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Task rowData = row.getItem();
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        selectedTask = rowData;
                        statusSelectedTask = "IN PROGRESS";
                        renderTaskPane(selectedTask);
                    }

                    if (event.getButton() == MouseButton.SECONDARY) {
                        closeTask.setOnAction(e ->{
                            try {
                                closeTask(rowData);
                            } catch (IOException | InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        
                        deleteTask.setOnAction(e -> {
                            try {
                                deleteTask(rowData);
                            } catch (IOException | InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        
                        dropTask.setOnAction(e -> {
                            try {
                                dropTask(rowData);
                            } catch (IOException | InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        contextMenuInProgressTask.show(taskInProgressTableView, event.getScreenX(), event.getScreenY());
                    }
                }
            });
            return row;
        });
    }

    //////------CloseTaskTableView------//////

    private void setupCloseTaskCellFactory() {
        taskNameClose.setCellValueFactory(new PropertyValueFactory<>("name"));
        userNameClose.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        taskNameClose.setResizable(false);
        taskNameClose.setResizable(false);

        taskClosedTableView.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Task rowData = row.getItem();
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        selectedTask = rowData;
                        statusSelectedTask = "CLOSE";
                        renderTaskPane(selectedTask);
                    }
                }
            });
            return row;
        });
    }


}