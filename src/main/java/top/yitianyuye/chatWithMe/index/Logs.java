package top.yitianyuye.chatWithMe.index;

import java.sql.Connection;

//用户使用记录
public class Logs extends Module{
    public Logs(String[] tableName, String tablename, Connection conn, String sql){
        super(tableName,tablename,conn,sql);
        id="uid";
        insertSql="insert into logs values(?,?,?)";
    }
}
