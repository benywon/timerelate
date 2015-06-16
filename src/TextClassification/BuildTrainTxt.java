package TextClassification;

import BaiduRelate.Queryer;
import Bases.BaseMethods;
import Bases.MyFile;
import TimeRelate.FindWordFromSentence;
import TimeRelate.GetTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 从我们的所有百度文档里面的得到训练集的一个程序
 * 我们遍历每一个百度文档  根据历史教科书将其归类
 * Created by benywon on 2015/5/27.
 */
public class BuildTrainTxt {
    public final String HistoryBookDirectory="L:\\program\\cip\\SAT-HISTORY\\resources\\jiaocai4xml";
    public final String HistoryBookFilePath ="L:\\program\\cip\\SAT-HISTORY\\resources\\jiaocai4xml\\jiaocaicontent.txt";
    public final String TestHistoryBookFilePath ="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test-txt.txt";
    public final static String HistoryFILE="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\history-pos";
    public final static String HistoryFILENEG="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\history-neg";
    public final static String TestHistoryPos="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test\\Test-history-pos";
    public final static String TestHistoryNeg="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test\\Test-history-neg";
    public final static String TagCountFile="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\tagrelate\\TagCountFile.map";
    public final static String TagIndexFile="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\tagrelate\\indextag.map";
    public final static String TagIndexFileMost100="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\tagrelate\\indextag100.list";

    public static String[] noisetags={"电影","电视剧","歌曲","影视","演员","交通","词语","流行","生活","词汇","旅游","爱情","明星","工具"};//我们定义的虚假的噪声标签
    public static String[] postags={"历史","中国历史","历史人物","战争","革命","文物","朝代","世界历史","政治","宗教","年号","地名"};

    /**
     *产生一个跟历史有关的文件s  输入文本是一个自然语言文章 输出的是百科条目中包含有这些关键词并且
     * 类型是历史的文章 存成文件夹
      */
    public void getsettest()
    {
        String content=MyFile.readfile(TestHistoryBookFilePath);
        Map<String,Set<String>> indextag=new HashMap<>();
        //开始查找我们的实体
        //首先是获取所有的实体的列表
        Map<String,Integer> tagcount=new HashMap<>();
        String[] contents=content.split("。");
        Set<String> entityset=new HashSet<>();
        GetTime getTime=new GetTime();
        for(String txt:contents)
        {
            Set<String> set = FindWordFromSentence.ForwardMaxMatch(txt);//正向最大匹配
            entityset.addAll(set);
        }
        //开始在百度里面检索这个条目是不是
        Queryer queryer=new Queryer();
        for(String tag:entityset)
        {
            System.out.println(tag);
            String cc2=queryer.GetStringFromBaiduWithTagConstrain("lemmatitle", tag,noisetags,true);
            if(!cc2.equals(""))
            {
                String  path=this.HistoryFILENEG+"\\"+tag+".txt";
                MyFile.Write2File(cc2,path,false);
                continue;
            }

            String cc=queryer.GetStringFromBaiduWithTagConstrain("lemmatitle", tag,postags,false);
            if(!cc.equals(""))
            {
                Set<String> tags=Queryer.TagsCount;
                for(String thistag:tags)
                {
                    if(tagcount.containsKey(thistag))
                    {
                        int count=tagcount.get(thistag);
                        tagcount.put(thistag,++count);
                    }
                    else
                    {
                        tagcount.put(thistag,1);
                    }
                }
                String  path=this.HistoryFILE+"\\"+tag+".txt";
                indextag.put(path,tags);
                MyFile.Write2File(cc,path,false);
            }

        }
        MyFile.WriteMap(tagcount,TagCountFile);
        MyFile.WriteMap(indextag,TagIndexFile);
        System.out.println("测试集生成成功");

    }
    public void gethistorybookcontent()
    {
        //首先将我们这个教材目录下的所有文件遍历并且得到其中的xml文件
        List<String> list=new LinkedList<>();
        MyFile.getFilesFromDirectory(list,HistoryBookDirectory);
        for(String li:list)
        {
            if(!li.endsWith(".xml"))
            {
               list.remove(li);
            }
        }
        //现在所有的书本地址就有了 我们现在开始遍历
        String book=null;
        for(String file:list)
        {
            book+=gettxtfromfile(file);
        }
        MyFile.Write2File(book,this.HistoryBookFilePath,false);
    }

    /**
     * 从一个xml文本里面得到其中有用信息的纯文本
     * @param infile 文件地址
     * @return 最后的string
     */
   private String gettxtfromfile(String infile)
   {
       String value="";
       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
       DocumentBuilder db = null;
       try {
           db = dbf.newDocumentBuilder();
           Document document = db.parse(new File(infile));
           NodeList list = document.getElementsByTagName("content");
           for(int i = 0; i < list.getLength(); i++) {
               Element  element = (Element)list.item(i);
               String content=element.getTextContent();
               value+=content+"\n";
           }
       } catch (ParserConfigurationException e) {
           e.printStackTrace();
       } catch (SAXException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
        return value;
       }
    public static void gettagscount()
    {
        Map<String,Integer> map= (Map<String, Integer>) MyFile.ReadObj(TagCountFile);
        map= BaseMethods.sortByValue(map, false);
        String content="";
        List<String> list=new ArrayList<>();
        int num=0;
        for(Map.Entry<String, Integer> entry:map.entrySet()){
            String co=entry.getKey();
           int cou=entry.getValue();
            content+=co+"\t"+cou+"\n";
            if(++num<=100)
            {
                list.add(co);
            }

        }
        MyFile.WriteMap(list,TagIndexFileMost100);
        MyFile.Write2File(content,"L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\tagrelate\\tagscount.txt",false);
        System.out.println("成功");
    }
    public static void main(String[] args) {
        BuildTrainTxt buildTrainTxt=new BuildTrainTxt();
        buildTrainTxt.getsettest();
        gettagscount();

    }

}
