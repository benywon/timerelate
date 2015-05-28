package TextClassification;

import Bases.MyFile;
import TimeRelate.FindWordFromSentence;
import TimeRelate.GetTime;
import libsvm.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**我们构建的特征直接就是这个文档的bag-of words信息
 * 首先还是用字典分词
 * Created by benywon on 2015/5/27.
 */
public class ClassifyTexts {
    public final String POSFILE=BuildTrainTxt.HistoryFILE;
    public final String NEGFILE=BuildTrainTxt.HistoryFILENEG;
    public final String SVMMODELPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\classiftextsvmmodel.svmmodel";
    public final String TRAINPOSLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\postive.listmap";
    public final String TRAINNEGLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\negative.listmap";
    public final String TESTPOSLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test\\postive.listmap";
    public final String TESTNEGLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test\\negative.listmap";
    public List<Map<Integer,Integer>> poslist=new ArrayList<>();
    public List<Map<Integer,Integer>> neglist=new ArrayList<>();
    public List<Map<Integer,Integer>> testlist=new ArrayList<>();
    public ClassifyTexts()
    {

    }
    //我们的样本集构建完毕之后  就可以来进行建模了
    public void BuildModel()
    {
        this.poslist= (List<Map<Integer, Integer>>) MyFile.ReadObj(TRAINPOSLISTPATH);
        this.neglist= (List<Map<Integer, Integer>>) MyFile.ReadObj(TRAINNEGLISTPATH);
        System.out.println("读取训练集完毕");
        //定义训练集点a{10.0, 10.0} 和 点b{-10.0, -10.0}，对应lable为{1.0, -1.0}
        svm_node[][] traindata=new svm_node[poslist.size()+neglist.size()][];
        double[] label=new double[poslist.size()+neglist.size()];
        int i1=-1;
        for(Map<Integer,Integer> map:poslist)
        {
            svm_node[] nodes = new svm_node[map.size()];
            int i=-1;
            for(Map.Entry<Integer,Integer> entry:map.entrySet())
            {
                svm_node node=new svm_node();
                node.index=entry.getKey();
                node.value=entry.getValue();
                nodes[++i]=node;
            }
            traindata[++i1]=nodes;
            label[i1]=1;
        }
        for(Map<Integer,Integer> map:neglist)
        {
            svm_node[] nodes = new svm_node[map.size()];
            int i=-1;
            for(Map.Entry<Integer,Integer> entry:map.entrySet())
            {
                svm_node node=new svm_node();
                node.index=entry.getKey();
                node.value=entry.getValue();
                nodes[++i]=node;
            }
            traindata[++i1]=nodes;
            label[i1]=-1;
        }

        //定义svm_problem对象
        svm_problem problem = new svm_problem();
        problem.l = poslist.size()+neglist.size(); //向量个数
        problem.x = traindata; //训练集向量表
        problem.y = label; //对应的lable数组

        //定义svm_parameter对象
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 100;
        param.C = 5;
        param.eps = 0.00001;

        //训练SVM分类模型
        System.out.println(svm.svm_check_parameter(problem, param)); //如果参数没有问题，则svm.svm_check_parameter()函数返回null,否则返回error描述。
        svm_model model = svm.svm_train(problem, param); //svm.svm_train()训练出SVM分类模型
        try {
            svm.svm_save_model(SVMMODELPATH,model); //该方法将svm_model保存到文件中
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

public List<Double> testresult(String dir,String outpath)
{
    List<Double> result=new ArrayList<>();
    MyFile.DeleteFile(outpath);
    List<Map<Integer,Integer>> testlistmap=BuildMyFeature(dir);
    try {
        svm_model model=svm.svm_load_model(SVMMODELPATH); //该方法将svm_model保存到文件中

    for(Map<Integer,Integer> map:testlistmap)
    {
        svm_node[] nodes = new svm_node[map.size()];
        int i=-1;
        for(Map.Entry<Integer,Integer> entry:map.entrySet())
        {
            svm_node node=new svm_node();
            node.index=entry.getKey();
            node.value=entry.getValue();
            nodes[++i]=node;
        }
        result.add(svm.svm_predict(model, nodes));
    }
    } catch (IOException e) {
        e.printStackTrace();
    }
    int right=0;
    for(double re:result)
    {
        MyFile.Write2File(re + "      ", outpath, true);
        if(re>0)
        {
            right++;
        }
    }
    System.out.println("正例为："+right);
    return result;
}

    //我们先构建正样本的的例子
    public void BuildMyFeature(String posdir,String negdir,String OutPosList,String OutNegList)
    {
        GetTime getTime=new GetTime();
        //首先得到这个正负样本里面的数据
        List<String> posfiles= new ArrayList<>();
        List<String> negfiles= new ArrayList<>();
        MyFile.getFilesFromDirectory(posfiles,posdir);
        MyFile.getFilesFromDirectory(negfiles,negdir);
        //首先建立正类的分类器
        List<Map<Integer,Integer>> poslist=new ArrayList<>();
        List<Map<Integer,Integer>> neglist=new ArrayList<>();
        for(String file:posfiles)
        {
            String content=MyFile.readfile(file);
            System.out.println(file);
            //对这一个文章的特征进行提取
            //首先得到这个文章所有的词项
            Map<Integer,Integer> map= FindWordFromSentence.ForwardMaxMatchList(content);
            poslist.add(map);
        }
        for(String file:negfiles)
        {
            System.out.println(file);
            String content=MyFile.readfile(file);
            //对这一个文章的特征进行提取
            //首先得到这个文章所有的词项
            Map<Integer,Integer> map= FindWordFromSentence.ForwardMaxMatchList(content);
            neglist.add(map);
        }
        MyFile.WriteMap(poslist,OutPosList);
        MyFile.WriteMap(neglist,OutNegList);
    }
    //我们先构建正样本的的例子
    public List<Map<Integer, Integer>> BuildMyFeature(String dir)
    {
        GetTime getTime=new GetTime();
        //首先得到这个正负样本里面的数据
        List<String> files= new ArrayList<>();
        MyFile.getFilesFromDirectory(files,dir);
        //首先建立正类的分类器
        List<Map<Integer,Integer>> list=new ArrayList<>();
        for(String file:files)
        {
            String content=MyFile.readfile(file);
            System.out.println(file);
            //对这一个文章的特征进行提取
            //首先得到这个文章所有的词项
            Map<Integer,Integer> map= FindWordFromSentence.ForwardMaxMatchList(content);
            list.add(map);
        }
        return list;
    }
    public void caldev()
    {

        //定义测试数据点c
        svm_node pc0 = new svm_node();
        pc0.index = 0;
        pc0.value = 4;
        svm_node pc1 = new svm_node();
        pc1.index = 22322;
        pc1.value = 12;
        svm_node[] pc = {pc0, pc1};

        //预测测试数据的lable
//        System.out.println(svm.svm_predict(model, pc));
    }

    public static void main(String[] args) {
        ClassifyTexts classifyTexts=new ClassifyTexts();
        classifyTexts.BuildMyFeature(classifyTexts.POSFILE,classifyTexts.NEGFILE,classifyTexts.TRAINPOSLISTPATH,classifyTexts.TRAINNEGLISTPATH);
        classifyTexts.BuildModel();
        classifyTexts.testresult("L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\temp","L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test\\posresult.txt");

    }

}
