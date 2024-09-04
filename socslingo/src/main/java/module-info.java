module com.socslingo {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.socslingo.controllers to javafx.fxml;
    exports com.socslingo;
}