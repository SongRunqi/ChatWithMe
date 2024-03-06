package index;

import java.sql.Connection;

//设备信息
public class Equipment extends Module{
    public Equipment(String[] tableName, String tablename, Connection conn, String sql){
        super(tableName,tablename,conn,sql);
        id="id";
        insertSql="insert into equipment values(?,?,?,?,?,?,?)";
    }
}
