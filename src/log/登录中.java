package log;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class 登录中 extends JFrame implements Runnable{
    private ServerSocket serverSocket;
    public Socket client=null;
    JTextArea chatArea = new JTextArea();
    JTextArea sendArea = new JTextArea();
    public 登录中(){
        super("");
        this.setSize(new Dimension(800,400));
        this.setVisible(true);
        Container c=this.getContentPane();
        c.setLayout(new GridLayout());

        JButton b = new JButton("send");
        JButton se = new JButton("服务");
        chatArea.setPreferredSize(new Dimension(400,400));
        sendArea.setPreferredSize(new Dimension(400,200));
        JPanel p=new JPanel();
        JPanel center = new JPanel();
        center.setLayout(new FlowLayout());
        p.setLayout(new GridLayout(3,1));
        center.add(b);
        center.add(se);
        p.add(chatArea);
        p.add(center);
        p.add(sendArea);
        se.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    serverSocket = new ServerSocket(7773);
                    client = serverSocket.accept();
                    while(true){
                        while(client!=null){
                            InputStreamReader in = new InputStreamReader(client.getInputStream());
                            BufferedReader bw = new BufferedReader(in);
                            chatArea.setText(bw.readLine());
                            bw.close();
                            in.close();
                        }
                    }
                }catch(IOException e3){
                    e3.printStackTrace();
                }
            }
        });
        b.addActionListener(e -> link());
        c.add("west",new JPanel());
        c.add("Center",p);

    }
    public void link(){
        try{
            client = new Socket("127.0.0.1",7773);
            OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream());
            BufferedWriter bw = new BufferedWriter(out);
            bw.write(sendArea.getText());
//            bw.close();
//            out.close();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void run(){
        try{
            serverSocket = new ServerSocket(6767);
            while(true){
                while(client!=null){
                    InputStreamReader in = new InputStreamReader(client.getInputStream());
                    BufferedReader bw = new BufferedReader(in);
                    chatArea.setText(bw.readLine());
                }
            }
        }catch(IOException e){

        }
    }
    public void chatTo(){

    }
    public static void main(String[] args){
        new 登录中();
    }
}
