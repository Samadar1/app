module com.example.app {
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires org.controlsfx.controls;
    requires java.desktop;


    opens com.example.app.controllers to javafx.fxml;

    opens com.example.app to javafx.fxml;
    exports com.example.app;
    exports com.example.app.model to javafx.base;
    opens com.example.app.model to javafx.base;
    exports com.example.app.model.DTO to javafx.base;
    opens com.example.app.model.DTO to javafx.base;
}