package com.example.util;

import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utilities {


    // stop words preprocessing
    public static List<String> applyStopWordsRemoval(List<String> terms) {
        List<String> filteredTokens = new ArrayList<String>();
        for (String token : terms) {
            if (!Arrays.asList(STOP_WORDS).contains(token)) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }

    // stemming preprocessing
    public static List<String> applyStemming(List<String> tokens) {
        PorterStemmer stemmer = new PorterStemmer();
        List<String> stemmedTokens = new ArrayList<String>();
        for (String token : tokens) {
            stemmer.setCurrent(token);
            stemmer.stem();
            stemmedTokens.add(stemmer.getCurrent());
        }
        return stemmedTokens;
    }

    // Lemetization preprocessing

    public static List<String> applyLemmetization(List<String> terms) throws IOException {

        // Load the lemmatizer model
        InputStream modelIn = new FileInputStream("en-lemmatizer.bin");
        LemmatizerModel model = new LemmatizerModel(modelIn);
        modelIn.close();

        // Create a lemmatizer object using the model
        LemmatizerME lemmatizer = new LemmatizerME(model);

        // Lemmatize the terms
        String[] lemmas = lemmatizer.lemmatize(terms.toArray(new String[0]), new String[terms.size()]);

        // Print the lemmas
//        for (int i = 0; i < lemmas.length; i++) {
//            System.out.println("Original term: " + terms.get(i));
//            System.out.println("Lemma: " + lemmas[i]);
//        }

        return List.of(lemmas);
    }




    public static final String[] STOP_WORDS = {
            "a",
            "about",
            "above",
            "after",
            "again",
            "against",
            "all",
            "am",
            "an",
            "and",
            "any",
            "are",
            "aren't",
            "as",
            "at",
            "be",
            "because",
            "been",
            "before",
            "being",
            "below",
            "between",
            "both",
            "but",
            "by",
            "can",
            "can't",
            "cannot",
            "could",
            "couldn't",
            "did",
            "didn't",
            "do",
            "does",
            "doesn't",
            "doing",
            "don't",
            "down",
            "during",
            "each",
            "few",
            "for",
            "from",
            "further",
            "had",
            "hadn't",
            "has",
            "hasn't",
            "have",
            "haven't",
            "having",
            "he",
            "he'd",
            "he'll",
            "he's",
            "her",
            "here",
            "here's",
            "hers",
            "herself",
            "him",
            "himself",
            "his",
            "how",
            "how's",
            "i",
            "i'd",
            "i'll",
            "i'm",
            "i've",
            "if",
            "in",
            "into",
            "is",
            "isn't",
            "it",
            "it's",
            "its",
            "itself",
            "let's",
            "me",
            "more",
            "most",
            "mustn't",
            "my",
            "myself",
            "no",
            "nor",
            "not",
            "of",
            "off",
            "on",
            "once",
            "only",
            "or",
            "other",
            "ought",
            "our",
            "ours",
            "ourselves",
            "out",
            "over",
            "own",
            "same",
            "shan't",
            "she",
            "she'd",
            "she'll",
            "she's",
            "should",
            "shouldn't",
            "so",
            "some",
            "such",
            "than",
            "that",
            "that's",
            "the",
            "their",
            "theirs",
            "them",
            "themselves",
            "then",
            "there",
            "there's",
            "these",
            "they",
            "they'd",
            "they'll",
            "they're",
            "they've",
            "this",
            "those",
            "through",
            "to",
            "too",
            "under",
            "until",
            "up",
            "very",
            "was",
            "wasn't",
            "we",
            "we'd",
            "we'll",
            "we're",
            "we've",
            "were",
            "weren't",
            "what",
            "what's",
            "when",
            "when's",
            "where",
            "where's",
            "which",
            "while",
            "who",
            "who's",
            "whom",
            "why",
            "why's",
            "with",
            "won't",
            "would",
            "wouldn't" };
}
