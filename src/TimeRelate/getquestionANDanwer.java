package TimeRelate;

import Bases.MyFile;

import java.util.*;

/**
 * Created by benywon on 2015/4/20.
 */
public class getquestionANDanwer {
    public final static String questionfilepath="L:\\program\\cip\\SAT-HISTORY\\4月\\时间相关\\question.txt";
    public String question;
    public List<String> answers=new ArrayList<>();
    public List<Mylist> QA_Pair=new ArrayList<>();
    class Mylist{
        String question;
        List<String> answers=new ArrayList<>();
        public Mylist(String question,List<String> answers)
        {
            this.question=question;
            this.answers=answers;
        }
    }
    public getquestionANDanwer()
    {
        String content= MyFile.readfile(questionfilepath);
        String[] txt=content.split("\n");
        for(int i=0;i<(txt.length-1);i+=2)
        {
            String question=txt[i];
            String[] answer=txt[i+1].split("\t");
            List<String> answerlist=Arrays.asList(answer);
            Mylist mylist=new Mylist(question,answerlist);
            QA_Pair.add(mylist);
        }
    }
    public getquestionANDanwer outputaquestion(int i)
    {
        this.question=QA_Pair.get(i).question;
        this.answers=QA_Pair.get(i).answers;
        return this;
    }


}
