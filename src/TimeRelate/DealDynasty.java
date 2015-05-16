package TimeRelate;

import Bases.BaseMethods;
import Bases.MyFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by benywon on 2015/4/13.
 */
public class DealDynasty  {
    String DynastyName;
    Date DStartYear;
    Date DEndYear;
    int SpanDay=0;
    String CapitalAncient;
    String CapitalNowadays;
    String DFounder;
    String txtfilepath="L:\\program\\cip\\SAT-HISTORY\\4月\\时间相关\\朝代.txt";
    String mappath="L:\\program\\cip\\SAT-HISTORY\\4月\\时间相关\\朝代.map";
    public static List<TimeInterval> dynastylist=new ArrayList<>();

    public DealDynasty()
    {
        readfromobj();//读取朝代文件
    }

    /**
     * 从文件里面读取朝代
     * @param filepath
     */
    public void getDNfromTxt(String filepath)
    {
        String History= MyFile.readfile(filepath,"gbk");
        String[] dynastys=History.split("\r\n");
        for(String dynasty:dynastys)
        {
            TimeInterval times=new TimeInterval(dynasty);
            dynastylist.add(times);
        }
    }

    /**
     * 返回每一行的朝代信息
     * @param line
     * @return 结构体
     * 东汉	25年-220年	洛阳	河南洛阳	光武帝刘秀
     */
    private DealDynasty DealLine(String line)
    {
        DealDynasty instance =new DealDynasty();
        String[] info=line.split("\t");
        instance.DynastyName=info[0];
        String[] times=info[1].split("-");
        times[0]=times[0].replaceAll("年","");
        String DateFormatS="yyyy";
        if(times[0].indexOf("前")!=-1)
        {
            times[0]=times[0].replaceAll("前", "BC ");
            DateFormatS="G yyyy";
        }
        instance.DStartYear = BaseMethods.GetDateFromStr(times[0], DateFormatS);
        times[1]=times[1].replaceAll("年", "");
        String DateFormatB="yyyy";
        if(times[1].indexOf("前")!=-1)
        {
            times[1]=times[1].replaceAll("前","BC ");
            DateFormatB="G yyyy";
        }
        instance.DEndYear= BaseMethods.GetDateFromStr(times[1], DateFormatB);
        try {
            instance.SpanDay=BaseMethods.daysBetween(instance.DStartYear,instance.DEndYear,DateFormatS,DateFormatB);
        } catch (Exception e) {
        }
        if(info.length<3)
        {
            return instance;
        }
        instance.CapitalAncient=info[2];
        instance.CapitalNowadays=info[3];
        instance.DFounder=info[4];
        return instance;
    }

    /**
     * 将txt朝代文件写到map文件里面
     */
    public  void getdynastyfromtxt()
    {
        DealDynasty d=new DealDynasty();
        d.getDNfromTxt(txtfilepath);
        MyFile.WriteMap(d.dynastylist,mappath);
    }
    private void readfromobj()
    {
        this.dynastylist= (List<TimeInterval>) MyFile.ReadObj(this.mappath);
    }
    public static void main(String[] args)
    {
        DealDynasty dd=new DealDynasty();
        dd.getdynastyfromtxt();
//        dd.getdynastyfromtxt();
//        dd.DynastyList= (List<DealDynasty>) MyFile.ReadObj(dd.mappath);
//        System.out.println("hdiuf");
    }
}
