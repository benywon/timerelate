package TextClassification;

import BaiduRelate.oldbaidu.MYDB;
import Bases.MyFile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by benywon on 2015/5/27.
 */
public class DealBaiduTag {
    public static Map<String,Integer> TagMap=new HashMap<>();
    public MYDB mydb;
    public static String outmappath="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\outmaps.map";


    public DealBaiduTag()
    {
        this.mydb=new MYDB();

    }

    /***
     * 将百度的标签取出来并且存成的格式为map<tag,count>
     *
     */
    public void getbaidutag2file()
    {
        Map<String,Integer> map=new HashMap<>();
        String sql = " select TagList from "+MYDB.TableName+";" ;
        try {
            ResultSet rs = this.mydb.statement.executeQuery(sql);
            while(rs.next())
            {
                String[] tags=rs.getString("TagList").split(",|、| ");
                for(String tag:tags)
                {
                    if(map.containsKey(tag))
                    {
                        map.put(tag,map.get(tag)+1);
                    }
                    else
                    {
                        map.put(tag,1);
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        MyFile.WriteMap(map,outmappath);
    }

    public static void main(String[] args)
    {
        DealBaiduTag dealBaiduTag=new DealBaiduTag();
        dealBaiduTag.getbaidutag2file();
    }
}
