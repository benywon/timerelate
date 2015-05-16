package BaiduRelate.oldbaidu;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by benywon on 2014/12/27.
 */
public class saxxml extends DefaultHandler {
    private StringBuffer iobuf;
    private HashMap attrmap;
    public Document doc;
    public void startDocument() throws SAXException
    {
        iobuf=new StringBuffer();
        this.doc = new Document();
        this.attrmap = new HashMap();
//        System.out.println("文档解析开始");
    }
    public void endDocument() throws SAXException
    {
//        System.out.println("文档解析结");
    }
    public void startElement(String namespaceURI,String localName,String qName,Attributes atts)

    {

        iobuf.setLength(0);
//        if(atts.getLength()>0){
//
//
//
//            for(int i = 0; i < atts.getLength();i++)
//
//                this.attrmap.put(atts.getQName(i), atts.getValue(i));
//
//        }



//        System.out.println("标记名->"+qName);
//
//        for(int i=0;i<atts.getLength();i++)
//
//        {
//
//            System.out.println("属性值->"+atts.getValue(i));
//
//        }

    }
    public void endElement(String namespaceURI,String localName,String fullName )throws SAXException

    {

        if (fullName.equals("baike")) {

            return;

        }
        else if (fullName.equals("item")) {

            Iterator iter = attrmap.keySet().iterator();

            while (iter.hasNext()) {

                String attName = (String) iter.next();//System.out.println("---"+attName);

                String attValue = (String) attrmap.get(attName);
                Field field;
                if(attName.indexOf("content")>=0)//说明是具体内容的  需要我们做进一步的分词等等
                {
                    String after=rexls(attValue);
                    field = new VecTextField(attName, after, Field.Store.YES);//这样做可以打印并且是我们推荐的方法
                    //注意我们没有存储termvector
//                    if(attName.equals("summarycontent"))
//                    {
//                        field.setBoost(1.5f);
//                    }
                }
                else
                {
                    field = new StringField(attName, attValue,
                            Field.Store.YES);//这样做可以打印并且是我们推荐的方法
//                    if(attName.equals("lemmatitle"))
//                    {
//                        field.setBoost(3f);
//                    }
//                    else if(attName.equals("sublemmatitle"))
//                    {
//                        field.setBoost(2f);
//                    }
//                    else
//                    {
//                        field.setBoost(1f);
//                    }

                }
                doc.add(field);
            }
        }
        else {
            this.attrmap.put(fullName,iobuf.toString());
        }
//        System.out.println("文本值->"+iobuf.toString());

    }

    public void characters( char[] chars, int start, int length )throws SAXException

    {
        iobuf.append(chars, start, length);
    }
    public static String rexls(String in)
    {
        String ccpre=in;
        String cc="";
        while(true) {
            cc = ccpre.replaceFirst("\\[img\\].*\\[/img\\]", "");
            cc = cc.replaceFirst("\\[module\\].*\\[/module\\]", "");
            if(cc.equals(ccpre)){
                break;
            }
            ccpre=cc;
        }
        cc =cc.replaceAll("\\[[a-z,A-Z,/]*\\]","");
        return cc;
    }

}
