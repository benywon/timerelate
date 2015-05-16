package TimeRelate;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by benywon on 2015/4/16.
 */
public class TimeInterval implements Serializable {
   public Integer StartTime;
    public Integer EndTime;
    public Integer Interval;
    public String Dynasty;
    /**
     * 返回每一行的朝代信息
     * @param line
     * @return 结构体 但是是以数字表示时间的  而不是date类型
     * 东汉	25年-220年	洛阳	河南洛阳	光武帝刘秀
     */
    public TimeInterval(String line)
    {
        String[] info=line.split("\t");
        this.Dynasty=info[0];
        String[] times=info[1].split("-");
        Pattern p = Pattern.compile(".*\\d+年");
        try {
            Matcher m = p.matcher(times[0]);
            if (m.find() != true) {
                return;
            }
            times[0] = m.group().replaceAll("年|公元", "");
            if (times[0].indexOf("前") != -1) {
                times[0] = times[0].replaceAll("前", "-");
            }
            this.StartTime = Integer.parseInt(times[0]);
            m = p.matcher(times[1]);
            if (m.find() != true) {
                return;
            }
            times[1] = m.group().replaceAll("年|公元", "");
            if (times[1].indexOf("前") != -1) {
                times[1] = times[1].replaceAll("前", "-");
            }
            this.EndTime = Integer.parseInt(times[1]);
            this.Interval = this.EndTime - this.StartTime;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }


}
