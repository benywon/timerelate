package TimeRelate;

import Bases.BaseMethods;

import java.util.*;

/**
 * Created by benywon on 2015/4/17.
 */
public class DealTime {
    /**
     * 从一个输入的时间线中抽出有用的时间片段
     * @param inlist
     * @return 一个map和时间出现的次数有关
     */
    public Map<Integer,Integer> List2Map(List<Integer> inlist)
    {
        Map<Integer,Integer> map=new HashMap<>();
        for(int i:inlist)
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
     * 对我们的map排序的一种方法 按key排序
     * @param map
     * @return
     */
    public Map<Integer,Integer> SortDateMapByKey(Map<Integer,Integer> map)
    {

        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Integer, Integer> sortedMap = new TreeMap<Integer, Integer>(new Comparator<Integer>() {
            public int compare(Integer key1, Integer key2) {
                return key1 - key2;
            }});
        sortedMap.putAll(map);
        return sortedMap;
    }
    /**
     * 对我们的map排序的一种方法 按value排序
     * @param map
     * @return
     */
    public Map<Integer,Integer> SortDateMapByValue(Map<Integer,Integer> map)
    {

        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());

            }
        });
        Map<Integer, Integer> result = new LinkedHashMap();

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put((Integer)entry.getKey(),(Integer)entry.getValue());
        }
        return result;
    }
    /**
     * 对列表排序
     * @param list
     * @return
     */
    public List<Integer> SortIntegerList(List<Integer> list)
    {
        Collections.sort(list);
        return list;
    }

    /**
     * 将时间区间的噪声去掉
     * 我们的方案中 对出现次数最多的时间（相当于事件时间的前后侧重点不一样）
     * 对于众数后面的时间赋予的权重更高
     * @param list 包含噪声的时间区间
     * @return 去除掉噪声的时间区间
     */
    public List<Integer> StripTimeList(List<Integer> list)
    {
        List<Integer> outlist=new ArrayList<>();
        Map<Integer,Integer> map= List2Map(list);
        map=SortDateMapByValue(map);
        Iterator iter = map.keySet().iterator();
        int i=0;
        int max=0;
        int maxyear=-20000;
        int totalnum=list.size();//总共出现的年份的个数
        int max_year_equal_num=1;
        while (iter.hasNext()) {
            i++;
            Integer key = (Integer) iter.next();
            Integer value=map.get(key);
            if(i==1)
            {
                maxyear=key;
                max=value;
            }
            else
            {
                if(max-value<=1)//两者之差小于1就算进去
                {
                    max_year_equal_num++;
                }
            }
        }
        //首先我们要有策略的进行下一步的
        //max是最大值（数字 没啥用） max_year_equal_num是最大值的年份
        //totalnum 是出现的总数字数目
        List<Integer> SortedList=SortIntegerList(list);
        int startnum = 0;
        int endnum=totalnum-1;//默认是中位数
        if(max_year_equal_num<=3)//只有一个最大的
        {
            for(i=0;i<totalnum;i++)
            {
                if(SortedList.get(i)==maxyear)
                {
                    startnum=i;
                    break;
                }
            }
            for(i=totalnum-1;i>=0;i--)
            {
                if(SortedList.get(i)==maxyear)
                {
                    endnum=i;
                    break;
                }
            }
            int decentnum=totalnum*1/10+1;//我们认为众数右面的比较重要
            int acentnum=totalnum*1/10+1;
            int mid=(startnum+endnum)/2;
            //对于前面和后面的 我们也要有比例的加入
            List<Integer> pre=new ArrayList<>();
            List<Integer> suf=new ArrayList<>();
            for(i=((mid-decentnum)>=0?(mid-decentnum):0);i<=(mid+acentnum)&&i<totalnum;i++)
            {
                if(i<mid) {
                    pre.add(SortedList.get(i));
                }
                else if(i>mid)
                {
                    suf.add(SortedList.get(i));
                }
            }
            float premiddis=SortedList.get(mid)-BaseMethods.IntegerListMid(pre);
            float sufmiddis=BaseMethods.IntegerListMid(suf)-SortedList.get(mid);
            //前后的比例是看方差来算的 如果差距大 就小 如果小 就大量 总共1/5
            float precont=sufmiddis/(premiddis+sufmiddis);
            float sufcont=1-precont;
            decentnum= (int) (totalnum/5*precont);
            acentnum=(int) (totalnum/5*sufcont);
           for(i=((mid-decentnum)>=0?(mid-decentnum):0);i<=(mid+acentnum)&&i<totalnum;i++)
           {
               outlist.add(SortedList.get(i));
           }
        }
        else//不只有一个最大值  所以我们要取中间的了 这个可以取大一点
        {
            startnum=totalnum*2/5;
            endnum=totalnum*3/5;
            for(i=startnum;i<=endnum;i++)
            {
                outlist.add(SortedList.get(i));
            }
        }
        return outlist;
    }

    /**
     * 计算两个时间序列交叉程度的函数
     * 2015-04-19:还是最简单的看平均值
     * @param timelist1 时间序列1
     * @param timelist2 时间序列2
     * @return 最后相关度的一个打分 如果越小说明越近
     */
    public static float CalTimeRelation(List<Integer> timelist1,List<Integer> timelist2)
    {
        int time1= (int) BaseMethods.IntegerListMid(timelist1);
        int time2= (int) BaseMethods.IntegerListMid(timelist2);
        if(time1==Integer.MAX_VALUE&&time2==Integer.MAX_VALUE)
        {
            return Integer.MAX_VALUE;
        }
        return Math.abs(time1-time2);
    }
    //第四种  我们就是计算类似方差一样的东西
    public static float CalTimeType4(List<Float> in)
    {
        if(in.isEmpty())
        {
            return Float.MAX_VALUE;
        }
        else if(in.size()==1)
        {
            return Float.MAX_VALUE;
        }
        float ave=BaseMethods.FloatListAve(in);
        float sum=0;
        int num=in.size();
        for(int i=0;i<num;i++)
        {
            sum+=Math.abs(in.get(i)-ave);
        }
        sum/=num;
        return sum;
    }
}
