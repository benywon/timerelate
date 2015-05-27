package TimeRelate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by benywon on 2015/4/20.
 */
public class FindWordFromSentence {
    public static Set<String> ItemList = GetTime.ItemList;
    private static final int MAX_MATCH_LEGTH=30;
    /**
     * 依靠正向最大匹配获取百科实体
     * @param sentence
     * @return
     */

    public static Set<String> ForwardMaxMatch(String sentence)
    {
         List<String> TermList=new ArrayList<>();
        //首先先将一个string转化成char数组
        char[] words=sentence.toCharArray();
        int len=words.length;
        int j=0;
        int num=1;
        for(int i=0;i<len;i+=num)
        {
            String wordadd=null;
            num=1;
            for(j=i+1;j<=len;j++)
            {
                String temp=sentence.substring(i,j);
                if(ItemList.contains(temp))
                {
                    num=j-i;
                    wordadd=temp;
                }
                if((j-i)>MAX_MATCH_LEGTH)//超过正向匹配的最大距离就断开
                {
                    break;
                }
            }
            if(wordadd!=null)
            {
                TermList.add(wordadd);
            }
        }
        Set<String> Termset=new HashSet<>(TermList);
        return Termset;
    }

}
