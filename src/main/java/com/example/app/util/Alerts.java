package com.example.app.util;

import javafx.scene.control.Alert;

public class Alerts {

    //    NONE,
    //    INFORMATION,
    //    WARNING,
    //    CONFIRMATION,
    //    ERROR;

    public static void alert(String title, String header, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void alert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
