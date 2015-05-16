package HudongRelate;

import Bases.MyFile;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by benywon on 2015/4/16.
 */
public class GetHudong {
    public static void getfile(String infilename,String outfilename,String charset)
    {
        try {
            FileInputStream fileInputStream=new FileInputStream(infilename);
            GZIPInputStream gzipInputStream=new GZIPInputStream(fileInputStream);
            InputStreamReader inputStreamReader=new InputStreamReader(gzipInputStream,charset);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String l;
            int i=0;
            while ((l = reader.readLine()) != null) {
                i++;
                if(i==90000) {
                    break;
                }
//                System.out.println(l);
                MyFile.Write2File(l,outfilename,true);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

    }

    public static void main(String[] args) {
        getfile("J:\\BAIDU\\hdbk_cont.all.gz","J:\\BAIDU\\hdbk_cont.all_thumb.txt","gb2312");
    }

}
