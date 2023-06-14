module com.example.microgimp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires java.desktop;

    opens com.example.microgimp to javafx.fxml;
    exports com.example.microgimp;
}