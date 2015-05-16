package BaiduRelate.oldbaidu;

import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

/**
 * Created by benywon on 2015/1/28.
 */
public class indexbaidu {
    public String start="<item>";
    public String end="</item>";
    /**
     * ----------------------------------------------------------------
     *这个方法可以循环读取xml文件 在这里以开始和结尾 我们在这默认设置的是<item></item>
     * 然后把读到的之间的东西用xml进行检索 并且这一部分是一个doc
     * 最后将这一部分变成inputstream交给sax解析
     * ----------------------------------------------------------------
     * @param xmlfilepath 文件的位置 如果是“default”就是从默认的位置找xml文件
     * @param Indexpath 我们的索引的位置
     * @throws IOException
     */
    public void indexbaidufile(String xmlfilepath,String Indexpath) throws IOException {
        IndexWriter writer = null;
        writer = getIndexWriter(Indexpath,"default");
        SAXParserFactory sf = SAXParserFactory.newInstance();
        SAXParser sp = null;

        try {
            sp = sf.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        saxxml saxxml=new saxxml();
        FileInputStream inputStream = null;
        if(xmlfilepath.equals("default"))
        {
            xmlfilepath="J:\\BAIDU\\Baike-AllContent.xml";
        }
        try {
            inputStream = new FileInputStream(xmlfilepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Charset.defaultCharset().displayName()
        // InputStreamReader streamReader = new InputStreamReader(inputStream, "gb18030");
        BufferedReader dr=new BufferedReader(new InputStreamReader(inputStream,"gbk"));
        String line =  dr.readLine();
        String page="";
        Boolean istrue=false;
        Long num=0L;
        while(line!= null){

            // System.out.println(line);
            //首先判断是不是开头
            if(line.indexOf(start)>=0)
            {
                istrue=true;
            }
            if(istrue) {
                page += line + "\n";
            }
            if(line.indexOf(end)>=0)
            {
                try {
                    InputStream is = new ByteArrayInputStream(page.getBytes("utf-8"));
                    sp.parse(is,saxxml);
                    writer.addDocument(saxxml.doc);
                    num++;
                    if(num%5000==0)
                    {
                        System.out.println(num+"");
                    }
                } catch (Exception e) {
//                        e.printStackTrace();
                }
                istrue=false;
                page="";
            }
            line = dr.readLine();
        }
        writer.close();
    }

    /**
     * 这个是其中之一 可以指定字典位置
     * @param indexPath 这个是要把索引放到那个文件夹下面
     * @param dictfilepath 字典的位置
     * @return 我们的lucene的writer
     * @throws IOException
     */
    private IndexWriter getIndexWriter(String indexPath,String dictfilepath)
            throws IOException
    {
        if(dictfilepath.equals("default"))
        {
            dictfilepath="J:\\BAIDU\\BAIDU";
        }
        Directory indexDir = FSDirectory.open(new File(indexPath));
        File dictfile=new File(dictfilepath);
        Analyzer analyzer = new SimpleAnalyzer(dictfile);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2,
                analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(indexDir, iwc);

        return writer;
    }
    private IndexWriter getIndexWriter(String indexPath)
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

