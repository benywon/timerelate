package ESA;

import Bases.MyFile;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by benywon on 2015/4/8.
 */
public class BuildESAThread {
    public static int threadnum=0;
    public final String LemmaMapPath =BuildESAConcept.LemmaMapPath;
    private static IndexSearcher searcher;
    public static int MaxDocNum=6000000;
    public String IndexPath = "L:/program/cip/SAT-HISTORY/baiduindex";
    public static List<String> WORDS=new ArrayList<String>();
    //首先我们从字典中读取各个单词 当然有他们的频率
    public BuildESAThread()
    {
        Map Dicmap = MyFile.ReadMap(LemmaMapPath);//从中读取字典文件
        this.WORDS=new ArrayList<String>(Dicmap.keySet());//得到它的列表文件
        getsearcher();//获取索引文件搜索器列表
    }

    public static void main(String[] args) throws IOException {
        BuildESAThread nj=new BuildESAThread();
        System.out.println("开始");
        for(int i=0;i<6;i++)
        {
            new MitiSay().start();
        }
        System.out.println("siuhdf");
    }
    public static void BuildConceptESA() throws IOException {
        //从我们的WORDS里面取  然后查询
        Map<Integer, Map<Integer,Float>> WORDmap=new HashMap();
        int i=0;
        int number=BuildESAThread.threadnum;
        BuildESAThread.threadnum++;
        int max=WORDS.size();
        for(int j=number*10000;j<max;j++)
        {
            if(j==(number+1)*10000)
            {
                number=BuildESAThread.threadnum;
                j=BuildESAThread.threadnum*10000;
                BuildESAThread.threadnum++;
            }
             String word=WORDS.get(j);
//            我们用lucene查询看看是在哪些文档里面出现了
            int wordId = word.hashCode();
            Map<Integer,Float> map=new HashMap<Integer, Float>(getindexer("content",word));
            //然后将这个map放到大的map里面
            WORDmap.put(wordId,map);
            i++;
            System.out.println(j + "进程的" + BuildESAThread.threadnum);
            if((j-10000)%10000==0)
            {
                MyFile.WriteMap(WORDmap, "L:/program/cip/SAT-HISTORY/3月/时间地点相关/ESA-WORD/" + (int) (j/ 10000) + ".map");
                WORDmap.clear();
            }
        }
    }

    public static Map<Integer,Float> getindexer(String field, String term) throws IOException {
        Map<Integer,Float> map=new HashMap<Integer, Float>();
        Term keyword = new Term(field, term);
        TermQuery query=new TermQuery(keyword);
        TopDocs docs = searcher.search(query,MaxDocNum);
        ScoreDoc[] hits = docs.scoreDocs;
        //对于其中每个doc 我们得到它的打分 并且放到map里面去
        for (ScoreDoc hit : hits) {
            int num=hit.doc;
            float score=hit.score;
            map.put(num,score);
        }
        return map;
    }
    private void getsearcher()
    {
        Directory indexDir = null;
        try {
            indexDir = FSDirectory.open(new File(IndexPath));
            IndexReader ir = DirectoryReader.open(indexDir);
            this.searcher = new IndexSearcher(ir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MitiSay extends Thread {


    public void run() {
        try {
            Thread.sleep((int) Math.random() * 100);
            BuildESAThread.BuildConceptESA();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}