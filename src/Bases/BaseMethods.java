package Bases;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by benywon on 2015/4/13.
 */
public class BaseMethods {
    /**
     * 对日期形式的时间处理函数
     * 输入是str 和格式
     * @param datestr 字符串时间
     * @param format 日期格式  如yyyy-mm-dd
     * @return
     */
    public static Date GetDateFromStr(String datestr,String format) {
        Date d;
        try {
            Format f = new SimpleDateFormat(format);
            d = (Date) f.parseObject(datestr);
        } catch (ParseException e) {
            d = null;
        }
        return d;
    }
    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     **@param sformat  日期格式 比如yyyy-mm-dd
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate,Date bdate,String sformat,String bformat) throws Exception
    {
        SimpleDateFormat sdf=new SimpleDateFormat(sformat);
        SimpleDateFormat bdf=new SimpleDateFormat(bformat);
        smdate=sdf.parse(sdf.format(smdate));
        bdate=sdf.parse(bdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis ();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis ();
        long between_days=(time2-time1)/ (1000*3600*24);
        return Integer.parseInt(String.valueOf (between_days));
    }

    /**
     * 对字典以value排序
     * @param map 输入的字典
     * @return 输出的字典
     */
    public static Map sortByValue(Map map)
    {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());

            }
        });
        Map result = new LinkedHashMap();

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    /**
     * 使用 Map按key进行排序
     * @param oriMap
     * @return
     */
    public static Map<Integer, Object> sortMapByKey(Map<Integer, Object> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<Integer, Object> sortedMap = new TreeMap<Integer, Object>(new Comparator<Integer>() {
            public int compare(Integer key1, Integer key2) {
                return key1 - key2;
            }});
        sortedMap.putAll(oriMap);
        return sortedMap;
    }

    /**
     * 整型数组的平均值
     * @param list
     * @return
     */
    public static float IntegerListAve(List<Integer> list)
    {
        int num=list.size();
        if(num==0)
        {
            return 0;
        }
        float sum=0;
       for(int number:list)
       {
           sum+=number;
       }
        return sum/num;
    }
    /**
     * 整型数组的中位数
     * @param list
     * @return
     */
    public static float IntegerListMid(List<Integer> list)
    {
        if(list==null)
        {
            return Float.MAX_VALUE;
        }
        int num=list.size();
        if(num==0)
        {
            return Float.MAX_VALUE;
        }
        if(num%2==0)
        {
            float mid=list.get(num/2-1)+list.get(num/2);
            return mid/2;
        }
        else
        {
            return list.get((num+1)/2-1);
        }
    }

    public static float FloatListAve(List<Float> in) {

        int num=in.size();
        if(num==0)
        {
            return 0;
        }
        float sum=0;
        for(float number:in)
        {
            sum+=number;
        }
        return sum/num;
    }

    /**
     * 将一个任意类型的list转化为map 其中map的key为list元素 value为出现的次数
     * @param inlist
     * @return
     */
    public static Map<Object,Integer> List2Map(List<Object> inlist)
    {
        Map<Object,Integer> map=new HashMap<>();
        for(Object i:inlist)
        {
            if(map.containsKey(i))
            {
                int num=map.get(i);
                map.put(i,++num);
            }
            else
            {
                map.put(i,1);
            }
        }
        return map;
    }

    /**
     * 查找一个在另一个里面出现几次
     * @param bigstr
     * @param str
     * @return
     */
    public static int getStrcount(String bigstr,String str)
    {
        int count=0;
        String[] k=bigstr.split(str);
        if(bigstr.lastIndexOf(str)==(str.length()-bigstr.length()))
            count=k.length;
        else
            count=k.length-1;
        if(count==0)
            return 0;
        else
            return count;
    }


}
//比较器类


