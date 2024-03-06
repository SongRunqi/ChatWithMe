package table;
import java.sql.*;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

//获取表格和数据
public class Table  {
    //获取结果集
    public static ResultSet getResultSet(Connection conn,String sql){
        ResultSet rs=null;
        try{
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return rs;
    }
    //获取表格
    public static JScrollPane getTable(String[] tableName,ResultSet rs){
        JTable table = null;
        try{
            rs.last();
            int rows=rs.getRow();
            int colums = tableName.length;
            Object data[][] = readData(rows,colums,rs);//创建村塾数据的二维数组
            //创建表
            table = new JTable(data,tableName);
        }catch(SQLException e){
            e.printStackTrace();
        }
        //添加到滚动窗格
        JScrollPane js = new JScrollPane(table);
        return js;
    }


    public static JTable getRealTable(String[] tableName,ResultSet rs){
        JTable table = null;
        try{
            rs.last();
            int rows=rs.getRow();
            int colums = tableName.length;
            Object data[][] = readData(rows,colums,rs);//创建村塾数据的二维数组
            //创建表
            table = new JTable(data,tableName);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return table;
    }
    public static Object[][] readData(int row,int column,ResultSet rs){
        Object data[][] = new Object[row][column];
        try{
            rs.beforeFirst();
            int i=0;//控制行数
            while(rs.next()) {
                for(int j=0;j<column;j++){
                    data[i][j]=rs.getString(j+1);
//                    System.out.println(rs.getString(j+1)+"11111");
                }
                i++;
            }
        }catch(SQLException e2){

        }
        return data;
    }
    public static void deletRow(Connection conn,String sql,String value){
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,value);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void insert(Connection conn,String sql,Object rowdata[]){
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for(int i=1;i<=rowdata.length;i++){
                pstmt.setObject(i,rowdata[i-1]);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
     *  为了记录用户的行为，用户每点击一个功能，将会调用这个方法
     * 将用户的行为记录到logs表中
     */
    public static void record(Connection conn,String uid,String function){
        try{
            PreparedStatement p = conn.prepareStatement("insert into logs values(?,?,?)");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String date = df.format(new Date().getTime());// new Date()为获取当前系统时间，也可使用当前时间
            p.setString(1,uid);
            p.setString(2,function);
            p.setString(3,date);
            p.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /*
     * 更新用户的更改
     */
    public static void updateChange(Connection conn,String sql,Object f){
        try{
            PreparedStatement p = conn.prepareStatement(sql);
            p.setObject(1,f);
            p.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public static ResultSet select(Connection conn,String sql){
        try{
            PreparedStatement p = conn.prepareStatement(sql);
            return p.executeQuery();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String getIp(Connection conn,String uid){
        String sql = "select ip from user where uid = ?";
        String ip="";
        PreparedStatement p;
        try{
            p = conn.prepareStatement(sql);
            p.setString(1,uid);
            ResultSet rs = p.executeQuery();
            rs.next();
            ip = rs.getString("ip");
        }catch(SQLException e){
            e.printStackTrace();
        }
        return ip;
    }
    public static void setIp(Connection conn,String uid,String ip){
        String sql = "update user set ip=? where uid=?";
        PreparedStatement p;
        try {
            p = conn.prepareStatement(sql);
            p.setString(1,ip);
            p.setString(2,uid);
            p.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
