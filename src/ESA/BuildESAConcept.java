package ESA;

import Bases.BaseMethods;
import Bases.MyFile;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 建立ESA我们分为以下几步：
 * 首先是将索引中的词项抽出来 去重
 * 然后将其放进lemma。map里面
 * 以后可以从这个维度来找
 * ------------------------------
 * 然后对于我们的文章 我们遍历一遍
 * 对其中的词项统计一下 然后算出tfidf  放到.map文件里面
 * Created by benywon on 2015/4/8.
 */
public class BuildESAConcept {
    public static final String LemmaMapPath = "L:/program/cip/SAT-HISTORY/3月/时间地点相关/lemma.map";
    public String IndexPath = "L:/program/cip/SAT-HISTORY/baiduindex";



    /**
     * 这个程序是为了读取我们的字典文件 然后建立所谓的ESA
     * 我们遍历每一个文档 从i到最大值
     * 然后对于每一个文档 首先取出它的lemmatitle 也就是概念
     * 然后对每个概念建模
     *
     * @param indexPath
     * @throws IOException
     * @throws ParseException
     */
    public void Cmatrix(String indexPath) throws IOException, ParseException {
//        Map Dicmap = MyFile.ReadMap(LemmaMapPath);
        Map<Integer, Map<Integer, Float>> MAP = new HashMap<Integer, Map<Integer, Float>>();
//        Set ConceptMap= MyFile.readFromDict("J:\\BAIDU\\BAIDU\\words-my.dic");
//        Set<String> set = Dicmap.keySet();//我们不需要词频 只需要把这个keyset找出俩就行
//        List<String> Dic = new ArrayList<String>(set);
        Directory indexDir = FSDirectory.open(new File(indexPath));
        IndexReader ir = DirectoryReader.open(indexDir);
        int docnum = ir.numDocs();
        int j = 0;
        for (int i = 1; i < docnum; i++) {
            Terms terms = ir.getTermVector(i, "content");
            if (terms == null) {
                continue;
            }
            TermsEnum termsEnums = terms.iterator(null);
            //
            Map<Integer, Float> map = new HashMap<Integer, Float>();
            DefaultSimilarity simi = new DefaultSimilarity();
            BytesRef byteRef = null;
            while ((byteRef = termsEnums.next()) != null) {
                String term = new String(byteRef.bytes, byteRef.offset, byteRef.length);
                //首先在字典里面
//
//                int termId=Dic.indexOf(term);//之所以去掉是因为太耗时间 所以最后用的hash
//                if(termId<0)
//                {
//                    continue;
//                }
                int termId = term.hashCode();
                float tf = termsEnums.totalTermFreq();
                float idf = simi.idf(termsEnums.docFreq(), docnum);
                float score = tf * idf;
                map.put(termId, score);
                //开始循环的往这个概念里面填写东西了
            }
            MAP.put(i, map);
            j = j + 1;
            if ((j - 10000) % 10000 == 0) {
                MyFile.WriteMap(MAP, "L:/program/cip/SAT-HISTORY/3月/时间地点相关/ESA/" + (int) (j / 10000) + ".map");
                MAP.clear();
            }
        }
        MyFile.WriteMap(MAP, "L:/program/cip/SAT-HISTORY/3月/时间地点相关/ESA/last.map");
        System.out.println("结束");
    }

    public void getfreq(String indexPath) throws IOException {
        Directory indexDir = FSDirectory.open(new File(indexPath));
        IndexReader reader = DirectoryReader.open(indexDir);
        Fields fields = MultiFields.getFields(reader);
        Map<String, Integer> fr = new HashMap<String, Integer>();
        Terms terms = fields.terms("content");
        TermsEnum termsEnum = terms.iterator(null);
        long count = 0;
        BytesRef cc = termsEnum.next();
        while (cc != null) {
            long freq = termsEnum.totalTermFreq();
            String content = cc.utf8ToString().replace("\t", "").replace("\n", "");
            fr.put(content, (int) freq);
            count++;
            cc = termsEnum.next();
        }
        System.out.println(count);//总共的百科content的词项数目
        fr = gethannum(fr);
        fr = BaseMethods.sortByValue(fr);

//        WriteMap2TxtFile(fr);可选项 将这个map写进txt文件可以看的
        //然后在这里我们得到了最后的确定性的词项集合后 开始对每个词项建立索引
        MyFile.WriteMap(fr, LemmaMapPath);
        System.out.println("构建字典结束");

    }

    /*
    这个是为了得到不含英文的词项数目
     */
    public Map gethannum(Map<String, Integer> map) {
        Set<String> keySet = map.keySet();
        Map<String, Integer> maps = new HashMap<String, Integer>();
        long num = 0;
        Iterator<String> iter = keySet.iterator();
        Pattern pattern = Pattern.compile(".*[0-9 a-zA-Z]+.*");
        while (iter.hasNext()) {
            String key = iter.next();
            Matcher matcher = pattern.matcher(key);
            boolean b = matcher.matches();
            //当条件满足时，将返回true，否则返回false
            if (!b) {
                maps.put(key, map.get(key));
            }
            //
        }
        return maps;
    }

    /*
    同上面一样的实现
     */
    public Set<String> gethannum(Set<String> set) {
        Set<String> aSet = new HashSet<String>();
        long num = 0;
        Iterator<String> iter = set.iterator();
        Pattern pattern = Pattern.compile(".*[0-9 a-zA-Z]+.*");
        while (iter.hasNext()) {
            String key = iter.next();
            Matcher matcher = pattern.matcher(key);
            boolean b = matcher.matches();
            //当条件满足时，将返回true，否则返回false
            if (!b) {
                aSet.add(key);
            }
            //
        }
        return aSet;
    }

    /*
    读取百度字典并且将其中包含英文的去掉的方法
     */
    public void editdic(String infile, String outfile) throws Exception {
        File file = new File(infile);
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File files = new File(outfile);
        FileWriter fw = null;
        fw = new FileWriter(files);
        writer = new BufferedWriter(fw);
        reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        Pattern pattern = Pattern.compile(".*[0-9 a-zA-Z]+.*");
        while ((tempString = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(tempString);
            boolean b = matcher.matches();
            //当条件满足时，将返回true，否则返回false
            if (!b) {
                writer.write(tempString);
                writer.newLine();
            }

        }
        reader.close();
        writer.close();
    }

    public void WriteMap2TxtFile(Map fr) {
        File file = new File("L:/program/cip/SAT-HISTORY/3月/时间地点相关/content_lemma-total.txt");
        FileWriter fw = null;
        Set<String> keySet = fr.keySet();
        Iterator<String> iter = keySet.iterator();
        BufferedWriter writer = null;
        try {
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            while (iter.hasNext()) {
                String key = iter.next();
                String num = fr.get(key) + "";
                writer.write(key + ":" + num);
                writer.newLine();//换行
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void BuildESAWord(String indexpath) throws IOException, ParseException {
        getfreq(IndexPath);
        Cmatrix(IndexPath);
    }
}