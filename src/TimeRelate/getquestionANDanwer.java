package TimeRelate;

import Bases.MyFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by benywon on 2015/4/20.
 */
public class getquestionANDanwer {
    public final static String questionfilepath="L:\\program\\cip\\SAT-HISTORY\\4月\\时间相关\\question.txt";
    public String question;
    public List<String> answers=new ArrayList<>();
    public getquestionANDanwer()
    {
        String content= MyFile.readfile(questionfilepath);
        String[] txt=content.split("\n");
        this.question=txt[0];
        String[] answers=txt[1].split("\t");
        this.answers= Arrays.asList(answers);
    }

}
