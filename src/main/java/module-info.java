module fsd.assignment.assignment1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens fsd.assignment.assignment to javafx.fxml;
    exports fsd.assignment.assignment;
}