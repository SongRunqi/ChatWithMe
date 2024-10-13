package top.yitianyuye.chatWithMe.index;

import java.sql.Connection;

public class Insepectioninfo extends Module{
    public Insepectioninfo(String[] tableName, String tablename, Connection conn, String sql){
        super(tableName,tablename,conn,sql);
        id="id";
        insertSql="insert into insepectioninfo values(?,?,?,?,?,?)";
    }
}
