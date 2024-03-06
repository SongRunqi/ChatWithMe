package index;
import javax.swing.*;
import java.sql.Connection;

public class Department extends Module{
    public Department(String[] tableName,String tablename, Connection conn,String sql){
        super(tableName,tablename,conn,sql);
        id = "dpid";
        insertSql="insert into department values(?,?,?,?,?)";
    }
}
