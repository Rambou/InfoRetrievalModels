package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.GeneralFunctions.removeStopWords;
import static main.GeneralFunctions.stemmDatabase;

/**
 *
 * @author Nikolaos Bousios (Rambou)
 */
public class Main {

    //Fiel Paths
    private static final String RELEVANCE_PATH = "relevance.txt";
    private static final String LOG_PATH = "logs.txt";
    private static final String QUERIES_PATH = "queries.txt";
    private static final String MYQUERIES_PATH = "myqueries.txt";
    private static final String STOPLIST_PATH = "stoplist.txt";
    private static final String RESULTS_PATH = "results.csv";

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        //Create Array Lists to hold words for each database into memmory
        ArrayList<Query> Database0 = new ArrayList<>();
        ArrayList<Query> Database1 = new ArrayList<>();
        ArrayList<Query> Database2 = new ArrayList<>();
        ArrayList<Query> Database3 = new ArrayList<>();
        ArrayList<Query> myQDatabase0 = new ArrayList<>();
        ArrayList<Query> myQDatabase1 = new ArrayList<>();
        ArrayList<Query> myQDatabase2 = new ArrayList<>();
        ArrayList<Query> myQDatabase3 = new ArrayList<>();
        
        Relevant relevant = new Relevant();

        //Initiate Writter Class
        Writter writter = new Writter(LOG_PATH, true);

