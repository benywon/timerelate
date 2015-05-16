package BaiduRelate.oldbaidu;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

/**
 * Created by benywon on 2015/4/2.
 * 因为在lucene4。0以后我们无法通过指定field里面的参数来构造field域类型 所以我们需要进行融合 将field进行扩展
 * 使其可以存储TermVector
 */
public class VecTextField extends Field {

    /* Indexed, tokenized, not stored. */
    public static final FieldType TYPE_NOT_STORED = new FieldType();

    /* Indexed, tokenized, stored. */
    public static final FieldType TYPE_STORED = new FieldType();

    static {
        TYPE_NOT_STORED.setIndexed(true);
        TYPE_NOT_STORED.setTokenized(true);
        TYPE_NOT_STORED.setStoreTermVectors(true);
        TYPE_NOT_STORED.setStoreTermVectorPositions(true);
        TYPE_NOT_STORED.freeze();

        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setStored(true);
        TYPE_STORED.setStoreTermVectors(true);
        TYPE_STORED.setStoreTermVectorPositions(true);
        TYPE_STORED.freeze();
    }


    /**
     * 扩展当前的索引器的field
     * @param name
     * @param value
     * @param store
     */
    public VecTextField(String name, String value, Store store) {
        super(name, value, store == Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
    }

    /**
     * Creates a new un-stored TextField with TokenStream value.
     */
    public VecTextField(String name, TokenStream stream) {
        super(name, stream, TYPE_NOT_STORED);
    }
}