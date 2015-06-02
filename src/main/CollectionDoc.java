package main;

import java.io.Serializable;

/**
 *
 * @author Nikolaos Bousios (Rambou)
 */
public class CollectionDoc implements Serializable {

    private final int docid;
    private final int doclen;
    private final int tf;

    public CollectionDoc(int docid, int doclen, int tf) {
        this.docid = docid;
        this.doclen = doclen;
        this.tf = tf;
    }

    public int getDocid() {
        return docid;
    }

    public int getDoclen() {
        return doclen;
    }

    public int getTf() {
        return tf;
    }

    @Override
    public String toString() {
        return "DOCid:" + docid + " DOClen:" + doclen + " TF:" + tf;
    }
}
