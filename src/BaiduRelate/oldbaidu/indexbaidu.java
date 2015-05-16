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
     *�����������ѭ����ȡxml�ļ� �������Կ�ʼ�ͽ�β ��������Ĭ�����õ���<item></item>
     * Ȼ��Ѷ�����֮��Ķ�����xml���м��� ������һ������һ��doc
     * �����һ���ֱ��inputstream����sax����
     * ----------------------------------------------------------------
     * @param xmlfilepath �ļ���λ�� ����ǡ�default�����Ǵ�Ĭ�ϵ�λ����xml�ļ�
     * @param Indexpath ���ǵ�������λ��
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
            //�����ж��ǲ��ǿ�ͷ
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
     * ���������֮һ ����ָ���ֵ�λ��
     * @param indexPath �����Ҫ�������ŵ��Ǹ��ļ�������
     * @param dictfilepath �ֵ��λ��
     * @return ���ǵ�lucene��writer
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

