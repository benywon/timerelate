package BaiduRelate;


//import org.apache.lucene.analysis.core.SimpleAnalyzer;

import BaiduRelate.oldbaidu.MYDB;
import TimeRelate.DealDynasty;
import TimeRelate.TimeInterval;
import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by benywon on 2015/4/1.
 */
public class Queryer {
    public static final String indexDicPath="J:\\BAIDU\\baidu-index";
    public static MYDB MyDb=new MYDB();
    private static List<String> HistoryTags=new ArrayList<>();//我们的历史标签还应该包括朝代名称
    public static String[] noisetags={"电影","电视剧","歌曲","解释","影视","流行","词语","词汇","汉字"};//我们定义的虚假的噪声标签
    static IndexReader ir = null;
    public Queryer()
    {
        DealDynasty timeInterval =new DealDynasty();
        String[] tags={"历史","朝代","战争","著作","文学","革命","文物","文化","人物","科技"};

        for (String tag:tags)
        {
            HistoryTags.add(tag);
        }
        List<TimeInterval> list=timeInterval.dynastylist;
        for(TimeInterval timeInterval1:list)
        {
            HistoryTags.add(timeInterval1.Dynasty);
        }
        try {
            Directory indexDir = FSDirectory.open(new File(indexDicPath));
            ir = DirectoryReader.open(indexDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void test(String indexPath,String dictpath) throws IOException, ParseException {

        Directory indexDir = FSDirectory.open(new File(indexPath));
        IndexReader ir = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(ir);
        File dictfile = new File(dictpath);
        QueryParser parser = new QueryParser("lemmatitle", new SimpleAnalyzer());//这个是我们在这个里面进行搜索查找主要是查百科条目
        String   querys="有西方学者认为“近代世界赖以建立的种种发明与发现可能有一半来源于中国”。传入欧洲并对“近代世界”产生深刻影响的宋代的科技成就是";
        System.out.println(querys);
        Query query = parser.parse(querys);
        TopDocs docs = searcher.search(query, 5);
        ScoreDoc[] hits = docs.scoreDocs;
        for (ScoreDoc hit : hits) {
            Explanation explanation = searcher.explain(query, hit.doc);
            System.out.println("doc: " + hit.doc + " score: " + hit.score);
            Document doc = searcher.doc(hit.doc);
//          System.out.println("在: "+ doc.getField("summarycontent").stringValue());
            System.out.println("在: "+ doc.getField("content").stringValue());
//          System.out.println("在: "+ doc.getField("sublemmatitle").stringValue());
            System.out.println("------------------------------------"+explanation.toString());

        }
    }
    /**
     * 输入一个词项 我们在百科里面将其查找出来
     * field
     *
     * @param field 是域  一般为lemmatitle
     * @param inquery 是查找的解析器
     * @return 注意如果这个名词有歧义 那么我们将其对应的所有百科条目都给串联起来
     */
 public static String GetStringFrom(String field,String inquery)
 {
     IndexSearcher searcher = new IndexSearcher(ir);
     Term t =new Term(field,inquery);
     String str="";
     try {
         TermQuery query=new TermQuery(t);
         ScoreDoc[] docs=searcher.search(query,20).scoreDocs;
         for (ScoreDoc hit : docs) {
             //我们还要有一个判断是不是
             Document doc = searcher.doc(hit.doc);
             if(isvalidhistory(doc)) {
                 str += doc.getField("content").stringValue();
             }
         }

     } catch (IOException e) {
         e.printStackTrace();
     }
    return str;
 }

    public static boolean isvalidhistory(Document doc)
    {
        String idstr=doc.getField("sublemmaid").stringValue();
        int id=Integer.parseInt(idstr);
        String result;
        result=MyDb.FindKey(id);
        if(result==null)
        {
            return false;
        }
        if(result.equals("null"))
        {
            return false;
        }
        Set<String> tags= new HashSet<String>(Arrays.asList(result.split(",")));
        //先排除噪声的影响
        for(String tag:tags)
        {
            for(String noise:noisetags)
            {
                if(tag.contains(noise))
                {
                    return false;
                }
            }
        }
        //然后再查找是不是真正的
        for(String tag:tags)
        {
            for(String HistoryTag:HistoryTags)
            {
                if(tag.contains(HistoryTag))
                {
                    return true;
                }
            }
        }
        return false;
    }

}
