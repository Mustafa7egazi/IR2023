package com.example.algorithms;

import com.example.ir2023.HomeController;


import java.io.*;
import java.util.*;

import static com.example.util.Utilities.*;

public class TermDocumentMatrix {

    private final BufferedReader bufferedReader;
    private List<String> terms;
    private final List<String> documents;
    private final Dictionary<String, List<String>> wholeWords;
    private final List<List<String>> globalWords;

    public static Boolean doneTermIndex = false;

    int[][] matrix;

    public TermDocumentMatrix() throws FileNotFoundException {
        File file = new File("cici.txt");
        FileReader reader = new FileReader(file);
        bufferedReader = new BufferedReader(reader);
        terms = new ArrayList<>();
        documents = new ArrayList<>();
        wholeWords = new Hashtable<>();
        globalWords = new ArrayList<>();
    }

    public void performTermMatrix() throws IOException {
        // Parse the text in the file to extract the individual documents and their terms
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith(".I")) {
                documents.add(line.substring(3));
            } else if (line.startsWith(".W")) {
                StringBuilder builder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null && !line.startsWith(".")) {
                    builder.append(line).append(" ");
                }
                String document = builder.toString().toLowerCase().trim();

                // normalization preprocessing
                if (HomeController.preprocessing.contains("Normalization")){
                    document = normalizeText(document);
                }

                List<String> words = List.of(document.split("\\s+"));


//                if (HomeController.preprocessing.contains("Lemetization")) {
//                    terms = applyLemmetization(terms);
//                }

                if (HomeController.preprocessing.contains("Stemming")) {
                    words = applyStemming(words);
                }
                if (HomeController.preprocessing.contains("Stop words")) {
                    words = applyStopWordsRemoval(words);
                }



                wholeWords.put("words", words);
                globalWords.add(words);
                //System.out.println(Arrays.toString(wholeWords.get("words")));

                for (String word : words) {
                    if (!terms.contains(word)) {
                        terms.add(word);
                    }
                }
//                for (String term: terms) {
//                    System.out.println(term);
//                }
            }
        }


        System.out.println(terms.subList(0, 20));


        System.out.println("------------------------ After ---------------------");
        System.out.println(terms.subList(0, 20));


        // Create the Term-Document Incidence Matrix
        matrix = new int[terms.size()][documents.size()];

        for (int i = 0; i < documents.size(); i++) {
            String document = documents.get(i);
            List<String> words = globalWords.get(i);

            for (String word : words) {
                //System.out.println(word);
                int index = terms.indexOf(word);
                if (index != -1) {
                    matrix[index][i] = 1;
                }
            }
        }

        System.out.println(terms.subList(1, 70));

        // Output the matrix to the console
//        for (int i = 0; i < terms.size(); i++) {
//            for (int j = 0; j < documents.size(); j++) {
//                //if (matrix[i][j] == 1) {
//                    System.out.println(matrix[i][j]);
//                //}
//            }
//        }


        bufferedReader.close();
        doneTermIndex = true;
