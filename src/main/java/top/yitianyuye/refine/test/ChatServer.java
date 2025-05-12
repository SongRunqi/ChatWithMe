package top.yitianyuye.refine.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

/**
 * @author Logan
 * @since 3/27/25 17:56
 **/
public class ChatServer {
    public static void main(String[] args) {
        new ChatServer().startServer();
        System.out.println("server stopped");
    }

    private void startServer() {
        try(ServerSocket server = new ServerSocket(8080)) {
            System.out.println("chat server started, waiting for connected");
            server.accept();
            System.out.println("client connected");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(OutputStream opt, byte[] bytes) {
        try {
            opt.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
