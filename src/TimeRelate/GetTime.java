package TimeRelate;

import BaiduRelate.ItemMap;
import BaiduRelate.Queryer;
import Bases.BaseMethods;
import TextClassification.ClassifyTexts;

import java.util.*;

/**
 * Created by benywon on 2015/4/16.
 */
public class GetTime {
    public List<TimeInterval> Dlist = new ArrayList<>();//朝代和时间
    public static Set<String> ItemSet = new HashSet<>();//百科的条目
    public static Map<String,Integer> ItemMaps = new HashMap<>();//百科的条目
    public GetItemTime getItemTime = new GetItemTime();
    public Queryer query;
    public String Question;
    public List<Integer> QuestionTimeList = new ArrayList<>();
    public Map<String, List<Integer>> AnswersTimeList = new HashMap<>();
    public int ansertype = 0;//默认的时间问题类型是0  就是发生在什么时候
    //                  1  表示发生的先后顺序
    //                  2  表示最早
    //                  3  表示最晚
    //                  4   同一世纪  同一时期一类的 时序排列 但是问题中没有显性的时间表示
    //                  5   起止时间
    //                  6   同一世纪 同一时期 但是是和问题中的时间有关

    /**
     * 这个是一个从一句话中获取一段时间的程序
     * 法国大革命对欧洲和世界都有巨大影响，法国大革命的起止时间应该是(　　)
     A．1789年7月至1794年7月
     B．1792年8月至1794年7月
     C．1640年至1688年
     D．1793年6月至1794年7月
     * 注意只是能获取一个事件的时间 而两个事件的时间获取就需要另外处理
     * @param sentence
     * @return
     */


    /**
     * 构造函数 就是看看是不是初始化成功
     */
    public GetTime() {
        //首先获取我们的日期文件--朝代 时间
        Dlist = new DealDynasty().dynastylist;
        //然后获取百度百科条目字典文件
        ItemSet = ItemMap.readfrommap();
        int number=0;
        for(String item:ItemSet)
        {
            ItemMaps.put(item,number++);
        }
        query = new Queryer();//百度相关的  检索器什么的 因为都是静态方法 所以只用在初始化的时候初始化一下就行
        ClassifyTexts classifyTexts=new ClassifyTexts(2);
        System.out.println("gettime初始化成功");
    }

    public List<Integer> gettimefromstring(String str) {
        List<Integer> timelist = new ArrayList<>();
        Set<String> set =FindWordFromSentence.ForwardMaxMatch(str);//正向最大匹配
        //扔到百度数据里面 抽取时间数据
        //然后我们需要在这个里面找到明显的时间信息 比如1928年等等
        List<Integer> QuestionExplicitTime=GetItemTime.gettimeinterval(str);
        timelist.addAll(QuestionExplicitTime);
        boolean predy = false;
        List<Integer> preoneitem = new ArrayList<>();
        for (String term : set) {

            List<Integer> oneitem = new ArrayList<>(getItemTime.GetTermTimeList(term));
            if (oneitem.isEmpty())//如果是空我们就应该直接继续
            {
                continue;
            }
            boolean nowdy = getItemTime.HasDynasty;
            if (predy && nowdy)//前面一个和现在这个词都是朝代 那么我们就取他们之间的60年来当作这个item的区间 元末明初一类的
            {
                //时间就是 这两个之间的东西 我们应该将它们归位一类
                int pres=preoneitem.get(0);
                int nows=oneitem.get(0);
                timelist.removeAll(preoneitem);//先把上一个的删了
                int trans=0;
                if(pres<nows)//是上一个比较小
                {
                    trans= oneitem.get(0);
                }
                else
                {
                    trans = preoneitem.get(0);
                }

                oneitem.clear();
                for (int i = -30; i <= 30; i++) {
                    oneitem.add(trans + i);
                }
            }
            timelist.addAll(oneitem);
            preoneitem = oneitem;
            predy = nowdy;
        }
        if (timelist.isEmpty()) {
            return null;
        }
        DealTime dealTime = new DealTime();
        List<Integer> lists = dealTime.StripTimeList(timelist);//就是我们得到的结果 按时间从前到后
        return lists;
    }

