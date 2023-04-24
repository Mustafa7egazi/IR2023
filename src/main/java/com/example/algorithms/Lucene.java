package com.example.algorithms;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Lucene {

    private String documentId = "";


    public Lucene() throws IOException {
        separateDataSetIntoFiles();
    }


    public void luceneIndexer() throws IOException {

        Directory indexDictory = FSDirectory.open(new File("indx"));
        SimpleAnalyzer sa = new SimpleAnalyzer(Version.LUCENE_42);
//     configure index with the analyzer instance
        IndexWriterConfig analyzerConfig =
                new IndexWriterConfig(Version.LUCENE_42, sa);
        IndexWriter writer = new IndexWriter(indexDictory, analyzerConfig);
        String dataDir = "dataset"; // Index *.txt files from this directory
        File[] files = new File(dataDir).listFiles();

        if (files != null){
            for (File f: files) {   // for each file in the directory
                if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead()){
                    System.out.println("Indexing " + f.getCanonicalPath());
                    Document doc = new Document(); //
                    doc.add(new Field("filename", f.getName(), //Index file name
                            Field.Store.YES, Field.Index.NOT_ANALYZED));
                    doc.add(new Field("contents", new FileReader(f))); //Index file content
                    doc.add(new Field("fullpath", f.getCanonicalPath(), // Index file full path
                            Field.Store.YES, Field.Index.NOT_ANALYZED));

                    writer.addDocument(doc); // Add document to Lucene index
                }
            }
            System.out.println("# of Docs indexed = " + writer.numDocs());
            System.out.println("Lucene Index Built Successfully.");
            writer.close();  // close the writer
        }else {
            System.out.println("Dataset is empty");
        }

    }

    public List<String> luceneSearcher(String queryToSearch) throws IOException, ParseException {

        List<String> foundDocs = new ArrayList<>();

        Directory indexDirct = FSDirectory.open(new File("indx")); // index directory
        IndexReader reader = DirectoryReader.open(indexDirct);// Open index
        IndexSearcher is = new IndexSearcher(reader);
        // build & parse the query then run it
        QueryParser parser = new QueryParser(Version.LUCENE_41,
                "contents",
                new SimpleAnalyzer(Version.LUCENE_41));

        Query query = parser.parse(queryToSearch);

        TopDocs hits = is.search(query, 1460); // Search index

        // display the query result
        System.out.println("Found " + hits.totalHits +
                " document(s) that matched query '" + queryToSearch + "':");

        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc); //Retrieve matching document
            if (!foundDocs.contains(doc.get("filename"))){
                foundDocs.add(doc.get("filename"));
            }
//            System.out.println(doc.get("fullpath"));  //Display filename
        }

        reader.close();  //Close IndexReader
        return foundDocs;
    }


    private void separateDataSetIntoFiles() throws IOException {
        BufferedReader bufferedReader;
        File file = new File("cici.txt");
        FileReader reader = new FileReader(file);
        bufferedReader = new BufferedReader(reader);
        File dir = new File("dataset");
        if (!dir.exists()) {
            dir.mkdir();

            // separation if the dir created for the first time only

            String title ="";

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith(".I")) {
                   documentId = line.substring(3);
                }
                else if (line.startsWith(".T")) {
                    StringBuilder builder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null && !line.startsWith(".")) {
                        builder.append(line);
                    }
                    title = builder.toString();
                }
                else if (line.startsWith(".W")) {
                    StringBuilder builder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null && !line.startsWith(".")) {
                        builder.append(line).append(" ");
                    }
                    try {
                        File f = new File(dir.getAbsolutePath()+"/"+"Doc-"+documentId+".txt");
                        FileWriter writer = new FileWriter(f.getAbsolutePath());
                        // Write to the file
                        writer.write(String.valueOf(builder));
                        // Close the writer to release resources
                        writer.close();
                    } catch (IOException e) {
                        System.out.println("An error occurred while writing to the file.");
                    }
                }
            }
        }
    }
}
