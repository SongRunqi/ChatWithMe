package exception;
import javax.swing.JOptionPane;
public class NonactivatedException extends Exception {
    public NonactivatedException(){
        JOptionPane.showMessageDialog(null,"账户未激活！");
    }
}