//        System.out.println(c);
    }


    public List<String> oneWordSearch(String wordToSearch) {
        List<String> foundDocuments = new ArrayList<>();
        int index;
        if (terms.contains(wordToSearch)) {
            index = terms.indexOf(wordToSearch);
        } else {
            index = -1;
        }

        if (index != -1) {
            for (int i = 0; i < documents.size(); i++) {
                if (matrix[index][i] == 1) {
                    foundDocuments.add(documents.get(i));
                }
            }
        }
        return foundDocuments;
    }

    public List<String> andSearch(String textToSearch) {
        String[] splitText = textToSearch.split("\\s+");
        System.out.println(Arrays.toString(splitText));
        List<String> foundDocuments = new ArrayList<>();
        int firstIndex, secondIndex;
        if (terms.contains(splitText[0].toLowerCase()) && terms.contains(splitText[2].toLowerCase())) {
            firstIndex = terms.indexOf(splitText[0].toLowerCase());
            secondIndex = terms.indexOf(splitText[2].toLowerCase());
        } else {
            firstIndex = -1;
            secondIndex = -1;
        }

        if ((firstIndex != -1) && (secondIndex != -1)) {
            for (int i = 0; i < documents.size(); i++) {
                if ((matrix[firstIndex][i] == 1) && (matrix[secondIndex][i] == 1)) {
                    foundDocuments.add(documents.get(i));
                }
            }
        }
        return foundDocuments;
    }

    public List<String> orSearch(String textToSearch) {
        String[] splitText = textToSearch.split("\\s+");
        System.out.println(Arrays.toString(splitText));
        List<String> foundDocuments = new ArrayList<>();
        int firstIndex, secondIndex;
        if (terms.contains(splitText[0].toLowerCase()) || terms.contains(splitText[2].toLowerCase())) {
            firstIndex = terms.indexOf(splitText[0].toLowerCase());
            secondIndex = terms.indexOf(splitText[2].toLowerCase());
        } else {
            firstIndex = -1;
            secondIndex = -1;
        }

        if ((firstIndex != -1) || (secondIndex != -1)) {
            for (int i = 0; i < documents.size(); i++) {
                if (firstIndex != -1 && secondIndex == -1) {
                    if ((matrix[firstIndex][i] == 1)) {
                        foundDocuments.add(documents.get(i));
                    }
                } else if (firstIndex == -1) {
                    if ((matrix[secondIndex][i] == 1)) {
                        foundDocuments.add(documents.get(i));
                    }
                } else {
                    if ((matrix[firstIndex][i] == 1) || (matrix[secondIndex][i] == 1)) {
                        foundDocuments.add(documents.get(i));
                    }
                }
            }
        }
        return foundDocuments;
    }

    public List<String> notSearch(String textToSearch) {
        List<String> splitText = List.of(textToSearch.split("\\s+"));
        List<String> foundDocuments = new ArrayList<>();
        int indexOfNot = splitText.indexOf("NOT");
        int index;
        int indexOfNextWord = ++indexOfNot;

        if (terms.contains(splitText.get(indexOfNextWord).toLowerCase())) {
            index = terms.indexOf(splitText.get(indexOfNextWord).toLowerCase());
        } else {
            return documents;
        }

        if (index != -1) {
            for (int i = 0; i < documents.size(); i++) {
                if (matrix[index][i] == 0) {
                    foundDocuments.add(documents.get(i));
                }
            }
        }
        return foundDocuments;
    }


    public List<String> booleanSearch(String textToSearch) {
        List<String> localTerms;
        localTerms = List.of(textToSearch.split("\\s+"));
        if (localTerms.size() == 3) {
            switch (localTerms.get(1)) {
                case "AND" -> {
                    List<String> resultOfAnd = andSearch(textToSearch);
                    if (resultOfAnd.isEmpty()) {
                        return List.of("Not Found with and query");
                    } else {
                        return resultOfAnd;
                    }
                }
                case "OR" -> {
                    List<String> resultOfOr = orSearch(textToSearch);
                    if (resultOfOr.isEmpty()) {
                        return List.of("Not Found with or query");
                    } else {
                        return resultOfOr;
                    }
                }
                default -> {
                    return List.of("Wrong query!!");
                }
            }
        } else if (localTerms.size() == 2 && (localTerms.indexOf("NOT")) == 0) {
            return notSearch(textToSearch);
        } else if (localTerms.size() == 4) {

            if (localTerms.indexOf("NOT") == 0) {
                List<String> firstResult = notSearch(localTerms.get(0) + " " + localTerms.get(1));
                List<String> secondResult = oneWordSearch(localTerms.get(3));
                Set<String> finalResult = new HashSet<>(firstResult);
                if (localTerms.indexOf("AND") == 2) {
                    finalResult.retainAll(secondResult);
                } else if (localTerms.indexOf("OR") == 2) {
                    finalResult.addAll(secondResult);
                } else {
                    return List.of("Wrong query!!");
                }
                if (finalResult.isEmpty()) {
                    return List.of("Not found!!");
                } else {

                    return new ArrayList<>(finalResult);
                }
            } else if (localTerms.indexOf("NOT") == 2) {
                List<String> firstResult = notSearch(localTerms.get(2) + " " + localTerms.get(3));
                List<String> secondResult = oneWordSearch(localTerms.get(0));
                List<String> finalResult = new ArrayList<>(firstResult);
                if (localTerms.indexOf("AND") == 1) {
                    finalResult.retainAll(secondResult);
                } else if (localTerms.indexOf("OR") == 1) {
                    finalResult.addAll(secondResult);
                } else {
                    return List.of("Wrong query!!");
                }
                if (finalResult.isEmpty()) {
                    return List.of("Not found!!");
                } else {
                    return new ArrayList<>(finalResult);
                }
            }
        } else if (localTerms.size() == 5) {

            if (localTerms.indexOf("NOT") == 0 && localTerms.lastIndexOf("NOT") == 3) {
                List<String> firstResult = notSearch(localTerms.get(0) + " " + localTerms.get(1));
                List<String> secondResult = notSearch(localTerms.get(3) + " " + localTerms.get(4));
                List<String> finalResult = new ArrayList<>(firstResult);
                if (localTerms.indexOf("AND") == 2) {
                    finalResult.retainAll(secondResult);
                } else if (localTerms.indexOf("OR") == 2) {
                    finalResult.addAll(secondResult);
                } else {
                    return List.of("Wrong query!!");
                }
                if (finalResult.isEmpty()) {
                    return List.of("Not found!!");
                } else {
                    return new ArrayList<>(finalResult);
                }
            }
        }
        return List.of("Unsupported query !!");
    }
}
