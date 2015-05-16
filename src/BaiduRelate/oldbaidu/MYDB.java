package BaiduRelate.oldbaidu;
import java.sql.*;




/**我的数据库函数 一般都是为了查找一些东西
 * Created by benywon on 2015/4/17.
 */
public  class MYDB  {
    public Connection con;
    public Statement statement;//是一个语句 每次更新都用这个语句来更新
    public String TableName="BaiduTagLists";
    public String IndexName="baikeKey";
    /**
     * 创建新表的函数 由于我们是为了存储百度taglist所以只有两个值  一个是key id  还有一个就是tag列表  text类型的
     * con.setAutoCommit(false);//不要自动更新表 这个是为了速度更快 程序最后一个在commit会使速度增加
     */
    public MYDB()
    {
        //初始化的时候我们将这个数据库进行更新
        try {
            Class.forName("org.postgresql.Driver" );
            String url = "jdbc:postgresql://localhost:5432/postgres" ;
            this.con = DriverManager.getConnection(url, "postgres", "cc2015");
            this.statement= this.con.createStatement();
            this.con.setAutoCommit(false);
            System.out.println("初始化数据库成功");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建数据库的一个表  一般只用一次
     */
    public void CreateTable()
    {
        try
        {

            String createtable="CREATE TABLE "+TableName+
                    " (ID INT NOT NULL,"+
                    "TagList TEXT )";
            this.statement.executeUpdate(createtable);
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
        System.out.println("成功添加了新表");
    }

    /**
     * 将这个数据库关闭 类似file.close()的操作
     */
    public void close()
    {
        try {
            statement.close();
            this.con.commit();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     *sql 删除一行 key是键值
     * @return
     */
    public boolean DeleteTable()
    {
        String sql="DROP TABLE "+TableName+";";
        try {
            this.statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     *sql 删除一行 key是键值
     * @param key
     * @return
     */
    public boolean DeleteItem(Integer key)
    {
        String sql="delete from "+TableName+"where "+IndexName+"="+key+";";
    try {
        this.statement.executeUpdate(sql);
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
    return true;
}

    /**
     * sql 添加一个东西
     * @param key
     * @param taglist
     * @return
     */
    public boolean InsertItem(Integer key,String taglist)
    {
        if(taglist!=null) {
            taglist = taglist.replaceAll("'", "-");
        }
        String sql="INSERT INTO "+TableName+"(ID,TagList) "+"VALUES ("+key+","+"'"+taglist+"');";
        try {
            this.statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从数据库中查找的函数 key是键值
     * @param key 键值
     * @return 查找到的内容
     */
    public String FindKey(Integer key)
    {
        String result=null;
        String sql = " select TagList from "+TableName+" where id="+key+";" ;
        try {
            ResultSet rs = this.statement.executeQuery(sql);
            if(rs.next())
            {
                result=rs.getString("TagList");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return  result;
    }
    public boolean getindex()
    {
        String sql="create index "+this.IndexName+" on "+this.TableName+"("+this.IndexName+");";
        try {
            this.statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

