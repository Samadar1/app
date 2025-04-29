module com.example.app {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.app.controllers to javafx.fxml;
    opens com.example.app to javafx.fxml;
    exports com.example.app;
}