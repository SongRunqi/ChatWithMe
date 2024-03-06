package exception;
import javax.swing.*;

public class GetConnectionException extends Exception{
    public GetConnectionException(){
        super();
        JOptionPane.showMessageDialog(null,"连接数据库失败");
    }
}
