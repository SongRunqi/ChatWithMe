package top.yitianyuye.chatWithMe.index;

import java.sql.Connection;

//报废信息
public class ScrapInfo extends Module{
    public ScrapInfo(String[] tableName, String tablename, Connection conn, String sql){
        super(tableName,tablename,conn,sql);
        id="id";
        insertSql="insert into scrapinfo values(?,?,?,?)";
    }
}
