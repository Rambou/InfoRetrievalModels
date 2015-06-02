package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Nikolaos Bousios (Rambou)
 */
public class GeneralFunctions {

    public static void stemmDatabase(ArrayList<Query> Source, ArrayList<Query> Destination) {
        //For each query
        Source.stream().map((q) -> {
            //Initiaze stemmer class and array to hold words
            Stemmer s = new Stemmer();
            int Length = q.getWordsSum();
            //Create new Query
            Query nq = new Query(q.getId());
            //for each word into query
            for (int i = 0; i < Length; i++) {
                //Get Word 
                String word = q.getWords().get(i).getText();
                //Add words for stemming
                s.add(word.toCharArray(), word.length());
                //Stemm the word
                s.stem();
                nq.addWord(new Word(s.toString()));
            }
            return nq;
        }).forEach((nq) -> {
            Destination.add(nq);
        });
    }

    public static void removeStopWords(ArrayList<Query> Database0, ArrayList<Query> Database2, ArrayList<String> Stopwords) {
        //For each query
        for (Query q : Database0) {
            //Create new Query
            Query nq = new Query(q.getId());

            //Save words that are not stopwords to nq "newquery"
            for (Word word : q.getWords()) {
                boolean flag = true;
                for (String sword : Stopwords) {
                    if (sword.equals(word.getText())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    nq.addWord(word);
                }
            }
            //Save query into database
            Database2.add(nq);
        }
    }

    public static void Clean_Database(ArrayList<Query> Wordlist, ArrayList<Word> Cleaned) {
        Wordlist.stream().forEach((q) -> {
            q.setUniqueWords(Cleaned);
        });
    }

    public static int Crawl_Data(String request, ArrayList<Word> database) {
        Document doc;
        while (true) {
            try {
                //Connect to website and download content
                doc = Jsoup.connect(request).maxBodySize(61 * 2048000).get();
                //Create Elements that contain the text inside each <pre> HTML TAG
                Elements newsHeadlines = doc.select("pre");
                int i = 0, d = 0;
                for (Element item : newsHeadlines) {
                    //Check if the word has not any docs and move to the next 
                    if (i == 0) {
                        //Get ctf df
                        String[] value = item.text().replaceAll(" +", " ").split("\\n")[1].split(" ");

                        database.get(d).setCtf(Integer.parseInt(value[1]));
                        database.get(d).setDf(Integer.parseInt(value[2]));
                        if (Integer.parseInt(value[2]) == 0) {
                            i = 0;
                            d++;
                        } else {
                            i++;
                        }
                    } else {
                        boolean j = true;
                        for (String line : item.text().replaceAll(" +", " ").split("\\n")) {
                            if (j) {
                                j = false;
                                continue;
                            }
                            String[] l = line.split(" ");
                            CollectionDoc mydoc = new CollectionDoc(Integer.parseInt(l[1]), Integer.parseInt(l[2]), Integer.parseInt(l[3]));
                            database.get(d).addDoc(mydoc);
                        }
                        d++;
                        i = 0;
                    }
                }
                return d;
            } catch (IOException | NumberFormatException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                //System.exit(-1);
                System.out.println("!!!* Crawl Crashed... Restarting*!!!");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex1) {
                Logger.getLogger(GeneralFunctions.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public static void runBoolean(ArrayList<Query> database, int database_num, String Type, Relevant Relevance, Writter result) {
        //For each query
        database.stream().forEach((q) -> {
            //ArrayList contain all relevant docIDs for each query
            Set<Integer> retrieved = new HashSet<>();

            /*for each word in query grad collection doc
             put them in a list and store the number of those
             who have the same docid. Each word must hae the same docid
             in its collection docs to be stored in a relevancelist.*/
            boolean init = false;
            for (Word w : q.getWords()) {
                //boolean model
                Set<Integer> s = new TreeSet<>();
                w.getDocs().stream().forEach((k) -> {
                    s.add(k.getDocid());
                });
                /*First time that relevant list is empty,
                 the collection doc ids are stored in*/
                if (!init) {
                    retrieved = s;
                    init = true;
                } else {
                    /*Each time a new array of docs comming
                     it gets intersected with the newest in raw*/
                    retrieved.retainAll(s);
                }
                //if retrieved gets empty it means there if no point of further searching
                if (retrieved.isEmpty()) {
                    break;
                }
            }
            //Run Evaluation
            String values = evaluate(q.getId(), retrieved, Relevance);
            //Store Results to csv
            result.outln(q.getId() + "," + Type + ",Database" + database_num + ",Boolean," + values);
        });
    }

    public static void runTF(ArrayList<Query> database, int database_num, String Type, Relevant Relevance, Writter result) {
        //For each query
        for (Query q : database) {
            //get a list with words and each appearance in query
            List<WordAppear> WordAppearance = getWordAppearance(q);

            //Get max appearance
            int MaxApp = WordAppearance.get(0).appearance;

            //Calculate Weight of each word and store in in the WordAppearance List
            WordAppearance.stream().forEach((wa) -> {
                wa.setTFw((double) wa.appearance / MaxApp);
            });

            //set Laverage base on lemur for each database
            //http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?d=?
            double Lave = 0;
            if (database_num == 0 || database_num == 1) {
                Lave = 493;
            } else if (database_num == 2 || database_num == 3) {
                Lave = 288;
            }

            //ArrayList contain all relevant docIDs for each query
            List<Docu> retrieved = new LinkedList<>();

            //ArrayList contain all relevant docIDs for each query
            List<Docu> All = new LinkedList<>();

            //for each word in query, calculate TFid
            for (Word w : q.getWords()) {
                //Find the word in the WordAppearance List
                for (WordAppear wa : WordAppearance) {
                    //Get the word
                    if (w.getText().equals(wa.word)) {
                        //Calculate TFid  for all Documents in the word
                        for (CollectionDoc doc : w.getDocs()) {
                            double TFid = (double) doc.getTf() / (doc.getTf() + 0.5 + 1.5 * ((double) doc.getDoclen() / Lave));
                            Docu d = new Docu(doc.getDocid(), TFid * wa.TFw);
                            //wa.addDoc(d);
                            All.add(d);
                        }
                        break;
                    }
                }
            }

            //Create a comparator to order Descend
            Comparator comp = (Comparator<Docu>) (Docu o1, Docu o2) -> {
                Integer i1 = o1.getId();
                Integer i2 = o2.getId();
                return (i1 > i2 ? -1 : (Objects.equals(i1, i2) ? 0 : 1));
            };

            //return a sorted list according to appearance
            All = (List<Docu>) All.stream().sorted(comp).collect(Collectors.toList());

            retrieved.add(new Docu(All.get(0).getId(), All.get(0).getTf()));
            //Get the first of List
            double CurrentTF = 0;
            int i = 0;
            for (Docu d : All) {

                if (retrieved.get(i).getId() == d.getId()) {
                    CurrentTF += d.getTf();
                    retrieved.get(i).setTf(CurrentTF);
                } else {
                    retrieved.add(new Docu(d.getId(), d.getTf()));
                    CurrentTF = d.getTf();
                    i++;
                }
            }

            //Sort again the retrieved and keep only 100
            //Create a comparator to order Descend
            Comparator comp1 = (Comparator<Docu>) (Docu o1, Docu o2) -> {
                double i1 = o1.getTf();
                double i2 = o2.getTf();
                return (i1 > i2 ? -1 : (Objects.equals(i1, i2) ? 0 : 1));
            };
            //return a sorted list according to appearance
            retrieved = (List<Docu>) retrieved.stream().sorted(comp1).collect(Collectors.toList());

            //create list with 100 most relevant
            Set<Integer> rels = new HashSet<>();

            int j = 0;
            for (Docu d : retrieved) {
                if (j == 100) {
                    break;
                }
                rels.add(d.getId());
                j++;
            }

            //Run Evaluation
            String values = evaluate(q.getId(), new HashSet<>(rels), Relevance);
            //Store Results to csv
            result.outln(q.getId() + "," + Type + ",Database" + database_num + ",TF," + values);
        }
    }

    public static void runTFIDF(ArrayList<Query> database, int database_num, String Type, Relevant Relevance, Writter result) {
        //For each query
        for (Query q : database) {
            //get a list with words and each appearance in query
            List<WordAppear> WordAppearance = getWordAppearance(q);

            //Get max appearance
            int MaxApp = WordAppearance.get(0).appearance;

            //Calculate Weight of each word and store in in the WordAppearance List
            WordAppearance.stream().forEach((wa) -> {
                wa.setTFw((double) wa.appearance / MaxApp);
            });

            //set Laverage base on lemur for each database
            //http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?d=?
            double Lave = 0;
            double numDocs = 84678;
            if (database_num == 0 || database_num == 1) {
                Lave = 493;
            } else if (database_num == 2 || database_num == 3) {
                Lave = 288;
            }

            //ArrayList contain all relevant docIDs for each query
            List<Docu> retrieved = new LinkedList<>();

            //ArrayList contain all relevant docIDs for each query
            List<Docu> All = new LinkedList<>();

            //for each word in query, calculate TFid
            for (Word w : q.getWords()) {
                //Find the word in the WordAppearance List
                for (WordAppear wa : WordAppearance) {
                    //Get the word
                    if (w.getText().equals(wa.word)) {
                        //Calculate TFid  for all Documents in the word
                        for (CollectionDoc doc : w.getDocs()) {
                            double TFid = (double) doc.getTf() / ((double) doc.getTf() + 0.5 + 1.5 * ((double) doc.getDoclen() / Lave));
                            double TFIDF = TFid * Math.log((double) numDocs / w.getDf());
                            Docu d = new Docu(doc.getDocid(), TFIDF * wa.TFw * Math.log((double) numDocs / w.getDf()));
                            //wa.addDoc(d);
                            All.add(d);
                        }
                        break;
                    }
                }
            }

            //Create a comparator to order Descend
            Comparator comp = (Comparator<Docu>) (Docu o1, Docu o2) -> {
                Integer i1 = o1.getId();
                Integer i2 = o2.getId();
                return (i1 > i2 ? -1 : (Objects.equals(i1, i2) ? 0 : 1));
            };

            //return a sorted list according to appearance
            All = (List<Docu>) All.stream().sorted(comp).collect(Collectors.toList());

            retrieved.add(new Docu(All.get(0).getId(), All.get(0).getTf()));
            //Get the first of List
            double CurrentTF = 0;
            int i = 0;
            for (Docu d : All) {

                if (retrieved.get(i).getId() == d.getId()) {
                    CurrentTF += d.getTf();
                    retrieved.get(i).setTf(CurrentTF);
                } else {
                    retrieved.add(new Docu(d.getId(), d.getTf()));
                    CurrentTF = d.getTf();
                    i++;
                }
            }

            //Sort again the retrieved and keep only 100
            //Create a comparator to order Descend
            Comparator comp1 = (Comparator<Docu>) (Docu o1, Docu o2) -> {
                double i1 = o1.getTf();
                double i2 = o2.getTf();
                return (i1 > i2 ? -1 : (Objects.equals(i1, i2) ? 0 : 1));
            };
            //return a sorted list according to appearance
            retrieved = (List<Docu>) retrieved.stream().sorted(comp1).collect(Collectors.toList());

            //create list with 100 most relevant
            Set<Integer> rels = new HashSet<>();

            int j = 0;
            for (Docu d : retrieved) {
                if (j == 100) {
                    break;
                }
                rels.add(d.getId());
                j++;
            }

            //Run Evaluation
            String values = evaluate(q.getId(), new HashSet<>(rels), Relevance);
            //Store Results to csv
            result.outln(q.getId() + "," + Type + ",Database" + database_num + ",TF-IDF," + values);
        }
    }

    public static void runBM25(ArrayList<Query> database, int database_num, String Type, Relevant Relevance, Writter result) {
        //Preconfigured variables
        double S = 0, s = 0, k1 = 1.2, k3 = 100, b = 0.75, N = 84678.0;

        //set Laverage base on lemur for each database
        //http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?d=?
        double Lave = 0;
        if (database_num == 0 || database_num == 1) {
            Lave = 493;
        } else if (database_num == 2 || database_num == 3) {
            Lave = 288;
        }

        //For each query
        for (Query q : database) {

            //ArrayList contain all relevant docIDs for each query
            List<Docu> retrieved = new LinkedList<>();

            //ArrayList contain all relevant docIDs for each query
            List<Docu> All = new LinkedList<>();

            //for each word in query, calculate TFid
            for (Word w : q.getWords()) {
                //Calculate RSV  for all Documents in the word
                for (CollectionDoc doc : w.getDocs()) {
                    double TF = doc.getTf() / (doc.getTf() + 0.5 + 1.5 * (doc.getDoclen() / Lave));

                    Double RSV = ((double) ((S + 0.5) / (S - s + 0.5)) / ((w.getDf() - s + 0.5) / (N - w.getDf() - S + s + 0.5)))
                            * (((k1 + 1) * TF) / (k1 * ((1 - b) + b * (doc.getDoclen() / Lave)) + TF))
                            * (((k1 + 1) * TF) / k3 + TF);
                    Docu d = new Docu(doc.getDocid(), Math.log(RSV));
                    All.add(d);
                }
            }

            //Create a comparator to order Descend
            Comparator comp = (Comparator<Docu>) (Docu o1, Docu o2) -> {
                Integer i1 = o1.getId();
                Integer i2 = o2.getId();
                return (i1 > i2 ? -1 : (Objects.equals(i1, i2) ? 0 : 1));
            };

            //return a sorted list according to appearance
            All = (List<Docu>) All.stream().sorted(comp).collect(Collectors.toList());

            retrieved.add(new Docu(All.get(0).getId(), All.get(0).getTf()));
            //Get the first of List
            double CurrentTF = 0;
            int i = 0;
            for (Docu d : All) {

                if (retrieved.get(i).getId() == d.getId()) {
                    CurrentTF += d.getTf();
                    retrieved.get(i).setTf(CurrentTF);
                } else {
                    retrieved.add(new Docu(d.getId(), d.getTf()));
                    CurrentTF = d.getTf();
                    i++;
                }
            }

            //Sort again the retrieved and keep only 100
            //Create a comparator to order Descend
            Comparator comp1 = (Comparator<Docu>) (Docu o1, Docu o2) -> {
                double i1 = o1.getTf();
                double i2 = o2.getTf();
                return (i1 > i2 ? -1 : (Objects.equals(i1, i2) ? 0 : 1));
            };
            //return a sorted list according to appearance
            retrieved = (List<Docu>) retrieved.stream().sorted(comp1).collect(Collectors.toList());

            //create list with 100 most relevant
            Set<Integer> rels = new HashSet<>();

            int j = 0;
            for (Docu d : retrieved) {
                if (j == 100) {
                    break;
                }
                rels.add(d.getId());
                j++;
            }

            //Run Evaluation
            String values = evaluate(q.getId(), new HashSet<>(rels), Relevance);
            //Store Results to csv
            result.outln(q.getId() + "," + Type + ",Database" + database_num + ",BM25," + values);
        }

    }

    public static String evaluate(int qid, Set<Integer> retrieved, Relevant Relevance) {
        //Get relevant with query
        Set<Integer> relRetrivDocs = Relevance.getRelsWithQID(qid);
        //Max number of all relevant docs
        int MaxRelDocs = relRetrivDocs.size();
        //relDocs now contain the Docs retrieved that are relevant
        relRetrivDocs.retainAll(retrieved);

        //Calculate Recal
        Double rec = (double) relRetrivDocs.size() / MaxRelDocs; //Double truncatedDouble = new BigDecimal(rec).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

        Double prec = 0.0, Rprec = 0.0;
        //Calculate Precision
        if (!retrieved.isEmpty()) {
            prec = (double) relRetrivDocs.size() / retrieved.size();
        }

        //System.out.println("Query ID: " + qid + " RelevantDoc:" + relRetrivDocs.size() + "|" + relRetrivDocs.toString() + " RetrievedDoc:" + retrieved.size() + "|" + retrieved.toString());
        //Grab relevant docs with a specific queryID, until the number of retrieved docs
        Set<Integer> relRetrvDocsRprec = new HashSet<>();//= Relevance.getRelsWithQID(qid, retrieved.size());

        int j = 0;
        for (Integer i : retrieved) {
            if (j > MaxRelDocs) {
                break;
            }
            relRetrvDocsRprec.add(i);
            j++;
        }

        //Set Max number of all relevant docs
        int MaxRelDocsRprec = relRetrvDocsRprec.size();
        //relDocs now contain the Docs retrieved that are relevant
        relRetrvDocsRprec.retainAll(relRetrivDocs);

        //Calculate R-Precision
        if (MaxRelDocsRprec != 0) {
            Rprec = (double) relRetrvDocsRprec.size() / MaxRelDocsRprec;
        }

        /*System.out.println("relDocs1:" + RelDocs + "||Retrieved:" + retrieved.size() + "||relDocs:" + relDocs.size() + "||PRec:" + Rprec);
         System.out.println("Recal:" + rec + " Precission:" + prec + " R-Precision:"+ Rprec);*/
        return rec + "," + prec + "," + Rprec;
    }

    public static List<WordAppear> getWordAppearance(Query q) {
        //List to hold Words with appearances
        Set<WordAppear> wa = new HashSet<>();
        //Initiate common words with all words
        Set<Word> CommonWords = new HashSet(q.getWords());
        //Get Common words
        CommonWords.retainAll(q.getWords());

        //find max appearance
        CommonWords.stream().forEach((w) -> {
            int appearance = 0;
            appearance = q.getWords().stream().filter((w1) -> (w.equals(w1))).map((_item) -> 1).reduce(appearance, Integer::sum);
            wa.add(new WordAppear(w.getText(), appearance));
        });

        //Create a comparator to order Descend
        Comparator comp = (Comparator<WordAppear>) (WordAppear o1, WordAppear o2) -> {
            Integer i1 = o1.getAppearance();
            Integer i2 = o2.getAppearance();
            return (i1 > i2 ? -1 : (Objects.equals(i1, i2) ? 0 : 1));
        };

        //return a sorted list according to appearance
        return (List<WordAppear>) wa.stream().sorted(comp).collect(Collectors.toList());
    }

    public static class WordAppear {

        private final int appearance;
        private final String word;
        private double TFw;
        private final Set<Docu> dcol;

        public WordAppear(String word, int appearance) {
            this.word = word;
            this.appearance = appearance;
            this.dcol = new HashSet<>();
        }

        public void addDoc(Docu d) {
            dcol.add(d);
        }

        public Set<Docu> getDoc(Docu d) {
            return dcol;
        }

        public double getTFw() {
            return TFw;
        }

        public void setTFw(double TFw) {
            this.TFw = TFw;
        }

        public int getAppearance() {
            return appearance;
        }

        public String getWord() {
            return word;
        }

        @Override
        public String toString() {
            return "W:" + word + "|Ap:" + appearance + "|Docs:" + dcol + "\n";
        }

    }

    public static class Docu {

        private final int id;
        private double tf;

        public Docu(int id, double tf) {
            this.id = id;
            this.tf = tf;
        }

        public int getId() {
            return id;
        }

        public void setTf(double tf) {
            this.tf = tf;
        }

        public double getTf() {
            return tf;
        }

        @Override
        public String toString() {
            return "id:" + id + "|tf:" + String.valueOf(tf);
        }
    }

    class Comparator1 implements Comparator<Docu> {

        @Override
        public int compare(Docu o1, Docu o2) {
            Docu nd = null;
            if (o1.id == o2.id) {
                return 0;
            }
            return 1;
        }
    }
}
