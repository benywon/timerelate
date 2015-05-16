package Bases;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by benywon on 2015/4/16.
 */
public class StopWords {
    public final static String StopFilePath="L:\\program\\cip\\SAT-HISTORY\\3月\\stopwords_cn.txt";
    public final static String StopListPath="L:\\program\\cip\\SAT-HISTORY\\3月\\stopwords_cn.set";
    public Set<String> StopLists= new HashSet<>();
    public StopWords()
    {
        StopLists= (Set<String>) MyFile.ReadObj(StopListPath);
    }
    public static void getlistfromfile()
    {
        Set<String> list=new HashSet<>();
        FileReader fr= null;
        try {
            fr = new FileReader(StopFilePath);
            BufferedReader br=new BufferedReader(fr);
            String line="";
            while ((line=br.readLine())!=null) {
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
        MyFile.WriteMap(list,StopListPath);
    }
    public static void main(String[] args) {
        getlistfromfile();
    }
}