    /**
     * 起止时间的判断
     * @param str
     * @return
     */
    public List<Integer> gettimefromstringtype5(String str) {
        List<Integer> timelist = new ArrayList<>();
        Set<String> set =FindWordFromSentence.ForwardMaxMatch(str);//正向最大匹配
        //扔到百度数据里面 抽取时间数据
        //然后我们需要在这个里面找到明显的时间信息 比如1928年等等
        List<Integer> QuestionExplicitTime=GetItemTime.gettimeinterval(str);
        timelist.addAll(QuestionExplicitTime);
        boolean predy = false;
        List<Integer> preoneitem = new ArrayList<>();
        for (String term : set) {

            List<Integer> oneitem = new ArrayList<>(getItemTime.GetTermTimeListSTARTT(term));
            if (oneitem.isEmpty())//如果是空我们就应该直接继续
            {
                continue;
            }
            boolean nowdy = getItemTime.HasDynasty;
            if (predy && nowdy)//前面一个和现在这个词都是朝代 那么我们就取他们之间的60年来当作这个item的区间 元末明初一类的
            {
                //时间就是 这两个之间的东西 我们应该将它们归位一类
                int pres=preoneitem.get(0);
                int nows=oneitem.get(0);
                timelist.removeAll(preoneitem);//先把上一个的删了
                int trans=0;
                if(pres<nows)//是上一个比较小
                {
                    trans= oneitem.get(0);
                }
                else
                {
                    trans = preoneitem.get(0);
                }

                oneitem.clear();
                for (int i = -30; i <= 30; i++) {
                    oneitem.add(trans + i);
                }
            }
            timelist.addAll(oneitem);
            preoneitem = oneitem;
            predy = nowdy;
        }
        if (timelist.isEmpty()) {
            return null;
        }
        DealTime dealTime = new DealTime();
        List<Integer> lists = dealTime.StripTimeList(timelist);//就是我们得到的结果 按时间从前到后
        return lists;
    }
    /**
     * 得到问题的时间区间
     *
     * @param question
     */
    public void putquestion(String question) {
        this.Question = question;
        AnalysisTimeQuestion();
        if(this.ansertype!=5)
        {
            this.QuestionTimeList = gettimefromstring(question);
        }
        else
        {
            this.QuestionTimeList=gettimefromstringtype5(question);
        }

    }

    /**
     * 将一个问题放到问题列表里面并且存储下来时间
     *
     * @param answer 问题
     */
    public void putanswer(String answer) {

        //我们根据问题的类型来分析时间的区间
        switch (this.ansertype) {
            case (4): {//同一世纪的问题 这种问题需要我们细心处理
                //注意是问题中没有时间区间  所以我们必须在答案里面找
                List<Integer> answerlist = getanswerType4(answer);
                this.AnswersTimeList.put(answer, answerlist);
            }
            break;
            case (5): {//起止时间 就是答案里面全是日期
                //注意是问题中没有时间区间  所以我们必须在答案里面找
                List<Integer> answerlist = getanswerType5(answer);
                this.AnswersTimeList.put(answer, answerlist);
            }
            break;
            case (6): {//同一朝代 问题中的肯定是有一个朝代或者时期的 我们要抓住这些东西
                List<Integer> answerlist = getanswerType6(answer);
                this.AnswersTimeList.put(answer, answerlist);
            }
            break;
            default: {
                List<Integer> answerlist = gettimefromstring(answer);
                this.AnswersTimeList.put(answer, answerlist);
            }
        }
    }


    /**
     * 答案包含不止一个时间区间 所以我们需要对答案进行分期处理
     *
     * @param answer
     * @return
     */
    private List<Integer> getanswerType4(String answer) {
        List<Integer> timelist = new ArrayList<>();
        Set<String> set = FindWordFromSentence.ForwardMaxMatch(answer);
        //扔到百度数据里面 抽取时间数据
        DealTime dealTime = new DealTime();
        for (String term : set) {
            List<Integer> oneitem = new ArrayList<>(getItemTime.GetTermTimeList(term));
            if(oneitem.size()!=0) {
                oneitem = dealTime.StripTimeList(oneitem);//就是我们得到的结果 按时间从前到后
                timelist.addAll(oneitem);

            }
            timelist.add(Integer.MAX_VALUE);//每一个实体之间用一个maxvalue聚合
        }
        if (timelist.isEmpty()) {
            return null;
        }
        return timelist;
    }

