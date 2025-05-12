package top.yitianyuye.refine;


/**
 * @author Logan
 * @since 3/29/25 11:04
 **/
public class Message {
    private JOINNER joinner;
    private String message;

    public static Message build() {
        return new Message();
    }

    public Message joinner(JOINNER joinner) {
        setJoinner(joinner);
        return this;
    }

    public Message message(String message) {
        setMessage(message);
        return this;
    }

    public JOINNER getJoinner() {
        return joinner;
    }

    public void setJoinner(JOINNER joinner) {
        this.joinner = joinner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
