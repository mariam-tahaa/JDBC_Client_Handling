module com.mycompany.clienthandl {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.mycompany.clienthandl to javafx.fxml;
    exports com.mycompany.clienthandl;
}
