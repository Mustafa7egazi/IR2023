package com.example.algorithms;

import com.example.ir2023.HomeController;

import java.io.*;
import java.util.*;

import static com.example.util.Utilities.*;

public class InvertedIndex {
    private final Map<String, List<String>> indexMap;
    private final BufferedReader bufferedReader;
    private final List<String> terms, documents;
    private final Dictionary<String, List<String>> wholeWords;
    private final List<List<String>> globalWords;

    public InvertedIndex() throws FileNotFoundException {
        indexMap = new HashMap<>();

        File file = new File("cici.txt");
        FileReader reader = new FileReader(file);
        bufferedReader = new BufferedReader(reader);
        terms = new ArrayList<>();
        documents = new ArrayList<>();
        wholeWords = new Hashtable<>();
        globalWords = new ArrayList<>();
    }

    public void performInvertedIndex() throws IOException {
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
//                    words = applyLemmetization(words);
//                }

                // stemming preprocessing
                if (HomeController.preprocessing.contains("Stemming")) {
                    words = applyStemming(words);
                }
                // stop words removal preprocessing
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
            }
        }


        for (String term : terms) {
            indexMap.put(term.toLowerCase(), new ArrayList<>());
        }

        for (int i = 0; i < documents.size(); i++) {
            List<String> words = globalWords.get(i);
            for (String word : words) {
                int index = terms.indexOf(word);
                if (index != -1) {
                    if (!indexMap.get(terms.get(index)).contains(documents.get(i))) {
                        indexMap.get(terms.get(index)).add(documents.get(i));
                    }
                }
            }
        }

        System.out.println(indexMap.get("biographi"));

//        System.out.println(indexMap);
//        System.out.println("finish");
    }

    public List<String> oneWordSearch(String textToSearch) {
        List<String> foundDocs = indexMap.get(textToSearch);
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
        List<String> firstWordDocs = indexMap.get(splitText[0].toLowerCase());
        List<String> secondWordDocs = indexMap.get(splitText[2].toLowerCase());
        try {
            if (firstWordDocs.isEmpty() || secondWordDocs.isEmpty())
                return List.of("Not found with AND");
        } catch (NullPointerException e) {
            return List.of("Not found with AND");
        }
        List<String> intersection = new ArrayList<>(firstWordDocs);
        intersection.retainAll(secondWordDocs);
        System.out.println(intersection);
        if (intersection.isEmpty()) {
            return List.of("Not found with AND");
        }
        return intersection;
    }

    public List<String> orSearch(String textToSearch) {
        String[] splitText = textToSearch.split("\\s+");
        List<String> firstWordDocs = indexMap.get(splitText[0].toLowerCase());
        List<String> secondWordDocs = indexMap.get(splitText[2].toLowerCase());


        if (firstWordDocs == null && secondWordDocs != null) {
            return secondWordDocs;
        } else if (firstWordDocs != null && secondWordDocs == null) {
            return firstWordDocs;
        } else if (firstWordDocs != null && secondWordDocs != null) {
            Set<String> allData = new HashSet<>();
            allData.addAll(firstWordDocs);
            allData.addAll(secondWordDocs);
            List<String> castedAllData = new ArrayList<>();
            castedAllData.addAll(allData);
            return castedAllData;
        } else {
            return List.of("Not found with OR");
        }
    }

    public List<String> notSearch(String textToSearch) {
        String[] splitText = textToSearch.split("\\s+");
        if (splitText.length == 2 && splitText[0].equals("NOT")) {
            List<String> wordFoundDocs = indexMap.get(splitText[1].toLowerCase());
            if (wordFoundDocs == null) {
                wordFoundDocs = new ArrayList<>();
            }
            List<String> resultDocs = new ArrayList<>();
            for (String doc : documents) {
                if (!wordFoundDocs.contains(doc)) {
                    resultDocs.add(doc);
                }
            }
            if (!resultDocs.isEmpty()) {
                return resultDocs;
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
}
