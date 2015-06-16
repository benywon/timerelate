package TextClassification;

import Bases.BaseMethods;
import TimeRelate.FindWordFromSentence;
import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;

import java.io.File;
import java.io.IOException;
import java.util.Map;


/**
 * Created by benywon on 2015/6/16.
 */
public class TagRelate {
    public static final String LIBLINEAR_MODEL_PATH="classiftextsvmmodel-liblinear.model";
    public static Model model = null;
    public boolean IsHistory;
    static {
        try {
            File modelFile = new File(LIBLINEAR_MODEL_PATH);
            model = Model.load(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            double prediction = Linear.predict(model, nodes);
        if(prediction<100)
        {
            IsHistory=true;
        }
        else
        {
            IsHistory=false;
        }
        double[] cc = new double[101];
        Linear.predictProbability(model,nodes,cc);
        return cc;
    }
}
