package top.yitianyuye.refine;

import java.util.List;

/**
 * @author Logan
 * @since 3/29/25 16:07
 **/
public class User {
    private String uid;
    private String ip;
    private int port;
    private JOINNER joinner;
    private MessageAgent messageAgent;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public JOINNER getJoinner() {
        return joinner;
    }

    public void setJoinner(JOINNER joinner) {
        this.joinner = joinner;
    }

    public MessageAgent getMessageAgent() {
        return messageAgent;
    }

    public void setMessageAgent(MessageAgent messageAgent) {
        this.messageAgent = messageAgent;
    }
}
