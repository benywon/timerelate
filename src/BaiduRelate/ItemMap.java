package BaiduRelate;

import Bases.MyFile;
import Bases.StopWords;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by benywon on 2015/4/16.
 */
public class ItemMap {
    public final static String ItemFile="J:\\BAIDU\\BAIDU\\words-my.dic";
    public final static String ItemFileStripEng="L:\\program\\cip\\SAT-HISTORY\\3月\\时间地点相关\\words-myafter.txt";
    public final static String ItemMapFile="L:\\program\\cip\\SAT-HISTORY\\3月\\时间地点相关\\ItemList.set";
    public static void File2List()
    {
        Set<String> list=new HashSet<>();
        FileReader fr= null;
        try {
            fr = new FileReader(ItemFileStripEng);
            BufferedReader br=new BufferedReader(fr);
            String line="";
            StopWords stopWords=new StopWords();
            while ((line=br.readLine())!=null) {
                if(stopWords.StopLists.contains(line))//包含停用词
                {
                    continue;
                }
                list.add(line);
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //写入obj文件
        MyFile.WriteMap(list,ItemMapFile);
    }
    public static Set readfrommap()
    {
        Set<String> set=new HashSet<>();
        set= (Set<String>)MyFile.ReadObj(ItemMapFile);
        return set;
    }

    public static void main(String[] args) {
        File2List();
    }
}
