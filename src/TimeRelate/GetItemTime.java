package TimeRelate;

import BaiduRelate.Queryer;
import Bases.BaseMethods;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**这个的主要功能是给定一个item 我们得到它的一个时间区间
 * Created by benywon on 2015/4/16.
 */
public class GetItemTime {
    Date StartTime=new Date();
    Date EndTime=new Date();
//    List<String> regexlist=new ArrayList<>();
    public static  final String dateregex="公元前(\\d{1,4})年|(\\d{1,4})年[^前以之代]|年[\\(（](\\d{1,4})[\\)）]|(\\d{1,4})年$|(\\d{1,2})世纪";
    String[] regexlist={"(\\d+)","公元前","公元","距今","世纪"};
    String[] ChineseNum={"零","一","二","三","四","五","六","七","八","九","十"};
    public boolean HasDynasty=false;
    public String WhichDynasty="炳宁国";
    /**
     * 检索功能的  将一个item从百度里面拿出来
     * @return
     */
    public String  getitemfrombaidu(String item)
    {
        String content = Queryer.GetStringFrom("lemmatitle", item);
        return content;
    }

    /**
     * 我们从一个文章里面抽取时间 这个可以是规则的方法（正则表达式==） 也可以是我们的其他方法 比如事件抽取中时间的抽取
     * @param str 输入的一篇文章
     */
    public static List<Integer> gettimeinterval(String str)
    {
        List<Integer> datelist=new ArrayList<>();
        /*
        首先简单匹配 就是纯阿拉伯字符串  但是前面有一些“公元”“年”==
         */
        Pattern p = Pattern.compile(dateregex);
        Matcher m = p.matcher(str);
        while (m.find()){
            //如果找到了  我们看是公元前还是公元后
            if(m.group(2)!=null)//直接是年份
            {
                String number=m.group(2);
                int date=Integer.parseInt(number);
                datelist.add(date);
            }
            else if(m.group(3)!=null)
            {
                String number=m.group(3);
                int date=Integer.parseInt(number);
                datelist.add(date);
            }
            else if(m.group(4)!=null)//最后一个 可能是一个整篇文章的结尾有这个数字
            {
                String number=m.group(4);
                int date=Integer.parseInt(number);
                datelist.add(date);
            }
            else if(m.group(5)!=null)//有什么什么世纪
            {
                String number=m.group(5);
                int date=Integer.parseInt(number);
                datelist.add(date*100);
            }
            else//公元前
            {
                String number=m.group(1);
                int date=-Integer.parseInt(number);
                datelist.add(date);
            }


        }
        return datelist;
    }
    /**
     * 主要是抽取时代的标签
     * 我们只在里面找朝代
     * 我们从一个文章里面抽取时间 这个可以是规则的方法（正则表达式==） 也可以是我们的其他方法 比如事件抽取中时间的抽取
     * @param str 输入的一篇文章
     */
    public static List<Integer> gettimeintervalType6(String str)
    {
        List<Integer> datelist=new ArrayList<>();
        /*
        首先简单匹配 就是纯阿拉伯字符串  但是前面有一些“公元”“年”==
         */
        /**
         * 还有就是明显的时代的标签 抓取时代信息
         */
        List<TimeInterval> dList=DealDynasty.dynastylist;
        String Dyregex="";
        for(TimeInterval timeInterval:dList)
        {
            String dys=timeInterval.Dynasty;
            if(dys.length()==1)
            {
                dys+="朝";
            }
            Dyregex+=dys+"|";
        }
        Dyregex=Dyregex.substring(0,Dyregex.length()-1);//把最后一个|去掉
        Pattern p = Pattern.compile(dateregex);
        Matcher m = p.matcher(str);
        List<Object> dylist=new ArrayList<>();
        while (m.find()){
            //如果找到了  我们看是公元前还是公元后
            dylist.add(m.group());
        }
        //找完之后我们对其中的内容进行排序 看看出现的次数
        Map<Object, Integer> map= BaseMethods.List2Map(dylist);
        map=BaseMethods.sortByValue(map);//根据大小将其排序
        Iterator iterator=map.keySet().iterator();
        //取前两个就行
        int number=0;
        int[] value = new int[2];
        boolean validdys=true;
        while(iterator.hasNext())
        {

            String key= (String) iterator.next();
            value[number]=map.get(key);
            if(number==1)
            {
                if(value[0]==value[1])
                {
                    validdys=false;//说明没有找到有效的朝代信息
                }
                break;
            }
            number++;
        }




        return datelist;
    }
    /**+
     * 从一个字符串中获取百科页面时间list的函数
     * @param term
     * @return
     */
    public List<Integer> GetTermTimeList(String term)
    {
        GetItemTime getTime= new GetItemTime();
        List<Integer> datelist=new ArrayList<>();
        this.HasDynasty=false;
        //首先查找是不是包含一个朝代 要是的话 就直接将这个信息输出
        List<TimeInterval> dList=DealDynasty.dynastylist;
        for(TimeInterval timeInterval:dList)
        {
            String dy=timeInterval.Dynasty;
            if(term.equals(dy)||(term+"朝").equals(dy))
            {
                this.HasDynasty=true;
                this.WhichDynasty=dy;
                for(int i=timeInterval.StartTime;i<timeInterval.EndTime;i++)
                {
                    datelist.add(i);
                }
                break;
            }
        }
        if(!this.HasDynasty) {
            String content = getTime.getitemfrombaidu(term);
            //每次我们只取这个文章前一半的数据
            String realcontent=content.substring(0,content.length()/2);
            datelist = getTime.gettimeinterval(realcontent);
        }
        return datelist;
    }

    /**
     * 从一段文字里面获取时间信息并且有朝代的影响在里面
     * @param term
     * @return
     */
    public List<Integer> GetTermTimeType6(String term)
    {
        GetItemTime getTime= new GetItemTime();
        List<Integer> datelist=new ArrayList<>();
        this.HasDynasty=false;
        //首先查找是不是包含一个朝代 要是的话 就直接将这个信息输出
        List<TimeInterval> dList=DealDynasty.dynastylist;
        for(TimeInterval timeInterval:dList)
        {
            String dy=timeInterval.Dynasty;
            if(term.equals(dy)||(term+"朝").equals(dy))
            {
                this.HasDynasty=true;
                this.WhichDynasty=dy;
                for(int i=timeInterval.StartTime;i<timeInterval.EndTime;i++)
                {
                    datelist.add(i);
                }
                break;
            }
        }
        if(!this.HasDynasty) {//
            String home = getTime.getitemfrombaidu(term);
            datelist = getTime.gettimeinterval(home);
        }
        return datelist;
    }
}
