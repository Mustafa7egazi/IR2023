package com.example.ir2023;

import com.example.algorithms.TermDocumentMatrix;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private String selectedIndexingAlgorithm = "";


    @FXML
    private AnchorPane homePage;

    @FXML
    private ChoiceBox<String> indexingIdexChoiceBox;
    @FXML
    private ChoiceBox<String> searchingIdexChoiceBox;

    @FXML
    private ImageView loadingIndicator;

    @FXML
    private Label doneLabel;


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
    void onFinishBtnClick() {

        if (indexingIdexChoiceBox.getValue().equals("Select") || indexingIdexChoiceBox.getValue().equals(""))
            System.out.println("Select algorithm first");
        else if (Objects.equals(selectedIndexingAlgorithm, "Term-incidence")) {
            loadingIndicator.setVisible(true);
                PauseTransition pt = new PauseTransition();
                pt.setDuration(Duration.seconds(1));
                pt.setOnFinished(e->{
                    try {
                        algo.performTermMatrix();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    } finally {
                        System.out.println("Finish indexing");
                        loadingIndicator.setVisible(false);
                        doneLabel.setVisible(true);
                    }
                });
                pt.play();


        } else {
            showAlert("Not implemented yet");
            System.out.println("Not implemented yet");
        }

    }

    @FXML
    void onSearchBtnClick() throws IOException {
        if (searchingIdexChoiceBox.getValue().equals("Term-incidence")){
            String searchText = searchField.getText().toLowerCase();
            String[] split = searchText.split(" ");
            if (split.length == 3) {
                showTermMatrixSearchResult(algo.booleanSearch(searchText));

            } else if (split.length == 1) {
                if (algo.oneWordSearch(searchText).isEmpty()) {
                    showAlert("Not Found!");

                } else {
                    showTermMatrixSearchResult(algo.oneWordSearch(searchText));
                }
            } else {
                showAlert("Unsupported query !!");
            }
        }else if (searchingIdexChoiceBox.getValue().equals("Select")){
            showAlert("Please select and algorithm to search with!");
        }else {
            showAlert("Not implemented yet!");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] choices = {"Select", "Lucene", "Term-incidence",
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

        loadingIndicator.setVisible(false);

        indexingIdexChoiceBox.setOnAction(this::getIndexTypeSelection);

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

    private void getIndexTypeSelection(ActionEvent event) {
        selectedIndexingAlgorithm = indexingIdexChoiceBox.getValue();
//        loadingIndicator.setVisible(true);
//        if (Objects.equals(indexingIdexChoiceBox.getValue(), "Term-incidence")){
//            try {
//                algo.performTermMatrix();
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }
//        }
//        try {
//            Thread.sleep(Duration.ofSeconds(1));
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        if (TermDocumentMatrix.doneTermIndex){
//            loadingIndicator.setVisible(false);
//        }
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
    @FXML
    void showAlert(String m){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Note!");
        alert.setContentText(m);
        alert.showAndWait();
    }


}
