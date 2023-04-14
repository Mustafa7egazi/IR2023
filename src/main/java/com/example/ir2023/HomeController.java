package com.example.ir2023;

import com.example.algorithms.TermDocumentMatrix;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    public static List<String> resultToShow;

    TermDocumentMatrix algo;

    {
        try {
            algo = new TermDocumentMatrix();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private final ArrayList<String> preprocessing = new ArrayList<>();
    private final ArrayList<CheckBox> checkBoxes = new ArrayList<>();


    @FXML
    private AnchorPane homePage;

    @FXML
    private ChoiceBox<String> indexingIdexChoiceBox;
    @FXML
    private ChoiceBox<String> searchingIdexChoiceBox;


    @FXML
    private TextField searchField;


    @FXML
    private CheckBox indexingLemetizationCBox, indexingNormalizationCBox,
            indexingStemmingCBox, indexingStopWordsCBox, indexingTokenizationCBox;

    @FXML
    private CheckBox searchingLemetizationCBox, searchingNormalizationCBox,
            searchingStemmingCBox, searchingStopWordsCBox, searchingTokenizationCBox;

    @FXML
    void onExitBtnClick() {
        homePage.getScene().getWindow().hide();
    }

    @FXML
    void onFinishBtnClick() throws IOException {

        TermDocumentMatrix algo = new TermDocumentMatrix();
        algo.performTermMatrix();

//        System.out.println("index: "+indexingIdexChoiceBox.getValue()+
//                "\t preprocessing: "+preprocessing);
    }

    @FXML
    void onSearchBtnClick() throws IOException {

        String searchText = searchField.getText().toLowerCase();
        String[] split = searchText.split(" ");
        if (split.length == 3) {
            showTermMatrixSearchResult(algo.booleanSearch(searchText));

        } else if (split.length == 1) {
            if (algo.oneWordSearch(searchText).isEmpty()) {
                showTermMatrixSearchResult(List.of("Not Found!"));

            } else {
                showTermMatrixSearchResult(algo.oneWordSearch(searchText));
            }
        } else {
            showTermMatrixSearchResult(List.of("Unsupported query !!"));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] choices = {"Lucene", "Term-incidence",
                "Inverted-index", "Positional-index",
                "Bi-word-index"};
        indexingIdexChoiceBox.getItems().addAll(choices);
        indexingIdexChoiceBox.setValue(choices[0]);
        searchingIdexChoiceBox.getItems().addAll(choices);
        searchingIdexChoiceBox.setValue(choices[0]);

        checkBoxes.add(indexingTokenizationCBox);
        checkBoxes.add(indexingNormalizationCBox);
        checkBoxes.add(indexingStemmingCBox);
        checkBoxes.add(indexingLemetizationCBox);
        checkBoxes.add(indexingStopWordsCBox);

        try {
            algo.performTermMatrix();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    void onCheckBoxChange() {

        for (CheckBox checkBox : checkBoxes) {
            manipulateCheckboxes(checkBox);
        }
    }

    private void manipulateCheckboxes(CheckBox c) {
        if (c.isSelected()) {
            if (!preprocessing.contains(c.getText()))
                preprocessing.add(c.getText());
        } else {
            preprocessing.remove(c.getText());
        }
    }

    private void showTermMatrixSearchResult(List<String> searchResult) throws IOException {
        resultToShow = searchResult;
        Stage resultStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("result-of-documents-list.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 490);
        resultStage.setTitle("Search Result!");
        resultStage.setScene(scene);
        resultStage.setResizable(false);
        resultStage.setAlwaysOnTop(true);
        resultStage.show();
    }
}
