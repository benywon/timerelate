package TextClassification;

import Bases.BaseMethods;
import Bases.MyFile;
import TimeRelate.FindWordFromSentence;
import de.bwaldvogel.liblinear.*;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**我们构建的特征直接就是这个文档的bag-of words信息
 * 首先还是用字典分词
 * Created by benywon on 2015/5/27.
 */
public class ClassifyTexts {
    public final String POSFILE=BuildTrainTxt.TestHistoryPos;
    public final String NEGFILE=BuildTrainTxt.TestHistoryNeg;
//    public final String SVMMODELPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\classiftextsvmmodel-liblinear.svmmodel";

    public final String SVMMODELPATH2="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\classiftextsvmmodel-liblinear.logisticmodel";
    public static int SVMTYPE=2;//默认是libsvm
//    public final String TRAINPOSLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\postive.listmap";
//    public final String TRAINNEGLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\negative.listmap";
//    public final String TESTPOSLISTPATH="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test\\postive.listmap";
//
    public static  svm_model svmmodel; //该方法将svm_model保存到文件中
    public static  Model libmodel; //该方法将svm_model保存到文件中
    public static int POSPOINTNUM=0;
//    public static int POSPOINTNUM=4763;
    public Map<Map<Integer,Integer>,List<Integer>> poslist=new HashMap<>();
    public List<Map<Integer,Integer>> neglist=new ArrayList<>();
    public List<Map<Integer,Integer>> testlist=new ArrayList<>();
    public static double CC=2;
    public static double est=0.5;


    public final String TRAINPOSLISTPATH="postive.listmap";
    public final String TRAINNEGLISTPATH="negative.listmap";
    public  String SVMMODELPATH="classiftextsvmmodel-liblinear.model";
    public final String TESTPOSLISTPATH="testpostive.listmap";
    public final String TESTNEGLISTPATH="testnegative.listmap";
    public String RESULTPOS="./result/resultpos&&.list";
    public String RESULTNEG="./result/resultneg&&.list";
    public String RESULTTXT="./result/result&&.txt";


    public ClassifyTexts(int type)
    {

//        GetTime getTime=new GetTime();
        SVMTYPE=type;
//        if(type==1)
//        {
//            try {
//                svmmodel=svm.svm_load_model(SVMMODELPATH); //该方法将svm_model保存到文件中
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        else if(type==2)
//        {
//            File file=new File(SVMMODELPATH2);
//            try {
//                libmodel=Model.load(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        this.poslist= (Map<Map<Integer, Integer>, List<Integer>>) MyFile.ReadObj(TRAINPOSLISTPATH);
        this.neglist= (List<Map<Integer, Integer>>) MyFile.ReadObj(TRAINNEGLISTPATH);
        int num =0;
        for (Map.Entry<Map<Integer,Integer>,List<Integer>> entry:poslist.entrySet())
        {
            List<Integer> list=entry.getValue();
            num+=list.size();
        }
        POSPOINTNUM=num;
        String txt=MyFile.readfile("1.txt");
        num=Integer.parseInt(txt);
        SVMMODELPATH+=num+"";
//        RESULTNEG=RESULTNEG.replaceAll("&&",num+"");
//        RESULTPOS=RESULTPOS.replaceAll("&&",num+"");
//        RESULTTXT=RESULTTXT.replaceAll("&&",num+"");
        num++;
        MyFile.Write2File(num+"","1.txt",false);


        System.out.println("历史文本分类器初始化成功");
    }
    //我们的样本集构建完毕之后  就可以来进行建模了

