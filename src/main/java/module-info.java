module com.company {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.company.controller to javafx.fxml;
    opens com.company.model to javafx.base;
    opens com.company.servicedata to javafx.fxml;

    exports com.company;
}