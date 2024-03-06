package index;
import exception.NonactivatedException;
import log.Chat;
import table.Table;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;


public class Manger extends JFrame implements Runnable,ActionListener {
    private Connection conn;
    private Object[][] data;
    private String uid;
    private String department[]={"部门编号","部门名称","部门类别","部门负责人","联系电话"};//部门表名
    private String eptminfo[]={"设备id","设备信息","故障问题","维修地点","维修负责人","维修开始日期","维修预计结束时间"};//设备维修信息
    private String equipment[]={"设备id","设备编号","设备名称","设备信息","状态","部门类别","设备类别"};//设备表
    private String equipmentuinfo[]={"用户名","设备id","使用部门","联系电话","用途","使用开始日期","试用结束日期"};//设备使用信息
    private String inspectioninfo[]={"设备id","检验内容","预期目标","检验负责人","检验日期"};//检验信息表
    private String logs[]={"用户名","使用内容","操作时间"};//记录表
    private String scrapinfo[] = {"设备id","报废原因","保费日期","经办人"};//报废信息表
    private String users[] = {"用户名","密码","姓名","性别","生日","邮件","级别"};
    private String buttonName[]={"部门信息","设备维修信息","设备信息","设备使用信息","用户记录","设备报废信息","用户信息","设备检验信息","退出登录","聊一聊"};
    private int i;
    private int priority=-1;
    private JPanel centerP=new JPanel();
    private String ip;//本机ip
    private String IP;//朋友的ip
    public Manger(Connection conn,Object data[][],int i,String uid,String ip){
        super();
        this.conn = conn;
        this.data = data;
        this.i = i;
        this.uid = uid;
        this.ip = ip;
        //中间面板设置布局管理器
        centerP.setLayout(new FlowLayout(FlowLayout.LEFT));
        centerP.setToolTipText("聊天面板");
        //获取用户对用的权限
        try{
            getPriority();
        }catch(Exception eee){
            eee.printStackTrace();
        }//
        Container c= this.getContentPane();
        c.setLayout(new BorderLayout());//整个布局时北方显示预览个人信息，西方放置各种功能按钮8，中间面板为空面板
        //创建9个按钮，并实例化
        JButton button[] = {new JButton("部门信息"),new JButton("设备维修信息"),new JButton("设备信息"),new JButton("设备使用信息"),new JButton("用户记录"),new JButton("设备报废信息"),new JButton("用户信息"),new JButton("设备检验信息"),new JButton("退出登录"),new JButton("聊一聊")};
        //创建3个面板设置布局管理器
        JPanel panel[]={new JPanel(),new JPanel()};//North,west
        panel[1].setLayout(new GridLayout(13,1));
        panel[0].setLayout(new FlowLayout());
        //将按钮装进panel【1】
        for(int j=0;j<button.length;j++){
            panel[1].add(button[j]);
            button[j].addActionListener(this);
        }
        if(priority==0){
            button[4].setEnabled(false);
        }
        //预览信息,放入panel【0】
        JLabel label = new JLabel("UID："+data[i][0].toString());
        JLabel label2 = new JLabel("姓名："+data[i][2].toString());
        panel[0].add(label);
        panel[0].add(label2);
        //装进contenPane
         c.add("North",panel[0]);
         c.add("West",panel[1]);
         c.add("Center",centerP);
        //设置大小和可见性
        this.setBounds(280,120,704,488);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void run(){

    }
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals(buttonName[0])){
            remove(centerP);
            centerP = new Department(department,"department",conn,"select * from department");
            Table.record(conn,uid,buttonName[0]);
            System.out.println("记录了，但是没挤上");
            add(centerP);
            setVisible(false);
            setVisible(true);
        }
        else if(e.getActionCommand().equals(buttonName[1])){
            remove(centerP);
            centerP = new EptmInfo(eptminfo,"eptminfo",conn,"select * from eptminfo");
            Table.record(conn,uid,buttonName[1]);
            add(centerP);
            setVisible(false);
            setVisible(true);
        }
        else if(e.getActionCommand().equals(buttonName[2])){
            remove(centerP);
            centerP = new Equipment(equipment,"equipment",conn,"select * from equipment");
            Table.record(conn,uid,buttonName[2]);
            add(centerP);
            setVisible(false);
            setVisible(true);
        }
        else if(e.getActionCommand().equals(buttonName[3])){
            remove(centerP);
            centerP = new EquipmentUInfo(equipmentuinfo,"equipmentuinfo",conn,"select * from equipmentuinfo");
            Table.record(conn,uid,buttonName[3]);
            add(centerP);
            setVisible(false);
            setVisible(true);
        }
        else if(e.getActionCommand().equals(buttonName[4])){
            remove(centerP);
            centerP = new Logs(logs,"logs",conn,"select * from logs");
            Table.record(conn,uid,buttonName[4]);
            add(centerP);
            setVisible(false);
            setVisible(true);
        }
        else if(e.getActionCommand().equals(buttonName[5])){
            remove(centerP);
            centerP = new ScrapInfo(scrapinfo,"Scrapinfo",conn,"select * from scrapinfo");
            Table.record(conn,uid,buttonName[5]);
            add(centerP);
            setVisible(false);
            setVisible(true);
        }
        else if(e.getActionCommand().equals(buttonName[6])){
            remove(centerP);
            centerP = new User(users,"user",conn,"select * from user");
            Table.record(conn,uid,buttonName[6]);
            add(centerP);
            setVisible(false);
            setVisible(true);
        }else if(e.getActionCommand().equals(buttonName[7])){
            remove(centerP);
            centerP = new Insepectioninfo(inspectioninfo,"insepctioninfo",conn,"select * from insepctioninfo");
            Table.record(conn,uid,buttonName[7]);
            add(centerP);
            setVisible(false);
            setVisible(true);
        }
        else if(e.getActionCommand().equals(buttonName[8])){
            int i=JOptionPane.showConfirmDialog(null,"确定退出？");//返回值为0表示用户点击了确定
            System.out.println(i);
            if(i==0){
                System.exit(0);//退出程序
                Table.record(conn,uid,buttonName[8]);
            }
        }else if(e.getActionCommand().equals(buttonName[9])){
            //移除组件在添加
            remove(centerP);
            centerP.removeAll();
            Chat chat = new Chat(conn,uid,ip);
            Thread t = new Thread(chat);
            t.start();
            Table.record(conn,uid,buttonName[9]);
            centerP.add(chat);
            add("Center",centerP);
            //刷新
            setVisible(false);
            setVisible(true);
        }



    }
    //获取对应权限
    private void getPriority() throws NonactivatedException{
        if(data[i][6].equals("一般用户")){
            priority = 0;
        }
        else if(data[i][6].toString().equals("管理员")){
            priority = 1;
        }
        else
            throw new NonactivatedException();
    }

}
