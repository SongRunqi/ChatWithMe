package top.yitianyuye.chatWithMe.exception;
import javax.swing.JOptionPane;
public class LoginException extends Exception {
    public LoginException(){
        JOptionPane.showMessageDialog(null,"请输入你的账号后再登录");
    }
}
