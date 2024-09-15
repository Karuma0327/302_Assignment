module com.socslingo {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.socslingo.controllers to javafx.fxml;
    exports com.socslingo;
    exports com.socslingo.controllers; 

}