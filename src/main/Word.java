package main;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Nikolaos Bousios (Rambou) The Word class is about each word in query.
 * It hold and contain valuable information about its frequency in documents and
 * other info. Also holds all the doc id that is in there.
 */
public class Word implements Serializable {

    private int ctf;
    private int df;
    private ArrayList<CollectionDoc> CollectionDocs;
    private String text;

    public Word(String text) {
        this.text = text;
        this.CollectionDocs = new ArrayList<>();
    }

    public Word(int ctf, int df) {
        this.ctf = ctf;
        this.df = df;
        this.CollectionDocs = new ArrayList<>();
    }

    public Word(String text, int ctf, int df) {
        this.text = text;
        this.ctf = ctf;
        this.df = df;
        this.CollectionDocs = new ArrayList<>();
    }

    public void addDoc(CollectionDoc e) {
        this.CollectionDocs.add(e);
    }

    public ArrayList<CollectionDoc> getDocs() {
        return CollectionDocs;
    }

    public int getCtf() {
        return ctf;
    }

    public void setCtf(int ctf) {
        this.ctf = ctf;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        String docs_toString = "Documents:\n";
        docs_toString = CollectionDocs.stream().map((s) -> s.toString() + "\n").reduce(docs_toString, String::concat);
        return "Word: " + text + "\nCTF:" + ctf + " " + "DF:" + df + "\n" + docs_toString;
    }
}
