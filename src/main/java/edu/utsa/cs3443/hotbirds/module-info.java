module edu.utsa.cs3443.espi_test_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.utsa.cs3443.espi_test_project to javafx.fxml;
    exports edu.utsa.cs3443.espi_test_project;
}