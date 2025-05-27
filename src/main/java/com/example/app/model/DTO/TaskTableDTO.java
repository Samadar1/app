package com.example.app.model.DTO;

import javafx.beans.property.SimpleStringProperty;

public class TaskTableDTO {
    private final SimpleStringProperty taskName;
    private final SimpleStringProperty userName;

    public TaskTableDTO(String taskName, String userName) {
        this.taskName = new SimpleStringProperty(taskName);
        this.userName = new SimpleStringProperty(userName);
    }
    public String getTaskName() { return taskName.get(); }
    public void setTaskName(String taskName) { this.taskName.set(taskName); }
    public SimpleStringProperty taskNameProperty() { return taskName; }

    public String getUserName() { return userName.get(); }
    public void setUserName(String userName) { this.userName.set(userName); }
    public SimpleStringProperty userNameProperty() { return userName; }
}
