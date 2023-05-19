package com.example.algorithms;

import com.example.ir2023.HomeController;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.util.Utilities.*;

public class BiWordIndex {

    private final Map<String, List<String>> indexMap;
    //private final Map<String, Map<String, List<Integer>>> indexMap; // Map each bi-word to a map of documents and their positions
    private final BufferedReader bufferedReader;
    private final List<String> biWords, documents;

    private final List<List<String>> documentWords;

    public BiWordIndex() throws FileNotFoundException {
        indexMap = new HashMap<>();

        File file = new File("cici.txt");
        FileReader reader = new FileReader(file);
        bufferedReader = new BufferedReader(reader);
        biWords = new ArrayList<>();
        documents = new ArrayList<>();

        documentWords = new ArrayList<>();
    }

    public void performBiWordIndex() throws IOException {
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

                // normalization
                if (HomeController.preprocessing.contains("Normalization")){
                    document = normalizeText(document);
                }

                List<String> words = Arrays.asList(document.split("\\s+"));

                if (HomeController.preprocessing.contains("Stemming")) {
                    words = applyStemming(words);
                }
                if (HomeController.preprocessing.contains("Stop words")) {
                    words = applyStopWordsRemoval(words);
                }

                documentWords.add(words);

                for (int i = 0; i < words.size() - 1; i++) {
                    String biWord = words.get(i) + " " + words.get(i + 1);
                    if (!biWords.contains(biWord)) {
                        biWords.add(biWord);
                    }
                }
            }
        }
        System.out.println("biwording done");

        // Initialize the index map
        for (String biWord : biWords) {
            indexMap.put(biWord.toLowerCase(), new ArrayList<>());
        }
        System.out.println("index map initialization done");


        //Populate the index map with document ids for each bi-word
        for (int i = 0; i < documents.size(); i++) {

            List<String> words = documentWords.get(i);
            for (int j = 0; j < words.size() - 1; j++) {
                String biWord = words.get(j) + " " + words.get(j + 1);
                int index = biWords.indexOf(biWord);
                if (index != -1) {
                    //System.out.println("found the biword");
                    if (!(indexMap.get(biWord).contains(documents.get(i))))
                    {
                        indexMap.get(biWord).add(documents.get(i));
                    }
                }
            }

//            for (String biWord:biWords) {
////                List<String> eachBiWordTerms = Arrays.asList(biword.toLowerCase().split("\\s+"));
////                int firstWordIndex, secondWordIndex;
//                if (doc.trim().contains(biWord)){
//                    if (!(indexMap.get(biWord).contains(documents.get(i))))
//                    {
//                        indexMap.get(biWord).add(documents.get(i));
//                    }
//                }
//            }
        }

        // Print the index map
        for (String biWord : indexMap.keySet()) {
            System.out.println(biWord + ": " + indexMap.get(biWord));
        }
    }

    public static void main(String[] args) throws IOException {
        BiWordIndex biWordIndex = new BiWordIndex();
        System.out.println("begin index");
        biWordIndex.performBiWordIndex();
        System.out.println("after index");

        System.out.println("searching");

        System.out.println(biWordIndex.search("biogra* of"));
    }

    public  List<String> search(String input) {

        System.out.println(input);

        List<String> results = new ArrayList<>();

        // Escape special characters and replace '*' with '.*' to match any number of characters
        String regex = input.replace("*", ".*");
        Pattern pattern = Pattern.compile(regex);

        for (String key : indexMap.keySet()) {
            Matcher matcher = pattern.matcher(key);

            if (matcher.matches()) {
                results.addAll(indexMap.get(key));
            }
        }
        return results;
    }


//    public List<String> search(String query) {
//        String[] biWords = query.toLowerCase().split("\\s+");
//        Set<String> result = new HashSet<>();
//
//        if (biWords.length > 1) {
//            for (int i = 0; i < biWords.length - 1; i++) {
//                String biWord = biWords[i] + " " + biWords[i + 1];
//                if (indexMap.containsKey(biWord)) {
//                    Map<String, List<Integer>> docPositions = indexMap.get(biWord);
//                    result.addAll(docPositions.keySet());
//                }
//            }
//        }
//
//        return new ArrayList<>(result);
//    }
}

