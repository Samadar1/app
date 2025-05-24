package com.example.app.model;

public class Project {
    private String name;
    private long id;

    public Project(String name) {
        this.name = name;
    }

    public Project(long id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
