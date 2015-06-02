package main;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Nikolaos Bousios (Rambou)
 *
 * Query: Contains a bunch of words that will be used to ask a database. The
 * result that will be based on them.
 */
public class Query implements Serializable {

    private final int id;
    private final ArrayList<Word> Words;

    public Query(int id) {
        this.id = id;
        Words = new ArrayList<>();
    }

    public void addWord(Word w) {
        Words.add(w);
    }

    public Word getWord(String word) {
        for (Word w : Words) {
            if (w.getText().equals(word)) {
                return w;
            }
        }
        return null;
    }

    //!!!!!!gia duo lexeis den tis pernaei... prosoxh 8elei allagma h sunarthsh editWord()
    public void editWord(Word word) {
        for (int i = 0; i < Words.size(); i++) {
            if (Words.get(i).getText().equals(word.getText())) {
                Words.set(i, word);
                break;
            }
        }
    }

    public ArrayList<Word> getWords() {
        return Words;
    }

    public int getWordsSum() {
        return Words.size();
    }

    public int getId() {
        return id;
    }

    //return an arraylist of unique words in query
    public void setUniqueWords(ArrayList<Word> Cleaned) {
        //List<Word> deduped = Cleaned.stream().distinct().collect(Collectors.toList());
        for (Word w : Words) {
            boolean i = true;
            for (Word w1 : Cleaned) {
                if (w1.getText().equals(w.getText())) {
                    i = false;
                    break;
                }
            }
            if (i) {
                Cleaned.add(w);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        Words.forEach(n -> text.append(n.getText()).append(" "));
        return "Query ID: " + this.id + " Number of Words: " + getWordsSum() + " Text: " + text.toString();
    }

}