        if (!(args.length > 0 && args[0].equals("models"))) {
            //<editor-fold defaultstate="collapsed" desc="Initiate data and store them">
            //Load files
            File QueriesFile = new File(System.getProperty("user.dir") + "\\" + QUERIES_PATH);
            File myQueriesFile = new File(System.getProperty("user.dir") + "\\" + MYQUERIES_PATH);
            File StopFile = new File(System.getProperty("user.dir") + "\\" + STOPLIST_PATH);

            //Create Array Lists to hold file content to memmory
            ArrayList<String> Stopwords = new ArrayList<>();

            //<editor-fold defaultstate="collapsed" desc="Read-Load Files">
            //<editor-fold defaultstate="collapsed" desc="Read-Load Queries - Create Database 0">
            writter.outln("***Loading Queries into memmory...***");
            try (BufferedReader br = new BufferedReader(new FileReader(QueriesFile))) {
                // process each line.
                for (String line; (line = br.readLine()) != null;) {
                    //Remove punctuation and converts letters to lowercase.
                    String[] words = line.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().split("\\s+");

                    //Create Query with his ID
                    Query qr = new Query(Integer.valueOf(words[0]));

                    //Store Words into Query
                    boolean first = true; //Cut the first because its 
                    for (String wr : words) {
                        if (first) {
                            first = false;
                            continue;
                        }
                        qr.addWord(new Word(wr));
                    }
                    //Store query into Database
                    Database0.add(qr);
                }
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
            //Null the file var
            QueriesFile = null;

            Database0.stream().forEach((q) -> {
                writter.outln(q.toString());
            });
            writter.outln("Number of queries loaded: " + Database0.size());
        //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Read-Load My Queries - Create Database 0">
            writter.outln("***Loading My Queries into memmory...***");
            try (BufferedReader br = new BufferedReader(new FileReader(myQueriesFile))) {
                // process each line.
                for (String line; (line = br.readLine()) != null;) {
                    //Remove punctuation and converts letters to lowercase.
                    String[] words = line.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().split("\\s+");

                    //Create Query with his ID
                    Query qr = new Query(Integer.valueOf(words[0]));

                    //Store Words into Query
                    boolean first = true; //Cut the first because its 
                    for (String wr : words) {
                        if (first) {
                            first = false;
                            continue;
                        }
                        qr.addWord(new Word(wr));
                    }
                    //Store query into Database
                    myQDatabase0.add(qr);
                }
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
            //Null the file var
            myQueriesFile = null;

            myQDatabase0.stream().forEach((q) -> {
                writter.outln(q.toString());
            });
            writter.outln("Number of My queries loaded: " + Database0.size());
        //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Read-Load StopWords">
            writter.outln("\n***Loading Stopwords into memmory...***");

            try (BufferedReader br = new BufferedReader(new FileReader(StopFile))) {
                // process each line.
                for (String line; (line = br.readLine()) != null;) {
                    Stopwords.add(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
            //Null the file var
            StopFile = null;

            writter.outln("Number of Stopwords loaded: " + Database0.size());
        //</editor-fold>

            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Create Databases 1,2,3! Stemmer/stopwords according to database">
            writter.outln("***Start Building databases for Queries file...***");
            writter.outln("\n\n---Building database 1 (Stemming)---");
            stemmDatabase(Database0, Database1);
            //Print Database1
            Database1.stream().forEach((q) -> {
                writter.outln(q.toString());
            });
            writter.outln("---Building database 2 (Stopwords Remove)---");
            removeStopWords(Database0, Database2, Stopwords);
            //Print Database2
            Database2.stream().forEach((q) -> {
                writter.outln(q.toString());
            });
            writter.outln("---Building database 3 (Stemming and Stopwords Remove)---");
            stemmDatabase(Database2, Database3);
            //Print Database3
            Database3.stream().forEach((q) -> {
                writter.outln(q.toString());
            });
            writter.outln("***Databases 1,2,3 has been Built...***");

            writter.outln("***Start Building databases for My Queries file...***");
            writter.outln("\n\n---Building database 1 (Stemming)---");
            stemmDatabase(myQDatabase0, myQDatabase1);
            //Print myQDatabase1
            myQDatabase1.stream().forEach((q) -> {
                writter.outln(q.toString());
            });
            writter.outln("---Building database 2 (Stopwords Remove)---");
            removeStopWords(myQDatabase0, myQDatabase2, Stopwords);
            //Print myQDatabase2
            myQDatabase2.stream().forEach((q) -> {
                writter.outln(q.toString());
            });
            writter.outln("---Building database 3 (Stemming and Stopwords Remove)---");
            stemmDatabase(myQDatabase2, myQDatabase3);
            //Print myQDatabase3
            myQDatabase3.stream().forEach((q) -> {
                writter.outln(q.toString());
            });
            writter.outln("***databases 1,2,3 has been Built...***");
        //</editor-fold>

            //Clean unused data
            Stopwords.clear();
            // request garbage collection  
            System.gc();
            writter.outln("***Cleaned unsed memmory!***");

            //<editor-fold defaultstate="collapsed" desc="Clean duplicate words from each database">
            //Create Arrays for each database
            ArrayList<Word> D0 = new ArrayList<>();
            ArrayList<Word> D1 = new ArrayList<>();
            ArrayList<Word> D2 = new ArrayList<>();
            ArrayList<Word> D3 = new ArrayList<>();

            writter.outln("\n\n***Start Cleaning Databases from duplicate words***");
            writter.outln("---Clean database 0---");
            GeneralFunctions.Clean_Database(Database0, D0);
            writter.out("Unique Words: " + String.valueOf(D0.size()) + " Words:");
            //Print cleaned Database0
            D0.stream().forEach((word) -> {
                writter.out(word.getText() + " ");
            });
            writter.outln("---Clean database 1---");
            GeneralFunctions.Clean_Database(Database1, D1);
            writter.out("Unique Words: " + String.valueOf(D1.size()) + " Words:");
            //Print cleaned Database1
            D1.stream().forEach((word) -> {
                writter.out(word.getText() + " ");
            });
            writter.outln("---Clean database 2---");
            GeneralFunctions.Clean_Database(Database2, D2);
            writter.out("Unique Words: " + String.valueOf(D2.size()) + " Words:");
            //Print cleaned Database2
            D2.stream().forEach((word) -> {
                writter.out(word.getText() + " ");
            });
            writter.outln("---Clean database 3---");
            GeneralFunctions.Clean_Database(Database3, D3);
            writter.out("Unique Words: " + String.valueOf(D3.size()) + " Words:");
            //Print cleaned Database3
            D3.stream().forEach((word) -> {
                writter.out(word.getText() + " ");
            });
            writter.outln("\n***Cleaned databases from duplicate words!***");
        //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Crawl Data from lemur">
            //<editor-fold defaultstate="collapsed" desc="Build request strings for lemur">
            String request0 = "http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?g=d&d=0";
            String request1 = "http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?g=d&d=1";
            String request2 = "http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?g=d&d=2";
            String request3 = "http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?g=d&d=3";

            writter.outln("\n\n***Build request strings for lemur for each database***");
            for (Word item : D0) {
                request0 += "&v=" + item.getText();
            }
            writter.outln("Request for Database 0: " + request0);
            for (Word item : D1) {
                request1 += "&v=" + item.getText();
            }
            writter.outln("Request for Database 1: " + request1);
            for (Word item : D2) {
                request2 += "&v=" + item.getText();
            }
            writter.outln("Request for Database 2: " + request2);
            for (Word item : D3) {
                request3 += "&v=" + item.getText();
            }
            writter.outln("Request for Database 3: " + request3);
            //</editor-fold>

            writter.outln("\n\n***Start Crawling Words...***");

            long startTime, stopTime;
            writter.out("Words are crawled from Database 0... ");
            startTime = System.currentTimeMillis();
            writter.out(" Crawled:" + GeneralFunctions.Crawl_Data(request0, D0) + " words");
            stopTime = System.currentTimeMillis();
            writter.outln(", Elapsed time was " + (stopTime - startTime) / 1000 + " seconds.");

            //for each Query in Database Store Word info
            for (Query q : Database0) {
                for (Word word : D0) {
                    q.editWord(word);
                }
            }
            for (Query q : myQDatabase0) {
                for (Word word : D0) {
                    q.editWord(word);
                }
            }
            //<editor-fold defaultstate="collapsed" desc="Write Objects into file">   
            try {
                ObjectOutputStream DB0 = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + "\\" + "Database0.dat"));
                DB0.writeObject(Database0);
                DB0.flush();
                DB0.close();

                ObjectOutputStream myQDB0 = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + "\\" + "myQDatabase0.dat"));
                myQDB0.writeObject(myQDatabase0);
                myQDB0.flush();
                myQDB0.close();
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
            //</editor-fold>

            writter.out("Words are crawled from Database 1... ");
            startTime = System.currentTimeMillis();
            writter.out(" Crawled:" + GeneralFunctions.Crawl_Data(request1, D1) + " words");
            stopTime = System.currentTimeMillis();
            writter.outln(", Elapsed time was " + (stopTime - startTime) / 1000 + " seconds.");

            //for each Query in Database Store Word info
            for (Query q : Database1) {
                for (Word word : D1) {
                    q.editWord(word);
                }
            }
            for (Query q : myQDatabase1) {
                for (Word word : D1) {
                    q.editWord(word);
                }
            }
            //<editor-fold defaultstate="collapsed" desc="Write Objects into file">   
            try {
                ObjectOutputStream DB1 = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + "\\" + "Database1.dat"));
                DB1.writeObject(Database1);
                DB1.flush();
                DB1.close();

                ObjectOutputStream myQDB1 = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + "\\" + "myQDatabase1.dat"));
                myQDB1.writeObject(myQDatabase1);
                myQDB1.flush();
                myQDB1.close();
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
            //</editor-fold>

