package TextClassification;

import Bases.MyFile;

import java.util.ArrayList;
import java.util.List;

/**我们构建的特征直接就是这个文档的bag-of words信息
 * 首先还是用字典分词
 * Created by benywon on 2015/5/27.
 */
public class ClassifyTexts {
    public final String POSFILE=BuildTrainTxt.HistoryFILE;
    public final String NEGFILE=BuildTrainTxt.HistoryFILENEG;
    //我们先构建正样本的的例子
    public void TestClassify()
    {
        //首先得到这个正负样本里面的数据
        String posdir=POSFILE.replaceAll("\\\\王思聪.txt","");
        String negdir=NEGFILE.replaceAll("\\\\王思聪.txt","");
        List<String> posfiles= new ArrayList<>();
        List<String> negfiles= new ArrayList<>();
        MyFile.getFilesFromDirectory(posfiles,posdir);
        MyFile.getFilesFromDirectory(negfiles,posdir);
        //首先建立正类的分类器
        for(String file:posfiles)
        {

        }
    }



}
