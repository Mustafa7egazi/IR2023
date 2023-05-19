package com.example.util;


import org.tartarus.snowball.ext.PorterStemmer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Utilities {


    // normalizing text (removes punctuation and unite the letter case)
    public static String normalizeText(String input) {
        String result = input.toLowerCase(); // convert to lowercase
        result = result.replaceAll("\\p{Punct}", ""); // remove all punctuation
        result = result.replaceAll("(?<=\\p{Lu})\\s(?=\\p{Lu})", ""); // remove spaces between uppercase abbreviations
        //result = result.replaceAll("\\s", ""); // remove all remaining whitespace
        return result;
    }

    public static List<String> normalizeQueryText(List<String> inputList) {
        List<String> outputList = new ArrayList<>();
        for (String input : inputList) {
            String result = input;
           if (!result.equals("AND") && !result.equals("OR") && !result.equals("NOT")) {
                result = input.toLowerCase(); // convert to lowercase
           }
               result = result.replaceAll("[\\p{Punct}&&[^*]]", ""); // remove all punctuation
               result = result.replaceAll("(?<=\\p{Lu})\\s(?=\\p{Lu})", ""); // remove spaces between uppercase abbreviations
               //result = result.replaceAll("\\s", ""); // remove all remaining whitespace
               outputList.add(result);

        }
        return outputList;
    }


    // stop words preprocessing
    public static List<String> applyStopWordsRemoval(List<String> terms) {
        List<String> filteredTokens = new ArrayList<>();
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

//    public static String lemmatize(String inputStrings) {
//        // Set up the properties for the pipeline
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
//
//        // Create the pipeline
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//
//        // Create an annotation object with the input text
//        Annotation document = new Annotation(inputStrings);
//
//        // Run the pipeline on the annotation object
//        pipeline.annotate(document);
//
//        // Get the list of sentences from the annotation object
//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//
//        // Create a StringBuilder to store the lemmatized output
//        StringBuilder sb = new StringBuilder();
//
//        // Loop over each sentence in the list of sentences
//        for (CoreMap sentence : sentences) {
//            // Get the list of tokens from the sentence
//            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
//
//            // Loop over each token in the list of tokens
//            for (CoreLabel token : tokens) {
//                // Get the lemma for the token
//                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
//
//                // Append the lemma to the StringBuilder
//                sb.append(lemma);
//                sb.append(" ");
//            }
//        }
//
//        // Return the lemmatized output as a string
//        return sb.toString().trim();
//    }

    public static void main(String[] args) {
        String input = "This is a sample text! It contains U.S.A and punctuation marks.";
        String output = normalizeText(input);
        List<String> h = new ArrayList<>();
        h.add("U.S.A");
        h.add(", is a one of the biggest! countries.");
        h.add("isn't it?");
        System.out.println(h);
        System.out.println("-------------------after------------------");
        System.out.println(normalizeQueryText(h));
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
