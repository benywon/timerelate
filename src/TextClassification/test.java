package TextClassification;

import Bases.MyFile;

import java.util.List;

/**
 * Created by benywon on 2015/5/27.
 */
public class test {
    public static void main(String[] args) {
//        String HistoryFILENEG="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\history-neg";
//        String  path=HistoryFILENEG+"\\\\"+"李开复"+".txt";
//        System.out.println(path);
//        String cs1="我的";
//        String cs2="我们的祖国是花园";
//        if(cs2.contains(cs1))
//        {
//            System.out.println("chgg");
//        }
        List<double[]> result= (List<double[]>) MyFile.ReadObj("L:\\program\\cip\\SAT-HISTORY\\3月\\java\\out\\artifacts\\java_jar\\resultneg6.list");
        System.out.println("结束");

    }
}
