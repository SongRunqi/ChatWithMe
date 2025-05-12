package top.yitianyuye.refine.test;

import top.yitianyuye.refine.ChatClient;
import top.yitianyuye.refine.MessageAgent;
import top.yitianyuye.refine.MessageType;

import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author Logan
 * @since 3/27/25 21:56
 * Role for send and receive data for a socket
 **/
public class ClientAgent {

    private Socket client;

    private MessageAgent messageAgent;

    private ChatClient chatClient;

    public ClientAgent(SocketAddress endpoint, ChatClient chatClient) {
        try {
            client = new Socket();
            client.connect(endpoint);
//            messageAgent = new MessageAgent(client.getInputStream(), client.getOutputStream(), client);
            this.chatClient = chatClient;
        } catch (Exception e) {
            System.out.println("[client service] can not connected to the endpoint" + endpoint);
            System.out.println("[client service] error is");
            e.printStackTrace();
        }
    }

    public void sendText(MessageType messageType, byte[] message) {

    }


}
