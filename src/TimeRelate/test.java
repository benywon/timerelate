package TimeRelate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by benywon on 2015/4/20.
 */
public class test {
    public static  final String dateregex="公元前(\\d{1,4})年|(\\d{1,4})年[^前以之代]|年[\\(（](\\d{1,4})[\\)）]|(\\d{1,4})年$";

    public static void main(String[] args) {
        Pattern p = Pattern.compile(dateregex);
        Matcher m = p.matcher("1640年至1688年");
        while(m.find())
        {
            String out=m.group();
            System.out.println(out);
        }

    }
}
