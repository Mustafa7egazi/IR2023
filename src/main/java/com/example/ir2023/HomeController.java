package com.example.ir2023;

import com.example.algorithms.InvertedIndex;
import com.example.algorithms.Lucene;
import com.example.algorithms.PositionalIndex;
import com.example.algorithms.TermDocumentMatrix;
import com.example.util.Utilities;
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
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class HomeController implements Initializable {

    public static List<String> resultToShow;

    TermDocumentMatrix termMatrixAlgo;
    InvertedIndex invertedIndexAlgo;
    Lucene luceneAlgo;
    PositionalIndex positionalIndexAlgo;


    public static final ArrayList<String> preprocessing = new ArrayList<>();
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


    //global vars
    List<String> queryMembers = new ArrayList<>();
    String analyzedSearchText ="";

    @FXML
    void onExitBtnClick() {
        homePage.getScene().getWindow().hide();
    }

    @FXML
    void onFinishBtnClick() {

        preprocessing.addAll(Arrays.asList("Tokenization", "Normalization"));

        if (indexingLemetizationCBox.isSelected()) {
            preprocessing.add("Lemetization");
        }
        if (indexingStemmingCBox.isSelected()) {
            preprocessing.add("Stemming");
        }
        if (indexingStopWordsCBox.isSelected()) {
            preprocessing.add("Stop words");
        }

        if (!indexingTokenizationCBox.isSelected()) {
            showAlert("For better result, you should select Tokenization for processing.");
        } else {

            if (indexingIdexChoiceBox.getValue().equals("Select") || indexingIdexChoiceBox.getValue().equals(""))
                System.out.println("Select algorithm first");
            else if (Objects.equals(selectedIndexingAlgorithm, "Term-incidence")) {
                try {
                    termMatrixAlgo = new TermDocumentMatrix();
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                loadingIndicator.setVisible(true);
                PauseTransition pt = new PauseTransition();
                pt.setDuration(Duration.seconds(1));
                pt.setOnFinished(e -> {
                    try {
                        termMatrixAlgo.performTermMatrix();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    } finally {
                        System.out.println("Finish indexing");
                        loadingIndicator.setVisible(false);
                        doneLabel.setVisible(true);
                    }
                });
                pt.play();


            } else if (selectedIndexingAlgorithm.equals("Inverted-index")) {
                try {
                    invertedIndexAlgo = new InvertedIndex();
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                loadingIndicator.setVisible(true);
                PauseTransition pt = new PauseTransition();
                pt.setDuration(Duration.seconds(1));
                pt.setOnFinished(e -> {
                    try {
                        invertedIndexAlgo.performInvertedIndex();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    } finally {
                        System.out.println("Finish indexing");
                        loadingIndicator.setVisible(false);
                        doneLabel.setVisible(true);
                    }
                });
                pt.play();
            } else if (selectedIndexingAlgorithm.equals("Lucene")) {
                try {
                    luceneAlgo = new Lucene();
                    luceneAlgo.luceneIndexer();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

            } else if (selectedIndexingAlgorithm.equals("Positional-index")) {

                try {
                    positionalIndexAlgo = new PositionalIndex();
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                loadingIndicator.setVisible(true);
                PauseTransition pt = new PauseTransition();
                pt.setDuration(Duration.seconds(1));
                pt.setOnFinished(e -> {
                    try {
                        positionalIndexAlgo.performPositionalIndex();
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
    }

    @FXML
    void onSearchBtnClick() throws IOException {

        doneLabel.setVisible(false);
        if (!searchingTokenizationCBox.isSelected()) {
            showAlert("For better result, you should select Tokenization for searching.");

        } else {
            String searchText = searchField.getText();
            if (searchingIdexChoiceBox.getValue().equals("Term-incidence")) {

                processAndAnalyzeSearchText(searchText);

                String[] split = analyzedSearchText.toLowerCase().split(" ");
                if (split.length == 3 || split.length == 4 || split.length == 5) {
                    showSearchResultDocs(termMatrixAlgo.booleanSearch(analyzedSearchText.trim()));

                } else if (split.length == 2) {
                    if (split[0].equals("not")) {
                        showSearchResultDocs(termMatrixAlgo.notSearch(analyzedSearchText.trim()));
                    } else {
                        showAlert("Wrong boolean query formulation!");
                    }
                } else if (split.length == 1) {
                    if (termMatrixAlgo.oneWordSearch(analyzedSearchText.trim()).isEmpty()) {
                        showAlert("Not Found!");

                    } else {
                        showSearchResultDocs(termMatrixAlgo.oneWordSearch(analyzedSearchText.trim()));
                    }
                } else {
                    showAlert("Unsupported query !!");
                }
            } else if (searchingIdexChoiceBox.getValue().equals("Select")) {
                showAlert("Please select and algorithm to search with!");
            } else if (searchingIdexChoiceBox.getValue().equals("Inverted-index")) {

                processAndAnalyzeSearchText(searchText);

                String[] split = analyzedSearchText.split(" ");
                if (split.length == 3 || split.length == 4 || split.length == 5) {
                    showSearchResultDocs(invertedIndexAlgo.booleanSearch(analyzedSearchText.trim()));
                } else if (split.length == 2) {
                    if (split[0].equals("NOT")) {
                        showSearchResultDocs(invertedIndexAlgo.notSearch(analyzedSearchText.trim()));
                    } else {
                        showAlert("Wrong query formulation!!");
                    }
                } else if (split.length == 1) {
                    showSearchResultDocs(invertedIndexAlgo.oneWordSearch(analyzedSearchText.trim()));
                } else {
                    showAlert("Un supported query!!");
                }
            } else if (searchingIdexChoiceBox.getValue().equals("Lucene")) {
                try {
                    showSearchResultDocs(luceneAlgo.luceneSearcher(searchText));
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                }
            } else if (searchingIdexChoiceBox.getValue().equals("Positional-index")) {
                processAndAnalyzeSearchText(searchText);

                String[] split = analyzedSearchText.split(" ");
                if (split.length == 3 || split.length == 4 || split.length == 5) {
                    showSearchResultDocs(positionalIndexAlgo.booleanSearch(analyzedSearchText.trim()));
                } else if (split.length == 2) {
                    if (split[0].equals("NOT")) {
                        showSearchResultDocs(positionalIndexAlgo.notSearch(analyzedSearchText.trim()));
                    } else {
                        showAlert("Wrong query formulation!!");
                    }
                } else if (split.length == 1) {
                    showSearchResultDocs(positionalIndexAlgo.oneWordSearch(analyzedSearchText.trim()));
                } else {
                    showAlert("Un supported query!!");
                }
            } else {
                showAlert("Not implemented yet!");
            }
        }
    }

    private List<String> analyzeQuery(List<String> queryWords) throws IOException {
        List<String> newQueryWords = queryWords;
        if (preprocessing.contains("Stemming") && (indexingStemmingCBox.isSelected())) {
            newQueryWords =  Utilities.applyStemming(newQueryWords);
        }

        if (preprocessing.contains("Stop words") && (indexingStopWordsCBox.isSelected())) {
            newQueryWords =  Utilities.applyStopWordsRemoval(newQueryWords);
        }

//        if (preprocessing.contains("Lemetization") && (indexingLemetizationCBox.isSelected())) {
//            newQueryWords =  Utilities.applyLemmetization(newQueryWords);
//        }
        return newQueryWords;
    }

    private void processAndAnalyzeSearchText(String searchText) throws IOException {
        queryMembers = Arrays.asList(searchText.split(" "));
        queryMembers = analyzeQuery(queryMembers);

        System.out.println(queryMembers);

        StringBuilder solidQuery = new StringBuilder();
        for (String word:queryMembers) {
            if (word.equals("AND") || word.equals("OR") || word.equals("NOT"))
                solidQuery.append(word).append(" ");
            else
                solidQuery.append(word.toLowerCase()).append(" ");
        }

        analyzedSearchText = solidQuery.toString();
        System.out.println(analyzedSearchText);
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
    }

    private void showSearchResultDocs(List<String> searchResult) throws IOException {
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
    void showAlert(String m) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Note!");
        alert.setContentText(m);
        alert.showAndWait();
    }


}
