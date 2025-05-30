package com.example.app.model;

import java.util.ArrayList;
import java.util.List;

public class ProjectNode {
    private String name;
    private boolean isFile;
    private String content; // Только если isFile == true
    private ProjectNode parent; // Для навигации назад
    private List<ProjectNode> children = new ArrayList<>(); // Только если isFile == false

    public ProjectNode(String name, boolean isFile) {
        this.name = name;
        this.isFile = isFile;
    }

    public void addChild(ProjectNode child) {
        child.parent = this;
        children.add(child);
    }

    public boolean isFile() {
        return isFile;
    }

    public void setContent(String fileContent) {
        content = fileContent;
    }

    public String getContent() {
        return content;
    }

    public List<ProjectNode> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }
}
