package com.example.algorithms;

import com.example.ir2023.HomeController;
import javafx.geometry.Pos;

import java.io.*;
import java.util.*;

import static com.example.util.Utilities.*;

public class PositionalIndex {
    private final Map<String, Map<String, List<Integer>>> indexMap; // Map each term to a map of documents and their positions
    private final BufferedReader bufferedReader;
    private final List<String> terms, documents;
    private final Dictionary<String, List<String>> wholeWords;
    private static List<List<String>> globalWords;

    public PositionalIndex() throws FileNotFoundException {
        indexMap = new HashMap<>();

        File file = new File("cici.txt");
        FileReader reader = new FileReader(file);
        bufferedReader = new BufferedReader(reader);
        terms = new ArrayList<>();
        documents = new ArrayList<>();
        wholeWords = new Hashtable<>();
        globalWords = new ArrayList<>();
    }

    public void performPositionalIndex() throws IOException {
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

                List<String> words = Arrays.asList(document.split("\\s+"));

                // TODO: preprocessing activation
                if (HomeController.preprocessing.contains("Stemming")) {
                    words = applyStemming(words);
                }
                if (HomeController.preprocessing.contains("Stop words")) {
                    words = applyStopWordsRemoval(words);
                }

                wholeWords.put("words", words);
                globalWords.add(words);

                for (String word : words) {
                    if (!terms.contains(word)) {
                        terms.add(word);
                    }
                }
            }
        }

        // Initialize the index map
        for (String term : terms) {
            indexMap.put(term.toLowerCase(), new HashMap<>());
        }

        // Populate the index map with document positions for each term
        for (int i = 0; i < documents.size(); i++) {
            String doc = documents.get(i);
            List<String> words = globalWords.get(i);
            for (int j = 0; j < words.size(); j++) {
                String word = words.get(j);
                int index = terms.indexOf(word);
                if (index != -1) {
                    Map<String, List<Integer>> docPositions = indexMap.get(terms.get(index));
                    if (!docPositions.containsKey(doc)) {
                        docPositions.put(doc, new ArrayList<>());
                    }
                    docPositions.get(doc).add(j + 1); // Add the position of the word in the document
                }
            }
        }

        // Print the index map
        for (String term : indexMap.keySet()) {
            System.out.println(term + ": " + indexMap.get(term));
        }
    }


    public List<String> oneWordSearch(String textToSearch) {
        Map<String, List<Integer>> docsAndOccurrence = indexMap.get(textToSearch);

        List<String> foundDocs = new ArrayList<>(docsAndOccurrence.keySet());

        try {
            if (foundDocs.isEmpty()) {
                return List.of("Not found!!");
            }
        } catch (NullPointerException e) {
            return List.of("Not found!!");
        }
        return foundDocs;
    }


    public List<String> andSearch(String textToSearch) {
        String[] splitText = textToSearch.split("\\s+");
        String firstWord = splitText[0].toLowerCase();
        String secondWord = splitText[2].toLowerCase();

        Map<String, List<Integer>> docsAndOccurrenceOfFirstWord = indexMap.get(firstWord);
        Map<String, List<Integer>> docsAndOccurrenceOfSecondWord = indexMap.get(secondWord);

        List<String> firstWordDocs = new ArrayList<>(docsAndOccurrenceOfFirstWord.keySet());
        List<String> secondWordDocs = new ArrayList<>(docsAndOccurrenceOfSecondWord.keySet());


        try {
            if (firstWordDocs.isEmpty() || secondWordDocs.isEmpty())
                return List.of("Not found with AND");
        } catch (NullPointerException e) {
            return List.of("Not found with AND");
        }
        List<String> intersection = new ArrayList<>(firstWordDocs);
        intersection.retainAll(secondWordDocs);
        if (intersection.isEmpty()) {
            return List.of("Not found with AND");
        }
        return intersection;
    }


    public List<String> orSearch(String textToSearch) {
        String[] splitText = textToSearch.split("\\s+");
        String firstWord = splitText[0].toLowerCase();
        String secondWord = splitText[2].toLowerCase();

        Map<String, List<Integer>> docsAndOccurrenceOfFirstWord = indexMap.get(firstWord);
        Map<String, List<Integer>> docsAndOccurrenceOfSecondWord = indexMap.get(secondWord);

        List<String> firstWordDocs = new ArrayList<>(docsAndOccurrenceOfFirstWord.keySet());
        List<String> secondWordDocs = new ArrayList<>(docsAndOccurrenceOfSecondWord.keySet());


        if (firstWordDocs.isEmpty() && (!secondWordDocs.isEmpty())) {
            return secondWordDocs;
        } else if ((!firstWordDocs.isEmpty()) && secondWordDocs.isEmpty()) {
            return firstWordDocs;
        } else if ((!firstWordDocs.isEmpty()) && (!secondWordDocs.isEmpty())) {
            Set<String> allData = new HashSet<>();
            allData.addAll(firstWordDocs);
            allData.addAll(secondWordDocs);
            return new ArrayList<>(allData);
        } else {
            return List.of("Not found with OR");
        }
    }

    public List<String> notSearch(String textToSearch) {
        String[] splitText = textToSearch.split("\\s+");
        String firstWord = splitText[0];
        String secondWord = splitText[1].toLowerCase();

        Map<String, List<Integer>> docsAndOccurrenceOfTheWord = indexMap.get(secondWord);


        List<String> theWordDocs = new ArrayList<>(docsAndOccurrenceOfTheWord.keySet());


        if (splitText.length == 2 && splitText[0].equals("NOT")) {

            if (!(theWordDocs.isEmpty())) {
                List<String> resultDocs = new ArrayList<>();
                for (String doc : documents) {
                    if (!theWordDocs.contains(doc)) {
                        resultDocs.add(doc);
                    }
                }
                if (!resultDocs.isEmpty()) {
                    return resultDocs;
                }
            }
        }
        return List.of("Not Found !!");
    }

    public List<String> booleanSearch(String textToSearch) {
        List<String> localTerms;
        localTerms = List.of(textToSearch.split("\\s+"));
        if (localTerms.size() == 3) {
            switch (localTerms.get(1)) {
                case "AND" -> {
                    return andSearch(textToSearch);
                }
                case "OR" -> {
                    return orSearch(textToSearch);
                }
                default -> {
                    return List.of("Wrong query!!");
                }
            }
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


    public static void main(String[] args) throws FileNotFoundException {
        PositionalIndex p = new PositionalIndex();
        try {
            p.performPositionalIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("------------------------- Global  -------------------");
        System.out.print(globalWords.get(0));


        System.out.println("-------------------------------Result -----------------------------");
        String s = "The present study".toLowerCase();
        System.out.println(s+"\n"+p.search(s));
        //System.out.println(s+"\n"+p.search(s).size());
    }

    public List<String> search(String query) {
        String[] terms = query.toLowerCase().split("\\s+");
        List<String> results = new ArrayList<>();

        // Find the documents that contain the first term in the query
        Set<String> initialDocs =  indexMap.get(terms[0]).keySet();
        System.out.println("inital docs");
        System.out.println(initialDocs);

        // Loop through each of those documents and check if they contain the remaining terms
        for (String doc : initialDocs) {
            List<Integer> positions = new ArrayList<>();
            int docId = Integer.parseInt(doc);
            List<String> words = globalWords.get((docId)-1);
            System.out.println("words");
            System.out.println(words);
            for (int i = 0; i < words.size(); i++) {
                if (words.get(i).equals(terms[0])) {
                    positions.add(i);
                }
            }

            System.out.println("positions");
            System.out.println(positions);
            boolean found = true;                                  // the police arrived
                                                                   // the doctor arrived
            for (int i = 1; i < terms.length; i++) {
                List<Integer> newPositions = new ArrayList<>();
                for (int j = 0; j < positions.size(); j++) {
                    int pos = positions.get(j) + 1;
//                    if (positions.size() < (positions.get(j) +i)){
//                        //continue
//                        pos = positions.get(j) + 1;
//                    }
//                    else {
//                        pos = positions.get(j) + i;
//                    }
                    if (!(pos >= words.size()) && words.get(pos).equals(terms[i])) {
                        newPositions.add(pos);
//                        found = false;
//                        break;
                    }
                }
                if (newPositions.isEmpty()) {
                    found = false;
                    break;
                }
                positions = newPositions;
                System.out.println("newwwwwwwwwww positions");
                System.out.println(positions);
            }
            if (found) {
                results.add(doc);
            }
        }
        return results;
    }

}
