package top.yitianyuye.chatWithMe.log;
import top.yitianyuye.chatWithMe.table.Table;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Chat extends JPanel implements ActionListener,MouseListener,Runnable{
    private Connection conn;//保存与数据库之间的连接
    private final int port[]={7273,8274,6275,9276,17277,27278};
    private ResultSet friendList;//结果集为朋友列表，存放朋友的各种信息
    private String uid;//本用户id
    private String name;
    private List<String> friendUid =new ArrayList<String>();//朋友id列表
    private List<String> friendName = new ArrayList<String>();//朋友姓名列表
    private int friendNum;//存放朋友的个数
    private JPanel fp[];//朋友面板
    public JTextArea chatArea[];
    public JTextArea willSend[];
    public JButton send = new JButton("发送");
    private String imageName[]={"/headImage.jpg","/01.jpg","/02.jpg","/03.jpg","/04.jpg","/05.jpg","/06.jpg","/07.jpg","/08.jpg","/09.jpg"};
    public JPanel chatp;//本面板
    public JPanel center=new JPanel();
    private boolean isclient=true;//如果是第一次发送信息，那么自己作为客户端，另一方作为服务端
    JScrollPane js[];//同下
    JScrollPane js1[];//放置JTextArea
    JPanel buttonp;//放置发送按钮的面板
    private Socket fclient=null;//保存连入的客户端
    private Socket client=null;//自己作为客户端
    private ServerSocket server=null;//服务器
    private DataInputStream cread=null;//作为服务器请求连接的客户端的输入输出流
    private DataOutputStream cwrite=null;
    private DataInputStream sread=null;
    private String currentFriendUid;
    private DataOutputStream swrite=null;
    private int uport;
    private int fport;
    private int i=2;//1作为客户端，0服务器
    private Thread rev=null;
    private Thread t=null;
    private int chartNum;//指定自己的聊天面板序号
    private int yourchartNum;//指定对方的聊天面板序号
    private int clicknum=-1;
    private JButton sendFile = new JButton("发送文件");
    private String ip;//本机ip
    private String IP;//朋友的ip
    JLabel l[];
    JLabel l1[];
    JLabel l2[];
    public Chat(Connection conn,String uid,String ip){
        //获取参数
        this.conn = conn;
        this.uid = uid;
        this.ip = ip;
        Table.setIp(conn,uid,ip);
        init();

    }

    /*
     * 添加组件
     */
    public void init(){
        //分配端口号

        chatp= this;
        //设置边界布局管理
        setLayout(new BorderLayout());
        //创建panel对象
        JPanel west = getFriendPanel();
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        buttonp = new JPanel();//放button
        buttonp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        send.addActionListener(this);
        buttonp.add(send);
        buttonp.add(sendFile);
        
        sendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        //添加到容器内
        add("West",west);
        add("Center",center);
    }

    /*
     * 生成一个朋友列表的面板
     * return JPanel BoxLayout
     */
    public JPanel getFriendPanel(){
        //朋友来列表面板
        JPanel friendPanel=new JPanel();
        friendPanel.setLayout(new BoxLayout(friendPanel,BoxLayout.Y_AXIS));
        PreparedStatement pstmt=null;
        String sql="select uid,name,sex,birthday from user where uid in (select frienduid from friend where uid=?)";
        try{
            //创建sql语句并执行
            pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            pstmt.setString(1,uid);
            friendList = pstmt.executeQuery();
            //获取朋友个数
            friendList.last();
            friendNum = friendList.getRow();

                l = new JLabel[friendNum];
                l1 = new JLabel[friendNum];
                l2 = new JLabel[friendNum];

            //获取朋友信息
            get("uid",friendUid);
            get("name",friendName);
            System.out.println("friendUid更新完成");
            System.out.println(friendUid.size());

            //根据好友数目，生成对应的聊天面板
            chatArea = new JTextArea[friendUid.size()];
            willSend = new JTextArea[friendUid.size()];
            js = new JScrollPane[friendUid.size()];
            js1 = new JScrollPane[friendUid.size()];
            for(int i=0;i<friendUid.size();i++){
                chatArea[i] = new JTextArea();
                willSend[i] = new JTextArea();
                //给chatArea 添加JS
                js[i] = new JScrollPane(chatArea[i]);
                js1[i] =new JScrollPane(willSend[i]);
                js[i].setPreferredSize(new Dimension(320,220));
                js1[i].setPreferredSize(new Dimension(320,170));
                js[i].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                js1[i].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                chatArea[i].setLineWrap(true);        //激活自动换行功能
                chatArea[i].setWrapStyleWord(true);            // 激活断行不断字功能
                willSend[i].setLineWrap(true);        //激活自动换行功能
                willSend[i].setWrapStyleWord(true);            // 激活断行不断字功能
                //chatArea设置不可编辑
                chatArea[i].setEnabled(false);
            }
        }catch(Exception ee){
            ee.printStackTrace();
        }
        fp = new JPanel[friendNum];
        //生成装friendPanel  的面板，设置盒式布局管理器，Y型
        JPanel fpp=new JPanel();
        fpp.setLayout(new BoxLayout(fpp,BoxLayout.Y_AXIS));
       //给每个朋友添加个人信息
        for(int i=0;i<fp.length;i++){
            //生成头像
            ImageIcon imageIcon=new ImageIcon(getClass().getResource(imageName[i]));
            Image image = imageIcon.getImage(); // transform it
            Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
            imageIcon = new ImageIcon(newimg);
            //实例化朋友面板并设置流布局
            fp[i] = new JPanel();
            fp[i].setLayout(new FlowLayout(FlowLayout.LEFT));
            //给fpi添加信息
             l[i] = new JLabel("",imageIcon,JLabel.LEFT);
             l1[i] = new JLabel("id:"+friendUid.get(i));
             l2[i] = new JLabel("    姓名:"+friendName.get(i));
            fp[i].add(l[i]);
            fp[i].add(l1[i]);
            fp[i].add(l2[i]);
            fp[i].setOpaque(false);
            //给fp[i]增加监听器
            fp[i].addMouseListener(this);
            fpp.add(fp[i]);
        }
        //给willsend添加监听器
        JScrollPane fpjs=new JScrollPane(fpp);
        fpjs.setPreferredSize(new Dimension(250,380));
        friendPanel.add(fpjs);
        return friendPanel;
    }


    /*
     * 通过列名和列表来获取朋友的一些信息
     */
    public void get(String col,List list){
        try{
            friendList.beforeFirst();
            while(friendList.next()){
                list.add(friendList.getString(col));//从结果集获取数据
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /*发送按钮的事件监听器：根据自己是服务器还是客户端调用不同的方法
     *  实现发送信息的功能
     */
    public void actionPerformed(ActionEvent e){
        if(fclient!=null) {
            sendMessage(willSend[chartNum].getText(), 0);//发送信并显示
        }
        else {
            sendMessage(willSend[chartNum].getText(), 1);
        }
        willSend[chartNum].setText("");
    }


    /*实现Runnable方法
     *  指定服务器，启动服务器
     */
    public void run(){

        try{
            getPort();
            server = new ServerSocket(uport + 1);
            System.out.println("服务线程已启动，服务端口："+(uport + 1));
            System.out.println("服务线程已启动");
            rec_Thread();//运行接受服务线程

        }catch(Exception ee){
            ee.printStackTrace();
        }



    }

    /*
     * i指定this是服务器还是客户端
     * m要发送的信息
     *
     */
    public void sendMessage(String m,int i){
        try{//作为服务器
            if(i==0){
                cwrite.writeUTF("uid"+"@"+m+"\r\n");//标志自己的id
                System.out.println("向客户端发送成功");
            }
            else{

                swrite.writeUTF("uid"+"@"+m+"\r\n");
                System.out.println("向服务器发送成功");
            }
            showMeMessage(m+"\r\n");
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /*
     * 接收信息，将信息展示在聊天框内
     */
    public void showHeMessage(int i){
        try{
//            System.out.println(i);
            String  m="";

            if(i==0) {
                m = cread.readUTF();
                String u[] = m.split("@",2);
//                System.out.println(u.toString());
                String uid = u[0];
                for(int j=0;j<friendUid.size();j++){
                    if (uid.equals(friendUid.get(j))){
                        chartNum = j;
                        name = friendName.get(j);
                    }
                }
                m = u[1];
            }
            if(i==1){

                m = sread.readUTF();
                System.out.println("showHeMessage"+m);
                String u[] = m.split("@",2);
                String uid = u[0];
                for(int j=0;j<friendUid.size();j++){
                    if (uid.equals(friendUid.get(j))){
                        chartNum = j;
                        name = friendName.get(j);//获取聊天对象的名称
                    }
                }
//                if(clicknum!=chartNum)
//                    JOptionPane.showMessageDialog(null,name+"向你发来一条消息");
                m = u[1];
            }
            m = name+"："+m;
            System.out.println(chartNum);
            chatArea[chartNum].setText(chatArea[chartNum].getText()+m);
            System.out.println("消息展示完成");
        }catch(IOException e){

        }
    }
    /*
     * 将发送框的消息展示在聊天框
     */
    public void showMeMessage(String m){
        m="我："+m;//格式话消息
        chatArea[chartNum].setText(chatArea[chartNum].getText()+m);
    }
    /*
     * 首先点击用户会话框的将会作为客户端，另一方作为服务器
     * 如果是客户端，则启动接收消息线程
     * 成员变量赋值i为1
     */
    public void mouseClicked(MouseEvent e) {
        for(int i=0;i<fp.length;i++){
            if(e.getSource()==fp[i]){
                name = friendName.get(i);
                Font f = new Font("细明本",Font.PLAIN,12);
                l[i].setForeground(Color.red);
                l1[i].setForeground(Color.red);
                l2[i].setForeground(Color.red);
                clicknum = i;
                JOptionPane.showMessageDialog(null,"切换聊天对象："+name);
                IP  = Table.getIp(conn,friendUid.get(i));//获取朋友的IP
                chartNum = i;
                try{

                    getFport(friendUid.get(i));
                    InetAddress inet=InetAddress.getByName("localhost");
                    String ip = inet.getHostAddress();
                    if(fclient==null){
                        getPort();
                        client = new Socket(IP,fport + 1);//本机作为客户端
                        System.out.println("本机作为客户端,连接服务器" + IP + ":" + (fport + 1));
                        sread = new DataInputStream(client.getInputStream());
                        swrite = new DataOutputStream(client.getOutputStream());
                        this.i = 1;//作为客户端
                        rev_Thread();//启动接收消息线程
                    }
                }catch(Exception e1){

//                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null,"好友离线中。。。");
                }
                //刷新content
                center.removeAll();
                center.add(js[i]);
                center.add(buttonp);
                center.add(js1[i]);
                setVisible(false);
                setVisible(true);
                break;///////为什么就好了呢？
            }
            else{
                //选染成黑色
                for(int j=0;j<friendNum;j++) {
                    if(e.getSource()!=fp[i]) {
                        l[j].setForeground(Color.BLACK);
                        l1[j].setForeground(Color.BLACK);
                        l2[j].setForeground(Color.BLACK);
                    }
                }
            }

        }


    }

    /*
     * 接收消息线程，匿名类
     * 不断调用showHeMessage方法
     */
    public void rev_Thread(){
        rev=new Thread(){
            public void run(){
                while(true){
                    showHeMessage(i);//读取信息并展示
                }

            }
        };
        rev.start();
    }
    /*
     * 接受请求的线程
     * 作为服务器，启动接受消息线程
     * i=0
     */
    public void rec_Thread(){
        t = new Thread(){
            public void run(){
                while(true){
                    try{
                        fclient = server.accept();
                        System.out.println("已有客户端接入服务器");
                        cwrite = new DataOutputStream(fclient.getOutputStream());
                        cread = new DataInputStream(fclient.getInputStream());//获取对应客户端请求连接的输入输出流
                        i = 0;//作为服务器
                        while(true){
                            if(i!=2){
                                System.out.println("接受消息线程已启动");
                                rev_Thread();//运行接收消息线程
                                break;
                            }

                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    /*
     * 为了能在一台电脑上运行
     * 根据uid 自动分配不同的端口号，
     * 以避免端口号被占用
     * 获取客户端的port
     */
    public void getFport(String uid){
        switch(uid){
            case "1":fport=port[0];
                break;
            case "111":fport=port[1];
                break;
            case "222":fport = port[2];
                break;
            case "123":fport = port[3];
                break;
            case "321":fport = port[4];
                break;
            case "125711713":fport = port[5];
                break;

        }

    }
    /*
     * 获取本机的port
     */
    public void getPort(){
        switch(uid){
            case "1":uport=port[0];
                        break;
            case "111":uport=port[1];
                        break;
            case "222":uport = port[2];
                        break;
            case "123":uport = port[3];
                        break;
            case "321":uport = port[4];
                        break;
            case "125711713":uport = port[5];
                        break;
        }
        System.out.println(uid);
        System.out.println("本机："+ip+"端口："+uport);
    }

    /*
     * 更新chartNUm
     * chartnum指的是第几个聊天面板
     * 为了避免将其他面板的信息发送给非指定用户
     *
     */
    public void updateNum(){

    }
/*
 * 空方法，只是为了实现鼠标监听事件的抽象方法
 */
    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
