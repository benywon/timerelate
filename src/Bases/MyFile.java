package Bases;

import java.io.*;
import java.util.*;

/**
 * Created by benywon on 2015/4/6.
 */
public class MyFile {
    public static String readfile(String filepath)
    {
        String str = null;
        File file=new File(filepath);
        Long filelength = file.length();     //获取文件长度
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(filecontent);//返回文件内容,默认编码
    }

    /**
     * 读取文件效率最快的方法就是一次全读进来，很多人用readline()之类的方法，可能需要反复访问文件，
     * 而且每次readline()都会调用编码转换，
     * 降低了速度，所以，在已知编码的情况下，按字节流方式先将文件都读入内存，再一次性编码转换是最快的方式
     * @param filepath
     * @param charset
     * @return
     */
    public static String readfile(String filepath,String charset)
    {
        File file = new File(filepath);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取一个文件夹下面所有文件
     * @param filelist
     * @param filePath
     */
    public static void getFilesFromDirectory(List<String>filelist,String filePath){
        File root = new File(filePath);
        File[] files = root.listFiles();
        for(File file:files){
            if(file.isDirectory()){
                getFilesFromDirectory(filelist,file.getAbsolutePath());

            }else{
                filelist.add(file.getAbsolutePath());
            }
        }
    }


    /**
     * 将一个对象写入文件
     * @param map
     * @param filepath
     */
    public static void WriteMap(Object map,String filepath)
    {
        try {
            FileOutputStream outStream = new FileOutputStream(filepath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
            objectOutputStream.writeObject(map);
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static Object ReadObj(String filepath)
    {
        FileInputStream freader;
        Object obj = new Object();
        try {
            freader = new FileInputStream(filepath);
            ObjectInputStream objectInputStream = new ObjectInputStream(freader);

            obj = (Object) objectInputStream.readObject();
            freader.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return obj;
    }
    public static Map ReadMap(String filepath)
    {
        FileInputStream freader;
        HashMap map = new HashMap();
        try {
            freader = new FileInputStream(filepath);
            ObjectInputStream objectInputStream = new ObjectInputStream(freader);

            map = (HashMap) objectInputStream.readObject();
            freader.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }
    public static Set readFromDict(String filepath)
    {
        String tempString;
        Set<String> set=new HashSet<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filepath));
            while ((tempString = reader.readLine()) != null)
            {
                set.add(tempString);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }
    /**
     * 写文件 每次一行
     * @param str 写入内容
     * @param filepath 写入文件位置
     * @param isadd 是否是追加
     * @return 写入结果
     */
    public static boolean Write2File(String str,String filepath,boolean isadd)
    {
        FileWriter fw = null;
        try {
            fw = new FileWriter(filepath,isadd);

            fw.write(str);

            fw.close();
        } catch (IOException e1) {
            return false;
        }finally {
            {
                return true;
            }
        }
    }

    /**
     * 从一个文件中获取指定行数目的内容
     * @param filepath 文件地址
     * @param linecont 需要取出的行的总数
     * @return 取出的内容
     */
    public static String getStingLine(String filepath,int linecont,String charset)
    {
        String content="";
        String tempString;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), charset));
            int line=0;
            while ((tempString = reader.readLine()) != null)
            {
                content+=tempString;
                content+="\n";
                line++;
                if(line==linecont)
                {
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 判断文件是否存在
     * @param filepath
     * @return
     */
    public static boolean IsExit(String filepath)
    {
        File f = new File(filepath);
        if(f.exists())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public static void DeleteFile(String filepath)
    {
        if(IsExit(filepath))
        {
            File f = new File(filepath);
            f.delete();
        }
    }
    public static void main(String[] args) {
        String text=getStingLine("J:\\BAIDU\\百度百科\\knowledgeBaseToolFinal\\baiduFinal0723.txt",5000,"utf-8");
        System.out.println(text);

    }
//    public static void main(String[] args) {
////        Map mm2=MyFile.ReadMap("L:\\program\\cip\\SAT-HISTORY\\3月\\时间地点相关\\ESA\\34.map");
//        Map mm1=MyFile.ReadMap("L:/program/cip/SAT-HISTORY/3月/时间地点相关/ESA/34.map");
//        Set<Integer> keySet = mm1.keySet();
//        long num=0;
//        Iterator<Integer> iter = keySet.iterator();
//        while(iter.hasNext()) {
//            int key = iter.next();
//            System.out.println(key);
////            String value=(String)mm1.get(key);
////            System.out.println(value);
//        }
//        System.out.println("你好");
//    }
}
