package BaiduRelate.oldbaidu;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**处理百度百科标签页的程序
 * Created by benywon on 2015/4/17.
 */
public class DealTag {
    //匹配标签的 正则表达式
    public final static String TagRegex="\t\\d+\t\\d+\tn\tn\t([^\t]+)\t";
    public final static String NumRegex="^\\d+";//以开始位置查找匹配
    Map<Integer, Object> tagmap=new HashMap<>();
//public void
//我们没有办法 只能创建一个新的indexer本来是想用数据库做的 但是压力有点大 所以最后还是用lucene吧
    public void readline(String filepath)
    {
        String line="";
        Pattern p = Pattern.compile(TagRegex);//就是正则表达式匹配我们的tag标签的东西
        Pattern id = Pattern.compile(NumRegex);//就是正则表达式匹配我们的tag标签的东西
        try {
            InputStreamReader fr=new InputStreamReader(new FileInputStream(filepath),"gb18030");
            BufferedReader br=new BufferedReader(fr);
            while ((line=br.readLine())!=null) {
                Matcher m = p.matcher(line);
                Matcher n = id.matcher(line);
                String  tagliststr=null;
                int number=-1;
                if(n.find())
                {
                    String strnum=n.group();
                    number=Integer.parseInt(strnum);

                }
                if(m.find())
                {
                   tagliststr=m.group(1);
                }
                tagmap.put(number,tagliststr);
            }
            br.close();
            fr.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    /**
     * 一个宏观静态方法 将我们的tagfile处理成我们想要的形式
     * 这个方法也只使用一次 以后就可以查了
     */
    public static void dealtag()
    {
        DealTag dealTag=new DealTag();
        dealTag.readline("J:\\BAIDU\\Baike-AllInfo.txt");
//        dealTag.tagmap = BaseMethods.sortMapByKey(dealTag.tagmap);
        //将这个东西写进数据库或者lucene检索
        //写进数据库

        Iterator<Integer> iter = dealTag.tagmap.keySet().iterator();
        MYDB mydb=new MYDB();
        mydb.DeleteTable();
        mydb.CreateTable();
        int i=0;
        while (iter.hasNext()) {
            int key = iter.next();
            String value = (String) dealTag.tagmap.get(key);
            mydb.InsertItem(key,value);
            i++;
            System.out.println(i);
        }
        mydb.getindex();
        mydb.close();
    }
    public static void main(String[] args)
    {

        dealtag();


    }
}
