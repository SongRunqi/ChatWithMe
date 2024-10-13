package top.yitianyuye.chatWithMe.index;

import java.sql.Connection;

//设备管理信息
public class EptmInfo extends Module{
    public EptmInfo(String[] tableName, String tablename, Connection conn, String sql){
        super(tableName,tablename,conn,sql);
        id="id";
        insertSql="insert into eptminfo values(?,?,?,?,?,?,?)";
    }
}
