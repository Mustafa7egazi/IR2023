package com.example.algorithms;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndex2 {
    private Map<String, List<Integer>> index;

    public InvertedIndex2() {
        index = new HashMap<>();
    }

    // function to perform inverted index technique on data from file "cici.txt"
    public void buildIndex() throws IOException {
        File inputFile = new File("cici.txt");
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        int docId = 1;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split("\\W+");
            for (String word : words) {
                List<Integer> docList = index.getOrDefault(word, new ArrayList<>());
                docList.add(docId);
                index.put(word, docList);
            }
            docId++;
        }
        reader.close();

        // store the generated index into a file called "cici-inverted-index"
        PrintWriter writer = new PrintWriter(new FileWriter("cici-inverted-index"));
        for (String word : index.keySet()) {
            writer.print(word + " ");
            List<Integer> docList = index.get(word);
            for (int docId2 : docList) {
                writer.print(docId2 + " ");
            }
            writer.println();
        }
        writer.close();
    }

    // function to read from the inverted index to find a document using a query entered by the user
    public void search(String query) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("cici-inverted-index"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(" ");
            String word = tokens[0];
            List<Integer> docList = new ArrayList<>();
            for (int i = 1; i < tokens.length; i++) {
                docList.add(Integer.parseInt(tokens[i]));
            }
            if (word.equalsIgnoreCase(query)) {
                System.out.println("Found in documents: " + docList);
                reader.close();
                return;
            }
        }
        System.out.println("Not found");
        reader.close();
    }
}
