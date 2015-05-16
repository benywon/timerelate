package BaiduRelate;

import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Created by benywon on 2015/4/1.
 * 
 */
public class Indexer {


    private IndexWriter getIndexWriter(String indexPath,String dictpath)
            throws IOException
    {
        Directory indexDir = FSDirectory.open(new File(indexPath));
        File dictfile = new File(dictpath);
        Analyzer analyzer = new SimpleAnalyzer(dictfile);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2,
                analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(indexDir, iwc);
        return writer;
    }


    private  IndexWriter getIndexWriter(String indexPath)
            throws IOException
    {
        Directory indexDir = FSDirectory.open(new File(indexPath));
        Analyzer analyzer = new SimpleAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2,
                analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(indexDir, iwc);

        return writer;
    }
}
