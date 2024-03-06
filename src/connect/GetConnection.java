package connect;
import java.sql.*;
import exception.*;
public class GetConnection {
    public static Connection getConnection() throws GetConnectionException{
        String user="root";
        String password="root";
        String url = "jdbc:mysql://localhost:3306/yuzhoushixun?useUnicode=true&characterEncoding=utf8";
        Connection conn=null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url,user,password);//连接数据库
        }catch(SQLException e){
             e.printStackTrace();
             throw new GetConnectionException();
        }catch(ClassNotFoundException e2){
            e2.printStackTrace();
        }

        return conn;
    }
}



