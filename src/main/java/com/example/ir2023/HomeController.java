package com.example.ir2023;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {


    @FXML
    private AnchorPane homePage;

    @FXML
    private ChoiceBox<String> indexingIdexChoiceBox;
    @FXML
    private ChoiceBox<String> searchingIdexChoiceBox;


    @FXML
    private TextField searchField;


    @FXML
    private CheckBox indexingLemetizationCBox,indexingNormalizationCBox,
            indexingStemmingCBox,indexingStopWordsCBox,indexingTokenizationCBox;

    @FXML
    private CheckBox searchingLemetizationCBox,searchingNormalizationCBox,
            searchingStemmingCBox,searchingStopWordsCBox,searchingTokenizationCBox;

    @FXML
    void onExitBtnClick(ActionEvent event) throws IOException {
        homePage.getScene().getWindow().hide();
    }

    @FXML
    void onSearchBtnClick(ActionEvent event) {

        String searchText = searchField.getText();
        System.out.println(searchText);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] choices = {"Lucene", "Term-incidence",
                            "Inverted-index","Positional-index",
                            "Bi-word-index"};
        indexingIdexChoiceBox.getItems().addAll(choices);
        indexingIdexChoiceBox.setValue(choices[0]);
        searchingIdexChoiceBox.getItems().addAll(choices);
        searchingIdexChoiceBox.setValue(choices[0]);

    }
}
