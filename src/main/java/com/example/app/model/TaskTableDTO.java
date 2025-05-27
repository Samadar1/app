package com.example.app.model;

import javafx.beans.property.SimpleStringProperty;

public class TaskTableDTO {
    private final SimpleStringProperty open;
    private final SimpleStringProperty inProgress;
    private final SimpleStringProperty close;

    public TaskTableDTO(String open, String inProgress, String close) {
        this.open = new SimpleStringProperty(open);
        this.inProgress = new SimpleStringProperty(inProgress);
        this.close = new SimpleStringProperty(close);
    }

    public String getOpen() { return open.get(); }
    public String getInProgtess() { return inProgress.get(); }
    public String getClose() { return close.get(); }

    public void setOpen(String open) { this.open.set(open); }
    public void setInProgress(String inProgress) { this.inProgress.set(inProgress); }
    public void setClose(String close) { this.close.set(close); }

    public SimpleStringProperty openProperty() { return open; }
    public SimpleStringProperty inProgressProperty() { return inProgress; }
    public SimpleStringProperty closeProperty() { return close; }
}
