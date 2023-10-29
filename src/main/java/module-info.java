module com.company {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.company.Controller to javafx.fxml;
    opens com.company.Model to javafx.base;
    opens com.company.ServiceData to javafx.fxml;

    exports com.company;
}