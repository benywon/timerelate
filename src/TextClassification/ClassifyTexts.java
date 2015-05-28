package TextClassification;

import Bases.BaseMethods;
import Bases.MyFile;
import TimeRelate.FindWordFromSentence;
import de.bwaldvogel.liblinear.*;
import libsvm.*;

import java.io.File;
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
    public final String SVMMODELPATH2="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\classiftextsvmmodel-liblinear.svmmodel";
    public static int SVMTYPE=2;//默认是libsvm
    public final String TRAINPOSLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\postive.listmap";
    public final String TRAINNEGLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\negative.listmap";
    public final String TESTPOSLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test\\postive.listmap";
    public final String TESTNEGLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test\\negative.listmap";
    public static  svm_model svmmodel; //该方法将svm_model保存到文件中
    public static  Model libmodel; //该方法将svm_model保存到文件中
    public List<Map<Integer,Integer>> poslist=new ArrayList<>();
    public List<Map<Integer,Integer>> neglist=new ArrayList<>();
    public List<Map<Integer,Integer>> testlist=new ArrayList<>();
    public ClassifyTexts(int type)
    {
        SVMTYPE=type;
        if(type==1)
        {
            try {
                svmmodel=svm.svm_load_model(SVMMODELPATH); //该方法将svm_model保存到文件中
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(type==2)
        {
            File file=new File(SVMMODELPATH2);
            try {
                libmodel=Model.load(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("历史文本分类器初始化成功");
    }
    //我们的样本集构建完毕之后  就可以来进行建模了

    /**
     * 根据我们的类型来构建模型
     * @param type 1是libsvm包 2是liblinear包
     */
    public void BuildModel(int type)//type是我们的类型 是liblinear还是libsvm
    {
        String trainfile="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\train.txt";
        this.poslist= (List<Map<Integer, Integer>>) MyFile.ReadObj(TRAINPOSLISTPATH);
        this.neglist= (List<Map<Integer, Integer>>) MyFile.ReadObj(TRAINNEGLISTPATH);
        System.out.println("读取训练集完毕");
        if(type==1) {
            SVMTYPE=1;
            //定义训练集点a{10.0, 10.0} 和 点b{-10.0, -10.0}，对应lable为{1.0, -1.0}
            svm_node[][] traindata = new svm_node[poslist.size() + neglist.size()][];
            double[] label = new double[poslist.size() + neglist.size()];
            int i1 = -1;
            for (Map<Integer, Integer> map : poslist) {
                svm_node[] nodes = new svm_node[map.size()];
                int i = -1;
                String str = "";
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    svm_node node = new svm_node();
                    node.index = entry.getKey();
                    node.value = entry.getValue();
                    str += node.index + ":" + node.value + " ";
                    nodes[++i] = node;
                }
                traindata[++i1] = nodes;
                MyFile.Write2File(1 + " " + str + "\n", trainfile, true);
                label[i1] = 1;
            }
            for (Map<Integer, Integer> map : neglist) {
                svm_node[] nodes = new svm_node[map.size()];
                int i = -1;
                String str = "";
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    svm_node node = new svm_node();
                    node.index = entry.getKey();
                    node.value = entry.getValue();
                    nodes[++i] = node;
                    str += node.index + ":" + node.value + " ";
                }
                traindata[++i1] = nodes;
                MyFile.Write2File(-1 + " " + str + "\n", trainfile, true);
                label[i1] = -1;
            }

            //定义svm_problem对象
            svm_problem problem = new svm_problem();
            problem.l = poslist.size() + neglist.size(); //向量个数
            problem.x = traindata; //训练集向量表
            problem.y = label; //对应的lable数组

            //定义svm_parameter对象
            svm_parameter param = new svm_parameter();
            param.svm_type = svm_parameter.C_SVC;
            param.kernel_type = svm_parameter.LINEAR;
            param.cache_size = 100;
            param.C = 10;
//        param.nu=5;
            param.eps = 0.00001;

//        svm.svm_cross_validation(problem,param,5,label);

            //训练SVM分类模型
            System.out.println(svm.svm_check_parameter(problem, param)); //如果参数没有问题，则svm.svm_check_parameter()函数返回null,否则返回error描述。
            svm_model model = svm.svm_train(problem, param); //svm.svm_train()训练出SVM分类模型
            try {
                svm.svm_save_model(SVMMODELPATH, model); //该方法将svm_model保存到文件中
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(type==2)
        {
            //是liblinear
            SVMTYPE=2;
            Feature[][] featureMatrix = new Feature[poslist.size()+neglist.size()][];
            double[] label = new double[poslist.size()+neglist.size()];
            int j=-1;

            for (Map<Integer, Integer> map : poslist) {
                map=(Map)BaseMethods.sortMapByKey(map);
                Feature[] nodes = new FeatureNode[map.size()];
                int i = -1;
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    FeatureNode node =new FeatureNode(entry.getKey(),entry.getValue());
                    nodes[++i]=node;
                }
                featureMatrix[++j]=nodes;
                label[j]=1;
            }
            for (Map<Integer, Integer> map : neglist)
            {
                Feature[] nodes = new FeatureNode[map.size()];
                int i = -1;
                //我们要按升序对特征进行排列
                map=(Map)BaseMethods.sortMapByKey(map);
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    FeatureNode node =new FeatureNode(entry.getKey(),entry.getValue());
                    nodes[++i]=node;
                }
                featureMatrix[++j]=nodes;
                label[j]=-1;
            }
            //开始建模了
            Problem problem = new Problem();
            problem.l =poslist.size()+neglist.size() ; // number of training examples：训练样本数
            problem.n=FindWordFromSentence.ItemSet.size();
            problem.x = featureMatrix; // feature nodes：特征数据
            problem.y = label; // target values：类别
            SolverType solver = SolverType.L2R_L2LOSS_SVC; // -s 0
            double C = 1.5;    // cost of constraints violation
            double eps = 0.01; // stopping criteria

            Parameter parameter = new Parameter(solver, C, eps);
            Model model = Linear.train(problem, parameter);
            File modelFile = new File(SVMMODELPATH2);
            try {
                model.save(modelFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试我们的数据
     * @param dir 测试文本的目录
     * @param outpath 输出的结果
     * @return
     */
public List<Double> testresult(String dir,String outpath)
{

    List<Double> result=new ArrayList<>();
    MyFile.DeleteFile(outpath);
    List<Map<Integer,Integer>> testlistmap=BuildMyFeature(dir);
    if(SVMTYPE==1)
    {
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
    }
    else if(SVMTYPE==2)
    {
        File modelFile = new File(SVMMODELPATH2);
        try {
            Model model=Model.load(modelFile);
            for (Map<Integer, Integer> map : testlistmap) {
                map=(Map)BaseMethods.sortMapByKey(map);
                Feature[] nodes = new FeatureNode[map.size()];
                int i = -1;
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    FeatureNode node =new FeatureNode(entry.getKey(),entry.getValue());
                    nodes[++i]=node;
                }
                double prediction = Linear.predict(model, nodes);
                result.add(prediction);
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

    }
    return result;
}

    //我们先构建正样本的的例子

    /**
     * 构建我们的特征矩阵
     * @param posdir
     * @param negdir
     * @param OutPosList
     * @param OutNegList
     */
    public void BuildMyFeature(String posdir,String negdir,String OutPosList,String OutNegList)
    {
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

    /**
     * 通过一个文件夹下面的文件来对输入的一串字符串产生一个特征向量的列表
     * @param dir 文件夹目录  里面有很多子文件
     * @return 最后的特征矩阵
     */
    public List<Map<Integer, Integer>> BuildMyFeature(String dir)
    {

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
    /**
     * 对外调用的接口 看是不是历史事件
     * @param content 输入的文本内容
     * @return
     */
    public static boolean IsHistory(String content)
    {
        Map<Integer,Integer> map= FindWordFromSentence.ForwardMaxMatchList(content);
        map=(Map)BaseMethods.sortMapByKey(map);
        boolean ishistory=false;
        if(SVMTYPE==1)
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
            double result=svm.svm_predict(svmmodel, nodes);
            if(result>0)
            {
                ishistory=true;
            }
            else
            {
                ishistory=false;
            }

        }
        else if(SVMTYPE==2)
        {
            Feature[] nodes = new FeatureNode[map.size()];
            int i = -1;
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                FeatureNode node =new FeatureNode(entry.getKey(),entry.getValue());
                nodes[++i]=node;
            }
            double result = Linear.predict(libmodel, nodes);
            if(result>0)
            {
                ishistory=true;
            }
            else
            {
                ishistory=false;
            }
        }
        return ishistory;
    }
    public static void main(String[] args) {
        ClassifyTexts classifyTexts=new ClassifyTexts(2);
//        classifyTexts.BuildMyFeature(classifyTexts.POSFILE,classifyTexts.NEGFILE,classifyTexts.TRAINPOSLISTPATH,classifyTexts.TRAINNEGLISTPATH);
//        classifyTexts.BuildModel(2);
        classifyTexts.testresult("L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\temp","L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test\\posresult.txt");

    }

}
