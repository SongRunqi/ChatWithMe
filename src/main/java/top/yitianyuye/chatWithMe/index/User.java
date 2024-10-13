package top.yitianyuye.chatWithMe.index;

import java.sql.Connection;

//用户信息
public class User extends Module{
    public User(String[] tableName, String tablename, Connection conn, String sql){
        super(tableName,tablename,conn,sql);
        id="uid";
        insertSql="insert into User values(?,?,?,?,?,?,?)";
    }
}
