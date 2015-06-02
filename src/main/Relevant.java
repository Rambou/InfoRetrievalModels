package main;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Nikolaos Bousios (Rambou)
 */
public class Relevant {

    private final Set<int[]> RelevantDocs;

    public Relevant() {
        this.RelevantDocs = new HashSet<>();
    }

    public void add(int QueryID, int DocID) {
        RelevantDocs.add(new int[]{QueryID, DocID});
    }

    public Set<Integer> getRelsWithQID(int qID) {
        Set<Integer> res = new HashSet<>();
        RelevantDocs.stream().filter((i) -> (i[0] == qID)).forEach((i) -> {
            res.add(i[1]);
        });
        return res;
    }

    public Set<Integer> getRelsWithQID(int qID, int until) {
        Set<Integer> res = new HashSet<>();
        int j = 0;
        for (int[] i : RelevantDocs) {
            if (j == until) {
                break;
            }
            if ((i[0] == qID)) {
                res.add(i[1]);
                j++;
            }
        }
        return res;
    }

    public int getDocsWithQID(int qID) {
        int docs = 0;
        docs = RelevantDocs.stream().filter((i) -> ((i[0] == qID))).map((_item) -> 1).reduce(docs, Integer::sum);
        return docs;
    }

    public Set<int[]> getRelevantDocs() {
        return RelevantDocs;
    }

    public int Size() {
        return RelevantDocs.size();
    }
}