    /**
     * 答案包含的是纯时间区间的东西 我们需要对其进行处理
     * 注意由于答案直接包含具体时间了 我们就不用对其进行降噪处理
     *
     * @param answer 答案
     * @return 时间区间
     */
    private List<Integer> getanswerType5(String answer) {
        List<Integer> timelist = GetItemTime.gettimeinterval(answer);
        return timelist;
    }

    /**
     * 根据朝代时间来 匹配答案和问题的相似  首先我们必须获得问题的朝代
     * 所以我们要重新分析一下问题的时间朝代信息 以及答案的时间朝代信息
     * @param answer
     * @return
     */
    private List<Integer> getanswerType6(String answer)
    {
        List<Integer> timelist = new ArrayList<>();
        /*
        我们首先获得重新问题的时间朝代信息
         */
        Set<String> set =FindWordFromSentence.ForwardMaxMatch(this.Question);//正向最大匹配
        for (String term : set) {
            List<Integer> oneitem = new ArrayList<>(getItemTime.GetTermTimeList(term));
            if (oneitem.isEmpty())//如果是空我们就应该直接继续
            {
                continue;
            }
            if(getItemTime.HasDynasty)//问题中有朝代的信息
            {
                this.QuestionTimeList=oneitem;
            }
        }
        /**
         * 然后我们再来找答案的时间区间
         */
        set = FindWordFromSentence.ForwardMaxMatch(answer);
        //扔到百度数据里面 抽取时间数据
        DealTime dealTime = new DealTime();
        for (String term : set) {
            List<Integer> oneitem = new ArrayList<>(getItemTime.GetTermTimeList(term));
            if(oneitem.size()==0)
            {
                continue;
            }
//            oneitem = dealTime.StripTimeList(oneitem);//就是我们得到的结果 按时间从前到后
            timelist.addAll(oneitem);
        }
        if (timelist.isEmpty()) {
            return null;
        }
        return dealTime.StripTimeList(timelist);

    }

    /**
     * 通过问题来确定时间问题的类型
     */
    public void AnalysisTimeQuestion() {
        //如果含有时间先后的东西
        if (this.Question.contains("时间先后") || this.Question.contains("先后顺序")|| this.Question.contains("时序")) {
            this.ansertype = 1;
        } else if (this.Question.contains("最早")) {
            this.ansertype = 2;
        } else if (this.Question.contains("最晚")) {
            this.ansertype = 3;
        } else if (this.Question.contains("同一") || this.Question.contains("同属") ) {
            if (this.QuestionTimeList==null) {//是答案中有两个时间的那种问题
                this.ansertype = 4;
            } else {
                this.ansertype = 6;
            }
        } else if (this.Question.contains("起止时间")) {
            this.ansertype = 5;
        }
        System.out.println("问题类型为："+this.ansertype);
    }

