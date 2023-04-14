package com.example.ir2023;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ResultOfDocumentsList implements Initializable {


    @FXML
    private ListView<String> listViewOfResults;


    @FXML
    private AnchorPane searchResultWindow;

    @FXML
    void onOkBtnClicked(ActionEvent event) throws IOException {
        searchResultWindow.getScene().getWindow().hide();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewOfResults.getItems().addAll(HomeController.resultToShow);
    }
}
