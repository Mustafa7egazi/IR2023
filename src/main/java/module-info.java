module com.example.ir2023 {
    requires javafx.controls;
    requires javafx.fxml;
    requires lucene.core;
    requires lucene.analyzers.common;
    requires lucene.queryparser;


    opens com.example.ir2023 to javafx.fxml;
    exports com.example.ir2023;
}