            writter.out("Words are crawled from Database 2... ");
            startTime = System.currentTimeMillis();
            writter.out(" Crawled:" + GeneralFunctions.Crawl_Data(request2, D2) + " words");
            stopTime = System.currentTimeMillis();
            writter.outln(", Elapsed time was " + (stopTime - startTime) / 1000 + " seconds.");

            //for each Query in Database Store Word info
            for (Query q : Database2) {
                for (Word word : D2) {
                    q.editWord(word);
                }
            }
            for (Query q : myQDatabase2) {
                for (Word word : D2) {
                    q.editWord(word);
                }
            }
            //<editor-fold defaultstate="collapsed" desc="Write Objects into file">   
            try {
                ObjectOutputStream DB2 = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + "\\" + "Database2.dat"));
                DB2.writeObject(Database2);
                DB2.flush();
                DB2.close();

                ObjectOutputStream myQDB2 = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + "\\" + "myQDatabase2.dat"));
                myQDB2.writeObject(myQDatabase2);
                myQDB2.flush();
                myQDB2.close();
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
            //</editor-fold>

            writter.out("Words are crawled from Database 3... ");
            startTime = System.currentTimeMillis();
            writter.out(" Crawled:" + GeneralFunctions.Crawl_Data(request3, D3) + " words");
            stopTime = System.currentTimeMillis();
            writter.outln(", Elapsed time was " + (stopTime - startTime) / 1000 + " seconds.");

            //for each Query in Database Store Word info
            for (Query q : Database3) {
                for (Word word : D3) {
                    q.editWord(word);
                }
            }
            for (Query q : myQDatabase3) {
                for (Word word : D3) {
                    q.editWord(word);
                }
            }

            //<editor-fold defaultstate="collapsed" desc="Write Objects into file">   
            try {
                ObjectOutputStream DB3 = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + "\\" + "Database3.dat"));
                DB3.writeObject(Database3);
                DB3.flush();
                DB3.close();

                ObjectOutputStream myQDB3 = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + "\\" + "myQDatabase3.dat"));
                myQDB3.writeObject(myQDatabase3);
                myQDB3.flush();
                myQDB3.close();
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
        //</editor-fold>

            //</editor-fold>
            //Clean unused vars
            D0 = null;
            D1 = null;
            D2 = null;
            D3 = null;
            request0 = null;
            request1 = null;
            request2 = null;
            request3 = null;

            // request garbage collection  
            System.gc();
            writter.outln("***Cleaned unsed memmory!***");

            //</editor-fold>
        } else {
            writter.outln("***Loading Databases into memmory from file...***");
            long startTime, stopTime;
            startTime = System.currentTimeMillis();
            //<editor-fold defaultstate="collapsed" desc="Read objects from files and load them into memmory"> 
            ObjectInputStream D0 = new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + "\\" + "Database0.dat"));
            ObjectInputStream D1 = new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + "\\" + "Database1.dat"));
            ObjectInputStream D2 = new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + "\\" + "Database2.dat"));
            ObjectInputStream D3 = new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + "\\" + "Database3.dat"));
            ObjectInputStream myQD0 = new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + "\\" + "myQDatabase0.dat"));
            ObjectInputStream myQD1 = new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + "\\" + "myQDatabase1.dat"));
            ObjectInputStream myQD2 = new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + "\\" + "myQDatabase2.dat"));
            ObjectInputStream myQD3 = new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + "\\" + "myQDatabase3.dat"));

            try {
                Database0 = (ArrayList<Query>) D0.readObject();
                Database1 = (ArrayList<Query>) D1.readObject();
                Database2 = (ArrayList<Query>) D2.readObject();
                Database3 = (ArrayList<Query>) D3.readObject();

                myQDatabase0 = (ArrayList<Query>) myQD0.readObject();
                myQDatabase1 = (ArrayList<Query>) myQD1.readObject();
                myQDatabase2 = (ArrayList<Query>) myQD2.readObject();
                myQDatabase3 = (ArrayList<Query>) myQD3.readObject();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
            //Clean unused data
            D0 = null;
            D1 = null;
            D2 = null;
            D3 = null;
            myQD0 = null;
            myQD1 = null;
            myQD2 = null;
            myQD3 = null;
            // request garbage collection  
            System.gc();
            //</editor-fold>

            stopTime = System.currentTimeMillis();
            writter.outln("...Elapsed time to load databases was " + (stopTime - startTime) / 1000 + " seconds.");
        }

        //<editor-fold defaultstate="collapsed" desc="Read-Load relevance">
        writter.outln("***Loading Relevance into memmory...***");
        File RelevanceFile = new File(System.getProperty("user.dir") + "\\" + RELEVANCE_PATH);
        try (BufferedReader br = new BufferedReader(new FileReader(RelevanceFile))) {
            // process each line.
            for (String line; (line = br.readLine()) != null;) {
                String[] ln = line.split("\\s+");
                relevant.add(Integer.parseInt(ln[0]), Integer.parseInt(ln[1]));
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        //Null the file var
        RelevanceFile = null;

        writter.outln("Number of Relevance docs loaded: " + relevant.Size());
        //</editor-fold>

        //Initiate ResultWritter Class
        Writter result = new Writter(RESULTS_PATH, false);
        result.outln("QueryID,QueryType,Database,Model,Recall,Precision,R-Precision");

        //<editor-fold defaultstate="collapsed" desc="test Database">
        ArrayList<Query> f = new ArrayList<>();
        Query qr = new Query(54);
        Query qr1 = new Query(542);
        Query qr2 = new Query(52);

        CollectionDoc d1 = new CollectionDoc(114, 0, 0);
        CollectionDoc d2 = new CollectionDoc(861, 0, 0);
        CollectionDoc d3 = new CollectionDoc(13, 0, 0);
        CollectionDoc d4 = new CollectionDoc(3902, 0, 0);
        CollectionDoc d5 = new CollectionDoc(15, 0, 0);

        Word w1 = new Word("w1");
        Word w2 = new Word("w2");
        Word w3 = new Word("w3");
        Word w4 = new Word("w4");

        w1.addDoc(d1);
        w1.addDoc(d2);
        w1.addDoc(d3);
        w1.addDoc(d4);
        w1.addDoc(d5);
        w2.addDoc(d2);
        w2.addDoc(d3);
        w2.addDoc(d3);
        w2.addDoc(d3);
        w2.addDoc(d3);
        w2.addDoc(d4);

        w3.addDoc(d4);
        w4.addDoc(d4);
        w3.addDoc(d2);
        w4.addDoc(d2);

        qr.addWord(w1);
        qr.addWord(w1);
        qr.addWord(w1);
        qr.addWord(w2);
        qr.addWord(w3);
        qr.addWord(w3);
        qr.addWord(w4);
        qr1.addWord(w1);
        qr1.addWord(w1);
        qr1.addWord(w1);
        qr1.addWord(w1);
        qr2.addWord(w1);

        f.add(qr);
        f.add(qr1);
        f.add(qr2);
//</editor-fold>

        long startTime, stopTime;
        startTime = System.currentTimeMillis();

        writter.outln("\n***Run boolean model for All database***");
        writter.outln("--Runnng for Database0...");
        GeneralFunctions.runBoolean(Database0, 0, "FullWords", relevant, result);
        writter.outln("--Runnng for Database1...");
        GeneralFunctions.runBoolean(Database1, 1, "FullWords", relevant, result);
        writter.outln("--Runnng for Database2...");
        GeneralFunctions.runBoolean(Database2, 2, "FullWords", relevant, result);
        writter.outln("--Runnng for Database3...");
        GeneralFunctions.runBoolean(Database3, 3, "FullWords", relevant, result);
        writter.outln("--Runnng for myQDatabase0...");
        GeneralFunctions.runBoolean(myQDatabase0, 0, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase1...");
        GeneralFunctions.runBoolean(myQDatabase1, 1, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase2...");
        GeneralFunctions.runBoolean(myQDatabase2, 2, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase3...");
        GeneralFunctions.runBoolean(myQDatabase3, 3, "myWords", relevant, result);

        writter.outln("\n\n***Run TF model for All database***");
        writter.outln("--Runnng for Database0...");
        GeneralFunctions.runTF(Database0, 0, "FullWords", relevant, result);
        writter.outln("--Runnng for Database1...");
        GeneralFunctions.runTF(Database1, 1, "FullWords", relevant, result);
        writter.outln("--Runnng for Database2...");
        GeneralFunctions.runTF(Database2, 2, "FullWords", relevant, result);
        writter.outln("--Runnng for Database3...");
        GeneralFunctions.runTF(Database3, 3, "FullWords", relevant, result);
        writter.outln("--Runnng for myQDatabase0...");
        GeneralFunctions.runTF(myQDatabase0, 0, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase1...");
        GeneralFunctions.runTF(myQDatabase1, 1, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase2...");
        GeneralFunctions.runTF(myQDatabase2, 2, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase3...");
        GeneralFunctions.runTF(myQDatabase3, 3, "myWords", relevant, result);

        writter.outln("\n\n***Run TF-IDF model for All database***");
        writter.outln("--Runnng for Database0...");
        GeneralFunctions.runTFIDF(Database3, 0, "FullWords", relevant, result);
        writter.outln("--Runnng for Database1...");
        GeneralFunctions.runTFIDF(Database1, 1, "FullWords", relevant, result);
        writter.outln("--Runnng for Database2...");
        GeneralFunctions.runTFIDF(Database2, 2, "FullWords", relevant, result);
        writter.outln("--Runnng for Database3...");
        GeneralFunctions.runTFIDF(Database3, 3, "FullWords", relevant, result);
        writter.outln("--Runnng for myQDatabase0...");
        GeneralFunctions.runTFIDF(myQDatabase0, 0, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase1...");
        GeneralFunctions.runTFIDF(myQDatabase1, 1, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase2...");
        GeneralFunctions.runTFIDF(myQDatabase2, 2, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase3...");
        GeneralFunctions.runTFIDF(myQDatabase3, 3, "myWords", relevant, result);

        writter.outln("\n\n***Run BM25 model for All database***");
        writter.outln("--Runnng for Database0...");
        GeneralFunctions.runBM25(Database0, 0, "FullWords", relevant, result);
        writter.outln("--Runnng for Database1...");
        GeneralFunctions.runBM25(Database1, 1, "FullWords", relevant, result);
        writter.outln("--Runnng for Database2...");
        GeneralFunctions.runBM25(Database2, 2, "FullWords", relevant, result);
        writter.outln("--Runnng for Database3...");
        GeneralFunctions.runBM25(Database3, 3, "FullWords", relevant, result);
        writter.outln("--Runnng for myQDatabase0...");
        GeneralFunctions.runBM25(myQDatabase0, 0, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase1...");
        GeneralFunctions.runBM25(myQDatabase1, 1, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase2...");
        GeneralFunctions.runBM25(myQDatabase2, 2, "myWords", relevant, result);
        writter.outln("--Runnng for myQDatabase3...");
        GeneralFunctions.runBM25(myQDatabase3, 3, "myWords", relevant, result);

        stopTime = System.currentTimeMillis();
        writter.outln("...Elapsed time for running all models was " + (stopTime - startTime) / 1000 + " seconds.");

        //Close Log and Result File
        writter.close();
        result.close();

        //Clean memmory
        Database0 = null;
        Database1 = null;
        Database2 = null;
        Database3 = null;
        myQDatabase0 = null;
        myQDatabase1 = null;
        myQDatabase2 = null;
        myQDatabase3 = null;
        // request garbage collection  
        System.gc();
    }

}
