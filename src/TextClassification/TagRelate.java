package TextClassification;

import BaiduRelate.ItemMap;
import Bases.BaseMethods;
import Bases.MyFile;
import TimeRelate.FindWordFromSentence;
import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by benywon on 2015/6/16.
 */
public class TagRelate {
    public static final String LIBLINEAR_MODEL_PATH=TagRelate.class.getResource("/dependencyfiles/classiftextsvmmodel-liblinear.model").toString();
    public static Model model = null;
    public static Set<String> ItemSet = new HashSet<>();//百科的条目
    public static Map<String,Integer> ItemMaps = new HashMap<>();//百科的条目
    public double tagthis;
    public boolean IsHistory;
    private static String ItemMapFile=TagRelate.class.getResource("/dependencyfiles/ItemList.set").toString();
    public double[] predicts;
    static {
        try {
            File modelFile = new File(LIBLINEAR_MODEL_PATH);
            model = Model.load(modelFile);
            System.out.println("TAG分类器读取成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * 初始化字典文件
         */
        ItemSet = ItemMap.readfrommap(ItemMapFile);
        int number=0;
        for(String item:ItemSet)
        {
            ItemMaps.put(item,number++);
        }
        System.out.println("字典初始化成功");
    }
    public double[] GetTagRelate(String txt)
    {

            Map<Integer,Integer> map= FindWordFromSentence.ForwardMaxMatchList(txt);
            map=(Map) BaseMethods.sortMapByKey(map);
            Feature[] nodes = new FeatureNode[map.size()];
            int i=-1;
            for(Map.Entry<Integer,Integer> entry:map.entrySet())
            {
                FeatureNode node =new FeatureNode(entry.getKey(),entry.getValue());
                nodes[++i]=node;
            }
            this.tagthis = Linear.predict(model, nodes);
        if(tagthis<100)
        {
            IsHistory=true;
        }
        else
        {
            IsHistory=false;
        }
        double[] cc = new double[101];
        Linear.predictProbability(model,nodes,cc);
        this.predicts=cc;
        return cc;
    }
    public void write2file(String file)
    {
        String txt="";
        txt=this.IsHistory?"历史":"非历史";
        txt+="\t"+this.tagthis+"\n";
        for(int i=0;i<this.predicts.length;i++)
        {
            txt+=this.predicts[i]+"\n";
        }
        MyFile.Write2File(txt,file,false);
    }
    public static void main(String[] args) {
        String file=args[0];
        String outfile=args[1];
        TagRelate tagRelate=new TagRelate();
        String txt=MyFile.readfile(file);
        tagRelate.GetTagRelate(txt);
        tagRelate.write2file(outfile);
    }
}
