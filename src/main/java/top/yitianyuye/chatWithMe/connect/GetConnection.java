package top.yitianyuye.chatWithMe.connect;
import java.sql.*;
import top.yitianyuye.chatWithMe.exception.*;
public class GetConnection {
    public static Connection getConnection() throws GetConnectionException{
        String user="root";
        String password="english_improver";
        String url = "jdbc:mysql://8.141.83.81:3306/chat?useUnicode=true&characterEncoding=utf8";
        Connection conn=null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
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



