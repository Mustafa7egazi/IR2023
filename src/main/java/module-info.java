module com.example.ir2023 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ir2023 to javafx.fxml;
    exports com.example.ir2023;
}