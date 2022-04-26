module com.example.wordleclone {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.wordleclone to javafx.fxml;
    exports com.example.wordleclone;
}