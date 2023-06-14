module com.example.microgimp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.microgimp to javafx.fxml;
    exports com.example.microgimp;
}