    /**
     * 根据我们的类型来构建模型
     * @param type 1是libsvm包 2是liblinear包
     */
    public void BuildModel(int type)//type是我们的类型 是liblinear还是libsvm
    {
//        String trainfile="L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\train.txt";
//
//        System.out.println("读取训练集完毕");
//        if(type==1) {
//            SVMTYPE=1;
//            //定义训练集点a{10.0, 10.0} 和 点b{-10.0, -10.0}，对应lable为{1.0, -1.0}
//            svm_node[][] traindata = new svm_node[poslist.size() + neglist.size()][];
//            double[] label = new double[poslist.size() + neglist.size()];
//            int i1 = -1;
//            for (Map<Integer, Integer> map : poslist) {
//                svm_node[] nodes = new svm_node[map.size()];
//                int i = -1;
//                String str = "";
//                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
//                    svm_node node = new svm_node();
//                    node.index = entry.getKey();
//                    node.value = entry.getValue();
//                    str += node.index + ":" + node.value + " ";
//                    nodes[++i] = node;
//                }
//                traindata[++i1] = nodes;
//                MyFile.Write2File(1 + " " + str + "\n", trainfile, true);
//                label[i1] = 1;
//            }
//            for (Map<Integer, Integer> map : neglist) {
//                svm_node[] nodes = new svm_node[map.size()];
//                int i = -1;
//                String str = "";
//                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
//                    svm_node node = new svm_node();
//                    node.index = entry.getKey();
//                    node.value = entry.getValue();
//                    nodes[++i] = node;
//                    str += node.index + ":" + node.value + " ";
//                }
//                traindata[++i1] = nodes;
//                MyFile.Write2File(-1 + " " + str + "\n", trainfile, true);
//                label[i1] = -1;
//            }
//
//            //定义svm_problem对象
//            svm_problem problem = new svm_problem();
//            problem.l = poslist.size() + neglist.size(); //向量个数
//            problem.x = traindata; //训练集向量表
//            problem.y = label; //对应的lable数组
//
//            //定义svm_parameter对象
//            svm_parameter param = new svm_parameter();
//            param.svm_type = svm_parameter.C_SVC;
//            param.kernel_type = svm_parameter.LINEAR;
//            param.cache_size = 100;
//            param.C = 10;
////        param.nu=5;
//            param.eps = 0.00001;
//
////        svm.svm_cross_validation(problem,param,5,label);
//
//            //训练SVM分类模型
//            System.out.println(svm.svm_check_parameter(problem, param)); //如果参数没有问题，则svm.svm_check_parameter()函数返回null,否则返回error描述。
//            svm_model model = svm.svm_train(problem, param); //svm.svm_train()训练出SVM分类模型
//            try {
//                svm.svm_save_model(SVMMODELPATH, model); //该方法将svm_model保存到文件中
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        else if(type==2)
//        {
            //是liblinear
            SVMTYPE=2;
            Feature[][] featureMatrix = new Feature[POSPOINTNUM+neglist.size()][];
            double[] label = new double[POSPOINTNUM+neglist.size()];
            int j=-1;

            for (Map.Entry<Map<Integer,Integer>,List<Integer>> entry:poslist.entrySet()) {
                Map<Integer,Integer> map=entry.getKey();
                List<Integer> list=entry.getValue();
                map=(Map)BaseMethods.sortMapByKey(map);
                Feature[] nodes = new FeatureNode[map.size()];
                int i = -1;
                for (Map.Entry<Integer, Integer> entrys : map.entrySet()) {
                    FeatureNode node =new FeatureNode(entrys.getKey(),entrys.getValue());
                    nodes[++i]=node;
                }
                for(int lists:list)
                {
                    featureMatrix[++j] = nodes;
                    label[j] = lists;
                }
            }
            for (Map<Integer, Integer> map : neglist)
            {
                Feature[] nodes = new FeatureNode[map.size()];
                int i = -1;
                //我们要按升序对特征进行排列
                map=(Map)BaseMethods.sortMapByKey(map);

                    for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                        FeatureNode node = new FeatureNode(entry.getKey(), entry.getValue());
                        nodes[++i] = node;
                    }

                featureMatrix[++j]=nodes;
                label[j]=50;
            }
            //开始建模了
            Problem problem = new Problem();
            problem.l =POSPOINTNUM+neglist.size() ; // number of training examples：训练样本数
            problem.n=FindWordFromSentence.ItemSet.size()==0?4988815:FindWordFromSentence.ItemSet.size();
            problem.x = featureMatrix; // feature nodes：特征数据
            problem.y = label; // target values：类别
            SolverType solver = SolverType.L2R_LR; // -s 0
            double C = 1.0;    // cost of constraints violation
            double eps = 0.01; // stopping criteria
            Parameter parameter = new Parameter(solver, C, eps);
            Model model = Linear.train(problem, parameter);
            File modelFile = new File(SVMMODELPATH);
            try {
                model.save(modelFile);
            } catch (IOException e) {
                e.printStackTrace();
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
//    MyFile.DeleteFile(outpath);
    List<Map<Integer,Integer>> testlistmap=BuildMyFeature(dir);
//    MyFile.WriteMap(testlistmap,TESTPOSLISTPATH);
//    List<Map<Integer,Integer>> testlistmap2=BuildMyFeature(dir.replaceAll("pos","neg"));
//    MyFile.WriteMap(testlistmap2,TESTNEGLISTPATH);
    List<Map<Integer,Integer>> testlistmappos= (List<Map<Integer, Integer>>) MyFile.ReadObj(TESTPOSLISTPATH);
    List<Map<Integer,Integer>> testlistmapneg= (List<Map<Integer, Integer>>) MyFile.ReadObj(TESTNEGLISTPATH);
    System.out.println("读取测试数据成功");
    if(SVMTYPE==1)
    {
    try {
        svm_model model=svm.svm_load_model(SVMMODELPATH); //该方法将svm_model保存到文件中


    for(Map<Integer,Integer> map:testlistmappos)
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
        List<double[]> list=new ArrayList<>();
        File modelFile = new File(SVMMODELPATH);
        Model model = null;
        try {
            model = Model.load(modelFile);
        }
            catch (IOException e) {
                e.printStackTrace();
            }
            for (Map<Integer, Integer> map : testlistmappos) {
                map=(Map)BaseMethods.sortMapByKey(map);
                Feature[] nodes = new FeatureNode[map.size()];
                int i = -1;
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    FeatureNode node =new FeatureNode(entry.getKey(),entry.getValue());
                    nodes[++i]=node;
                }
                double prediction = Linear.predict(model, nodes);
//                System.out.println(prediction+" ");
                MyFile.Write2File(prediction+"\n",RESULTTXT,true);
                double[] cc = new double[101];
                Linear.predictProbability(model,nodes,cc);
                list.add(cc);
                result.add(prediction);
        }
        int right=0;
        MyFile.WriteMap(list,RESULTPOS);
        for(double re:result)
        {
            MyFile.Write2File(re + "      ", outpath, true);
            if(re<=99.9)
            {
                right++;
            }
        }
        System.out.println("正例为："+right);
        list.clear();
        result.clear();
        for (Map<Integer, Integer> map : testlistmapneg) {
            map=(Map)BaseMethods.sortMapByKey(map);
            Feature[] nodes = new FeatureNode[map.size()];
            int i = -1;
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                FeatureNode node =new FeatureNode(entry.getKey(),entry.getValue());
                nodes[++i]=node;
            }
            double prediction = Linear.predict(model, nodes);
//            System.out.println(prediction+" ");
            MyFile.Write2File(prediction+"\n",RESULTTXT,true);
            double[] cc = new double[101];
            Linear.predictProbability(model,nodes,cc);
            list.add(cc);
            result.add(prediction);
        }
        right=0;
        MyFile.WriteMap(list,RESULTNEG);
        for(double re:result)
        {
            MyFile.Write2File(re + "      ", outpath, true);
            if(re<=99.9)
            {
                right++;
            }
        }
        System.out.println("正例为："+right);
        System.out.println(SVMMODELPATH);
//        MyFile.Write2File("CC:"+CC+"est"+est+"---\t---"+right+"\n","L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\result.txt",true);

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
        Map<Map<Integer,Integer>,List<Integer>> poslist=new HashMap<>();
        Map<String,Set<String>> indexmap=MyFile.ReadMap(BuildTrainTxt.TagIndexFile);
        List<Map<Integer,Integer>> neglist=new ArrayList<>();
        List<String> tagsindexf= (List<String>) MyFile.ReadObj(BuildTrainTxt.TagIndexFileMost100);
        for(String file:posfiles)
        {
            String content=MyFile.readfile(file);
            System.out.println(file);
            //对这一个文章的特征进行提取
            //首先得到这个文章所有的词项
//            file=file.replaceAll("test\\\\Test-","");
            Set<String> list=indexmap.get(file);
            List<Integer> tagindex=new ArrayList<>();
            for(String str:list)
            {
                int cc=tagsindexf.indexOf(str);
                if(cc>=0)
                {
                    tagindex.add(cc);
                }
            }
            Map<Integer,Integer> map= FindWordFromSentence.ForwardMaxMatchList(content);
            POSPOINTNUM+=tagindex.size();
            poslist.put(map, tagindex);
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
        System.out.println("正例的个数"+POSPOINTNUM+"");
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
        System.out.println("开始");
        ClassifyTexts classifyTexts=new ClassifyTexts(2);
//        classifyTexts.BuildMyFeature(classifyTexts.POSFILE,classifyTexts.NEGFILE,classifyTexts.TRAINPOSLISTPATH,classifyTexts.TRAINNEGLISTPATH);
        classifyTexts.BuildModel(2);
//        classifyTexts.testresult("L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\temp\\pos", "L:\\program\\cip\\SAT-HISTORY\\5月\\历史标签\\test\\posresult.txt");


    }

}
