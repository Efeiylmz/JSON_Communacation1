module org.example.guiapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.guiapplication to javafx.fxml;
    exports org.example.guiapplication;
}