    /**
     * 通过记录下来的答案和问题的时间来确定结果
     *
     * @return 最后的答案
     */
    public String GetAnswerByTime() {
        /**
         * 我们要通过问题来分析实践问题的类型 有问先后顺序的 有问最早 最晚的
         */
        float min = Float.MAX_VALUE;
        String finalanswer = null;
        Iterator i = AnswersTimeList.keySet().iterator();
        switch (this.ansertype) {
            case (1): {//按先后顺序
                Map<String, Float> map = new HashMap<>();
                while (i.hasNext()) {
                    String answer = i.next().toString();
                    List<Integer> list = AnswersTimeList.get(answer);
                    float value = BaseMethods.IntegerListMid(list);
                    map.put(answer, value);
                }
                //然后我们对这个进行排序
                map = BaseMethods.sortByValue(map);
                //将结果输出
                Iterator it = map.keySet().iterator();
                while (it.hasNext()) {
                    finalanswer += it.next().toString() + "---";
                }
            }
            break;
            case (2)://表示最早发生在什么时候
            {
                //我们应该从问题前1/3匹配答案
                List<Integer> questionin = new ArrayList<>();
                for (int j = 0; j <= QuestionTimeList.size() / 3; j++) {
                    questionin.add(QuestionTimeList.get(j));
                }
                while (i.hasNext()) {
                    String answer = i.next().toString();
                    List<Integer> list = AnswersTimeList.get(answer);
                    float score = DealTime.CalTimeRelation(questionin, list);
                    if (score < min) {
                        min = score;
                        finalanswer = answer;
                    }
                }
            }
            break;
            case (3)://表示最晚发生在什么时候
            {
                //我们应该从问题后1/3匹配答案
                List<Integer> questionin = new ArrayList<>();
                for (int j = QuestionTimeList.size() * 2 / 3; j < QuestionTimeList.size(); j++) {
                    questionin.add(QuestionTimeList.get(j));
                }
                while (i.hasNext()) {
                    String answer = i.next().toString();
                    List<Integer> list = AnswersTimeList.get(answer);
                    float score = DealTime.CalTimeRelation(questionin, list);
                    if (score < min) {
                        min = score;
                        finalanswer = answer;
                    }
                }
            }
            break;
            case (4): {
                //这个我们就要来判断了 因为我们的list是各个时间片段  所以需要我们对每个时间片段的结果处理一下
                while (i.hasNext()) {
                    String answer = i.next().toString();
                    List<Integer> list = AnswersTimeList.get(answer);
                    //因为是type4  所以我们有相应的差距
                    int len = list.size();
                    float ave = 0;
                    List<Integer> temptime = new ArrayList<>();
                    List<Float> time2 = new ArrayList<>();
                    for (int j = 0; j < len; j++) {
                        Integer number = list.get(j);
                        if (!number.equals(Integer.MAX_VALUE)) {
                            temptime.add(number);
                        } else//碰到最大的
                        {
                            float thisnumber=BaseMethods.IntegerListMid(temptime);
                            if(!(thisnumber==Float.MAX_VALUE))
                            {
                                time2.add(thisnumber);//我们获得的是每段的中位值 这样便于比较
                            }
                            temptime.clear();
                        }
                    }
                    //然后我们计算这个答案的分离度
                    float score = DealTime.CalTimeType4(time2);//得分是问题中几个实体的时间距离
                    if (score < min) {
                        min = score;
                        finalanswer = answer;
                    }
                }
            }
            break;
            case (5): {
                //对于起止时间 我们的策略就是问题的对应百科条目中  出现次数最多的 那个 然后和这个起止时间来比
                while (i.hasNext()) {
                    String answer = i.next().toString();
                    List<Integer> list = AnswersTimeList.get(answer);
                    int max=-1;

                    for(Integer time:list)
                    {
                        int number=0;
                        for(Integer qtime:QuestionTimeList)
                        {
                            if(qtime.equals(time))
                            {
                                number++;
                                if(number>=max)
                                {
                                    max=number;
                                    finalanswer = answer;
                                }
                            }
                        }
                    }

                }
            }
            break;
            default: {
                while (i.hasNext()) {
                    String answer = i.next().toString();
                    List<Integer> list = AnswersTimeList.get(answer);
                    float score = DealTime.CalTimeRelation(QuestionTimeList, list);
                    if (score < min) {
                        min = score;
                        finalanswer = answer;
                    }
                }
            }
            break;

        }

        dosomeclear();
        return finalanswer;
    }

    /**
     * 每次回答完一个问题 清空的操作
     */
    private void dosomeclear()
    {
        this.AnswersTimeList.clear();
        this.ansertype=0;
        if(this.QuestionTimeList!=null)
        {
            this.QuestionTimeList.clear();
        }
        this.Question=null;
    }
    public void PutThisQuestion(getquestionANDanwer getquestionandanwer)
    {
        String quest=getquestionandanwer.question;
        putquestion(quest);
        List<String> answers=getquestionandanwer.answers;
        for(int i=0;i<answers.size();i++)
        {
            putanswer(answers.get(i));
        }
        System.out.println(quest);
        for(String answer:answers)
        {
            System.out.print(answer + "----");
        }
        System.out.println(" ");
    }
    public static void main(String[] args) {
        GetTime getTime = new GetTime();
        getquestionANDanwer getquestionandanwer = new getquestionANDanwer();
        for(int i=0;i<getquestionandanwer.QA_Pair.size();i++) {
            getTime.PutThisQuestion(getquestionandanwer.outputaquestion(i));
            System.out.println("答案是：-----");
            String answer = getTime.GetAnswerByTime();
            System.out.println(answer.replaceAll("\r|\n",""));
            System.out.println("\r\n");
        }
    }
}
