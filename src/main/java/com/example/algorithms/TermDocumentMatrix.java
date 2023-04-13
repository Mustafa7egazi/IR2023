package com.example.algorithms;

import java.io.*;
import java.util.*;

public class TermDocumentMatrix {
    public void performTermMatrix() throws IOException {
        File file = new File("/media/mustafa7egazi/Data/3- Faculty/4-Fourth year/Second term/Information Reterieval/Project/IR2023/cici.txt");
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);

        List<String> terms = new ArrayList<String>();
        List<String> documents = new ArrayList<String>();
        Dictionary<String, String[]> wholeWords = new Hashtable<>();

        List<String[]> globalWords = new ArrayList<>();

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
                String[] words = document.split("\\s+");
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

        // Create the Term-Document Incidence Matrix
        int[][] matrix = new int[terms.size()][documents.size()];

        for (int i = 0; i < documents.size(); i++) {
            String document = documents.get(i);
            //System.out.println(documents.get(i));
            //String[] words = document.split("\\s+");
            //String[] words = wholeWords.get("words");
            String[] words = globalWords.get(i);
            //System.out.println(Arrays.toString(words));
//            System.out.println(Arrays.toString(words));


            for (String word : words) {
                //System.out.println(word);
                int index = terms.indexOf(word);
//                if (index < 0) System.err.println(index);
//                else System.out.println(index);
                matrix[index][i] = 1;
//                if (index != -1){
//                }
            }
        }

        // Output the matrix to the console
//        int c = 0;
        for (int i = 0; i < terms.size(); i++) {
            for (int j = 0; j < 1; j++) {
                if (matrix[i][j] == 1) {
//                    c++;
                    System.out.println(matrix[i][j]);
                }
            }
        }
        bufferedReader.close();
//        System.out.println(c);
    }
}
