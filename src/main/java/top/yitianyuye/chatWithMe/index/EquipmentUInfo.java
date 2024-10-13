package top.yitianyuye.chatWithMe.index;

import java.sql.Connection;

//用户使用信息
public class EquipmentUInfo extends Module{
    public EquipmentUInfo(String[] tableName, String tablename, Connection conn, String sql){
        super(tableName,tablename,conn,sql);
        id="uid";
        insertSql="insert into equipmentuinfo values(?,?,?,?,?,?,?)";
    }
}
