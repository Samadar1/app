package com.example.app.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class PersonTableDTO {
    private final SimpleStringProperty name;
    private final SimpleStringProperty role;

    public PersonTableDTO(String name,  String role) {
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
    }

    public void setName(String name) { this.name.set(name); }
    public SimpleStringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }

    public void setRole(String role) { this.role.set(role); }
    public SimpleStringProperty roleProperty() { return role; }
    public String getRole() { return role.get(); }
}
