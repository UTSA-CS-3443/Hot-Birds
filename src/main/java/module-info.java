module edu.utsa.cs3443.hotbirds {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.utsa.cs3443.hotbirds.controllers to javafx.fxml;
    exports edu.utsa.cs3443.hotbirds;
}