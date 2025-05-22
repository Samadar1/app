module com.example.app {
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires org.controlsfx.controls;


    opens com.example.app.controllers to javafx.fxml;
    opens com.example.app to javafx.fxml;
    exports com.example.app;
}