package log;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import connect.*;
import exception.*;
import index.*;
import table.Table;
import java.io.*;
import java.sql.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class Login extends JFrame implements ActionListener,Runnable{
    private Connection conn=null;//
    private String users[] = {"用户名","密码","姓名","性别","生日","邮件","级别"};
    private String uid;//用户id
    private String pass;//密码
    private JTextField user;
    private JPasswordField password;
    private JButton button;//登录按钮
    private String localIp;
//    public Socket client = null;
    private ServerSocket testServer;
    private boolean canlogin=false;
    private final int port[]={7273,8274,6275,9276,17277,27278};
    private int uport;
    //构造方法
    public Login(){
        super("实验室设备管理");
        Container c =this.getContentPane();

        //与数据库取得连接
        try{
            conn = GetConnection.getConnection();
        }catch(GetConnectionException e){
            e.printStackTrace();
        }
        //生成图片
        ImageIcon imageIcon=new ImageIcon(getClass().getResource("title.jpg"));
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(150, 107,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        imageIcon = new ImageIcon(newimg);  // transform it back

        //创建三个标签，其中第一个标签只用来放图片
        JLabel label[] = {new JLabel(null,imageIcon,JLabel.CENTER),new JLabel("账号"),new JLabel("密码")};
        //创建输入框
        user=new JTextField(20);
        user.setPreferredSize(new Dimension(120,25));
        password = new JPasswordField(20);
        password.setPreferredSize(new Dimension(120,25));
        //当用户输入密码后，回车登录
        password.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        //创建面板，第一个面板存放label【0】
        JPanel p[] ={new JPanel(),new JPanel()};
        JPanel p1fa=new JPanel();
        p1fa.setLayout(new BoxLayout(p1fa,BoxLayout.Y_AXIS));
        //button
        button = new JButton("登录");
        button.setPreferredSize(new Dimension(80,40));
        button.addActionListener(this);
        //
        p[0].setLayout(new FlowLayout(FlowLayout.CENTER));
        p[1].setLayout(new FlowLayout(FlowLayout.LEFT));
        //添加
        p[0].add(label[0]);
        p[1].add((label[1]));
        p[1].add(user);
        p[1].add(label[2]);
        p[1].add(password);
        p[0].setOpaque(false);
        p[1].setOpaque(false);
        p1fa.setOpaque(false);//设置透明；
        c.setBackground(new Color(135,206,231));
        p1fa.add(p[1]);
        JPanel p1 = new JPanel();
        p1.setOpaque(false);
        JPanel p2 = new JPanel();
        p2.setOpaque(false);
        p1fa.add(p2);
        p1fa.add(button);
        c.add(p[0]);
        c.add(p1fa);
        c.add(p1);//加一个空面板
        //设置布局管理器
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        setBackground(new Color(67,82,58));
        //设置位置、可见性、窗口大小以及关闭方式
        this.setSize(300,360);
        this.setLocation(new Point(400,180));
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    //实现监听器的方法
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==button){
            login();
        }
    }
    //获取本机Ip
    public void getIP(){
        try{
            // get real ip
            InetAddress localHost = null;
            try {
                localHost = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            String hostAddress = localHost.getHostAddress();
//            System.out.println("hostAddress = " + hostAddress);
            List<InetAddress> validIPs = getValidIPv4Addresses();
            System.out.println("有效的IPv4地址:");
            for (InetAddress ip : validIPs) {
//                System.out.println(ip.getHostAddress());
            }

            InetAddress bestIP = selectBestIP(validIPs);
            InetAddress addr =InetAddress.getLocalHost();
            localIp = bestIP.getHostAddress();
        }catch(Exception e){

        }

    }

    public static java.util.List<InetAddress> getValidIPv4Addresses() {
        java.util.List<InetAddress> validIPs = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                if (netint.isUp() && !netint.isLoopback()) {
                    for (InetAddress inetAddress : Collections.list(netint.getInetAddresses())) {
                        if (inetAddress instanceof Inet4Address && isValidIPv4(inetAddress)) {
                            validIPs.add(inetAddress);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("获取网络接口时出错: " + e.getMessage());
        }
        return validIPs;
    }

    private static boolean isValidIPv4(InetAddress inetAddress) {
        if (inetAddress.isLoopbackAddress() || inetAddress.isLinkLocalAddress()) {
            return false;
        }
        String ip = inetAddress.getHostAddress();
        String[] octets = ip.split("\\.");
        if (octets.length != 4) {
            return false;
        }
        for (String octet : octets) {
            int value = Integer.parseInt(octet);
            if (value < 0 || value > 255) {
                return false;
            }
        }
        return true;
    }

    public static InetAddress selectBestIP(List<InetAddress> validIPs) {
        // 优先选择非私有IP
        Optional<InetAddress> publicIP = validIPs.stream()
                .filter(ip -> !ip.isSiteLocalAddress())
                .findFirst();

        if (publicIP.isPresent()) {
            return publicIP.get();
        }

        // 如果没有公网IP，选择私有IP中的第一个
        Optional<InetAddress> privateIP = validIPs.stream()
                .filter(InetAddress::isSiteLocalAddress)
                .findFirst();

        return privateIP.orElse(null);
    }
    //run
    public void run(){

    }
    //
    public void getPort(){
        switch(uid){
            case "321":uport=port[0];
                break;
            case "111":uport=port[1];
                break;
            case "222":uport = port[2];
                break;
            case "123":uport = port[3];
                break;
            case "1342":uport = port[4];
                break;
            case "125711713":uport = port[5];
                break;

        }
    }
    //登录功能
    public void login(){
////////////!!!!!!!!!!!!!!!!!!!!!!!!!!
        try{
            //分配端口号
            uid = user.getText();
            pass = new String(password.getPassword());
            getPort();
            testServer = new ServerSocket(uport);//书上代码
            getIP();
            canlogin = true;
            testServer.close();
        }catch(IOException e){
            canlogin = false;
        }
        if(!canlogin){
            JOptionPane.showMessageDialog(null,"您已登录！");
        }
        else {
            try {
                if (uid.equals("") || pass.equals("")) {
                    throw new LoginException();
                } else {
                    String sql = "select * from user";
                    // fix error: java.sql.SQLException: Operation not allowed for a result set of type ResultSet.TYPE_FORWARD_ONLY.
                    PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs = pstmt.executeQuery();

                    rs.last();
                    int r = rs.getRow();
                    int c = users.length;
                    Object data[][] = Table.readData(r, c, rs);
                    int i;
                    for (i = 0; i < data.length; i++) {
                        if (uid.equals(data[i][0]) && pass.equals(data[i][1]) && canlogin) {
                            break;
                        }
                    }
                    if (i >= data.length) {
                        JOptionPane.showMessageDialog(null, "账号或密码错误");
                    } else {
                        //登录成功
//                        try{
//                            InetAddress address = InetAddress.getByName("localhost");
//                            localIp = address.getHostAddress();
//                            System.out.println(localIp);
//                        }catch(Exception exc){
//                            exc.printStackTrace();
//                        }
                        this.setVisible(false);
                        Thread t = new Thread(new Manger(conn, data, i,uid,localIp));
                        t.start();
                        return;
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        Thread t =  new Thread(new Login());
        t.start();
    }

}
