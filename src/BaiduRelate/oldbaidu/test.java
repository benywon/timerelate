package BaiduRelate.oldbaidu;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Created by benywon on 2015/4/17.
 */
public class test {
    public static String CONN_STR = "jdbc:postgresql://localhost:5432/HomeworkDB";

    public static Connection getConn() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver").newInstance();
            conn = DriverManager.getConnection(test.CONN_STR, "postgres",
                    "123");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args){
        String sql = "CREATE DATABASE \"db\"";
        try {
            Statement stmt = test.getConn().createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            System.out.println("successed!");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
