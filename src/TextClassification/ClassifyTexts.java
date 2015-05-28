package TextClassification;

import Bases.MyFile;
import TimeRelate.FindWordFromSentence;
import libsvm.*;

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
    public List<Map<Integer,Integer>> poslist=new ArrayList<>();
    public List<Map<Integer,Integer>> neglist=new ArrayList<>();
    public ClassifyTexts()
    {

        this.poslist= (List<Map<Integer, Integer>>) MyFile.ReadObj("L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\postive.listmap");
        this.neglist= (List<Map<Integer, Integer>>) MyFile.ReadObj("L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\negative.listmap");
    }
    //我们的样本集构建完毕之后  就可以来进行建模了
    public void BuildModel()
    {

    }







    //我们先构建正样本的的例子
    public void BuildMyFeature()
    {
        //首先得到这个正负样本里面的数据
        String posdir=POSFILE.replaceAll("\\\\王思聪.txt","");
        String negdir=NEGFILE.replaceAll("\\\\王思聪.txt","");
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
        MyFile.WriteMap(poslist,"L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\postive.listmap");
        MyFile.WriteMap(neglist,"L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\negative.listmap");
    }
    public void caldev()
    {
        //定义训练集点a{10.0, 10.0} 和 点b{-10.0, -10.0}，对应lable为{1.0, -1.0}
        svm_node pa0 = new svm_node();
        pa0.index = 0;
        pa0.value = 10.0;
        svm_node pa1 = new svm_node();
        pa1.index = -1;
        pa1.value = 10.0;
        svm_node pb0 = new svm_node();
        pb0.index = 0;
        pb0.value = -10.0;
        svm_node pb1 = new svm_node();
        pb1.index = 0;
        pb1.value = -10.0;
        svm_node[] pa = {pa0, pa1}; //点a
        svm_node[] pb = {pb0, pb1}; //点b
        svm_node[][] datas = {pa, pb}; //训练集的向量表
        double[] lables = {1.0, -1.0}; //a,b 对应的lable
        //定义svm_problem对象
        svm_problem problem = new svm_problem();
        problem.l = 2; //向量个数
        problem.x = datas; //训练集向量表
        problem.y = lables; //对应的lable数组

        //定义svm_parameter对象
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 100;
        param.eps = 0.00001;
        param.C = 1;

        //训练SVM分类模型
        System.out.println(svm.svm_check_parameter(problem, param)); //如果参数没有问题，则svm.svm_check_parameter()函数返回null,否则返回error描述。
        svm_model model = svm.svm_train(problem, param); //svm.svm_train()训练出SVM分类模型

        //定义测试数据点c
        svm_node pc0 = new svm_node();
        pc0.index = 0;
        pc0.value = -0.1;
        svm_node pc1 = new svm_node();
        pc1.index = -1;
        pc1.value = 0.0;
        svm_node[] pc = {pc0, pc1};

        //预测测试数据的lable
        System.out.println(svm.svm_predict(model, pc));
    }

    public static void main(String[] args) {
        ClassifyTexts classifyTexts=new ClassifyTexts();
        classifyTexts.BuildMyFeature();
    }

}
