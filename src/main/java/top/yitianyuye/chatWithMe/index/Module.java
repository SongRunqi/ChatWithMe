package top.yitianyuye.chatWithMe.index;
import top.yitianyuye.chatWithMe.table.Table;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

//像是一个表格，
public class Module extends JPanel implements ActionListener{
    protected JTable table;
    protected DefaultTableModel model;//设置表格模式
    protected JScrollPane js;
    protected String tableName[];
    protected String tablename;
    protected Object data[][];
    protected String columName[]=null;
    Object rowData[][]={null,null,null,null,null};
    protected Connection conn;
    protected JButton change = new JButton("进入编辑模式");
    protected JButton delete = new JButton("删除");
    protected JButton quit = new JButton("保存修改");
    protected JButton add = new JButton("添加");
    private float kickNum=0;
    private ResultSet rs;
    private int Row;//未更改之前的行数
    protected String id="";//子类需要指定对应表的主键
    protected String insertSql="";
    JTextField search[];
    JButton searchb = new JButton("查找");
    ////表获取
    public Module(String[] tableName,String tablename, Connection conn,String sql){
        this.tableName = tableName;
        this.tablename=tablename;
        this.conn = conn;
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        table = new JTable();
        add.setEnabled(false);
        quit.setEnabled(false);
        delete.setEnabled(false);
        try{
            rs= Table.getResultSet(conn,sql);
            rs.last();
            int r = rs.getRow();
            Row = r;
            //获取真实的列名
            ResultSetMetaData rsmd = rs.getMetaData();
            int q = rsmd.getColumnCount();
            columName = new String[q];
            search = new JTextField[q];//生成搜索框
            for(int t=0;t<q;t++){
                search[t] = new JTextField((10-q));//实例化搜索框
                columName[t] = rsmd.getColumnName(t+1);
                System.out.println(columName[t]);
                this.add(search[t]);//将搜索框添加到容器内
            }///
            int c = tableName.length;
            data = Table.readData(r,c,rs);
            model = new DefaultTableModel(data,tableName);//给table设置模式，为了可以使用model的addRowData方法
            table.setModel(model);
            table.setEnabled(false);
            table.setCellSelectionEnabled(false);
            js = new JScrollPane(table);
            js.setPreferredSize(new Dimension(550,303));
        }catch(Exception e1){
            e1.printStackTrace();
        }
        //增加监听器
        change.addActionListener(this);
        delete.addActionListener(this);
        add.addActionListener(this);
        quit.addActionListener(this);
        System.out.println(search[0].getText());
        searchb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int count = 0;//查询条件
                int mark[]=new int[search.length];//1表示对应的搜索框有值
                for(int i=0;i<search.length;i++){
                    if(!search[i].getText().equals("")){
                        count++;
                        mark[i]=1;
                        System.out.println("right");
                    }
                    else mark[i] = 0;
                }
                for(int i=0;i<search.length;i++){
                    if(count==i&&i==0){
                        //没有输入内容就要搜索
                        JOptionPane.showMessageDialog(null,"你还没有输入搜索内容。");
                    }
                    else if(count==i&&i==1){
                        //只查询一个字段
                        int j=0;
                        for(j=0;j<mark.length;j++){
                            if(mark[j]!=0)
                                break;
                        }
                        String sql = "select * from "+tablename+" where "+columName[j]+"= '"+search[j].getText()+"' ";
                        System.out.println(sql);
                        rs = Table.select(conn,sql);
                        int row=0;
                        try {
                            rs.last();
                            row = rs.getRow();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        data = Table.readData(row,columName.length,rs);
                        model = new DefaultTableModel(data,tableName);//给table设置模式，为了可以使用model的addRowData方法
                        table.setModel(model);
                        table.setEnabled(false);
                        table.setCellSelectionEnabled(false);
                        setVisible(false);
                        setVisible(true);
                        if(row!=0)
                            JOptionPane.showMessageDialog(null,"查找成功");
                        else
                            JOptionPane.showMessageDialog(null,"无信息");

                    }
                    else if(count==i&&i==2){
                        //两个字段
                        int j=0,k=0,c=0;
                        for(j=0;j<mark.length;j++){
                            if(mark[j]!=0)
                                    k = j;
                            else if(k!=0&&mark[j]!=0){
                                 c = j;
                            }
                        }
                        String sql = "select * from "+tablename+" where "+columName[k]+"='"+search[k].getText()+"' and "+columName[c]+"='"+search[c].getText()+"'";
                        System.out.println(sql);
                        rs = Table.select(conn,sql);
                        int row=0;
                        try {
                            rs.last();
                            row = rs.getRow();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        data = Table.readData(row,columName.length,rs);
                        model = new DefaultTableModel(data,tableName);//给table设置模式，为了可以使用model的addRowData方法
                        table.setModel(model);
                        table.setEnabled(false);
                        table.setCellSelectionEnabled(false);
                        setVisible(false);
                        setVisible(true);
                        if(row!=0)
                            JOptionPane.showMessageDialog(null,"查找成功");
                        else
                            JOptionPane.showMessageDialog(null,"无信息");
                    }
                    else if(count>=3){
                        JOptionPane.showMessageDialog(null,"暂不支持多条件搜索！");
                    }
                }
            }
        });
        this.add(searchb);
        this.add(js);
        this.add(change);
        this.add(delete);
        this.add(add);
        this.add(quit);
        this.setVisible(true);
    }
    //实现接口方法ActionListener
    public void actionPerformed(ActionEvent e){
        String sql="";
        if(e.getActionCommand().equals("进入编辑模式")||e.getActionCommand().equals("退出编辑模式")){
            kickNum++;
            /*
             *判断点击次数的奇偶
             * 奇数设置为可编辑状态，按钮更改为退出编辑模式
             * 偶数为不可编辑状态，，
             *
             */
            if(kickNum%2!=0) {
                //移除组件，设置编辑状态为可编辑状态
                this.remove(js);
                table.setEnabled(true);
                table.setCellSelectionEnabled(true);
                this.remove(change);
                change.setText("退出编辑模式");//更改按钮的文本
                this.add(js);
                this.remove(delete);
                this.remove(add);
                this.remove(quit);
                delete.setEnabled(true);
                add.setEnabled(true);
                quit.setEnabled(true);
                this.add(change);
                this.add(delete);
                this.add(add);
                this.add(quit);
                this.setVisible(false);
                this.setVisible(true);
            }
            else{
                //移除组件，设置编辑状态为不可编辑，再次添加组件
                if (table.isEditing())
                    table.getCellEditor().stopCellEditing();
                this.remove(js);
                this.remove(change);
                table.setEnabled(false);
                table.setCellSelectionEnabled(false);
                change.setText("进入编辑模式");
                this.remove(delete);
                this.remove(add);
                this.remove(quit);
                delete.setEnabled(false);
                add.setEnabled(false);
                quit.setEnabled(false);
                this.add(js);
                this.add(change);
                this.add(delete);
                this.add(add);
                this.add(quit);
                //取消表格选中状态
                table.getSelectionModel().clearSelection();

                //刷新
                this.setVisible(false);
                this.setVisible(true);
            }

        }else if(e.getActionCommand().equals("添加")){
            model.addRow(rowData);
            int n = table.getSelectedRow();
            table.validate();
            table.updateUI();//更新
//            System.out.println(top.yitianyuye.chatWithMe.table.getRowCount());
        }else if(e.getActionCommand().equals("删除")){
            if(table.getSelectedRow()==-1){
                JOptionPane.showMessageDialog(null,"请选择...");
            }
            else{
                int n = table.getSelectedRow();
                model.removeRow(n);
                System.out.println(n);
                String value= data[n][0].toString();
                sql = "delete from "+tablename+" where "+id+"=?";
                System.out.println(sql);
                Table.deletRow(conn,sql,value);
            }
        }else if(e.getActionCommand().equals("保存修改")){
            if (table.isEditing())
                table.getCellEditor().stopCellEditing();    //让JTable失去焦点！！！！！！！！！！
            //执行更新操作
            for(int i=0;i<Row;i++){
                Object id = data[i][0];
                //如果更改主键，拒绝
                if(!data[i][0].toString().equals(table.getValueAt(i,0))){
                    JOptionPane.showMessageDialog(null,"不可以进行修改");
                }
                for(int j=1;j<tableName.length;j++){
                    if(table.getValueAt(i,j)!=data[i][j]){
                        String s = "update "+tablename+" set "+columName[j]+"=?";
                        Table.updateChange(conn,s,table.getValueAt(i,j));
                    }
                }
            }
            int r = table.getRowCount();
            Object o[][] = new Object[r-Row][tableName.length];
            for(int i=Row;i<r;i++){
                for(int j=0;j<tableName.length&&table.getValueAt(i-Row,j)!=null;j++){
                    o[i-Row][j] = table.getValueAt(i,j);
                }
                //执行inset
               Table.insert(conn,insertSql,o[i-Row]);
                Row = table.getRowCount();

            }
        }

    }

}
