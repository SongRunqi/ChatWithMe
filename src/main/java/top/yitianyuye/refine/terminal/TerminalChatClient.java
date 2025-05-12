package top.yitianyuye.refine.terminal;

import top.yitianyuye.refine.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Logan
 * @since 3/29/25 10:48
 **/
public class TerminalChatClient implements ChatClient, Runnable {
    private final String commandPrefix = "@";
    private boolean hasSession;
    private boolean isRunning;
    @SuppressWarnings("all")
    private final List<Integer> alreadyUsed = new ArrayList<>();

    @SuppressWarnings("all")
    private final Integer maxTriedTimes = 3;
    // 与用户发送聊天的server
    private volatile Integer currentServerConnections = 0;
    // 接收用户请求的server
    private volatile  Integer serverConnections;

    private User owner;

    @Override
    public void start(User user) {
        String[] command = new String[]{"shutdown", "connect", "list", "chat"};
        String[] commandDesc = new String[] {"shutdown the chat client", "connect to a new friend", "list all the connections", "chose a connection and go chat"};
        Set<String> commandSet = Arrays.stream(command).collect(Collectors.toSet());
        int commandCount = command.length;
        while (isRunning) {
            if (!hasSession) {
                // list command
                int i = 0;
                while (i++ < commandCount) {
                    System.out.println(command[i] + ": " + commandDesc[i]);
                }
            }
            Scanner scanner = new Scanner(System.in);
            // waiting for user operations
            String userInputCommand;
            try {
                userInputCommand = scanner.nextLine();

                String trimCommand = userInputCommand.trim();
                switch (trimCommand) {
                    case "shutdown":
                        shutdown();
                        break;
                    case "connect":
                        connect();
                        break;
                    case "list":
                        listConnections();
                        break;
                    case "chat":
                        break;

                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e);
            }

        }
    }

    @Override
    public void sendAction(User user, MessageType messageType, byte[] message) {

    }

    @Override
    public void showTextMessage(User user, JOINNER joinner, String message, boolean isStart, boolean isEnd) {
        Message chatMessage = Message.build().joinner(joinner).message(message);
        if (isStart) {
            System.out.print(joinner.toString() + " : ");
        }

        if (!isEnd) {
            System.out.print(message);
        } else {
            System.out.println(message);
        }

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect(User user) {

    }

    @Override
    public void startDefaultServer() {


    }

    @Override
    public void listConnections() {

    }

    /**
     * 接收其他客户端的连接请求，使用原有的或创建新的serversocket
     */
    @Override
    public void run() {
        int connections;
        // 接收
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            while (isRunning) {
                Socket client = serverSocket.accept();
                Runnable handleConnectRequestRunnable = () -> {
                    try {
                        InputStream inputStream = client.getInputStream();
                        OutputStream outputStream = client.getOutputStream();
                        MessageAgent messageAgent = new MessageAgent(inputStream, outputStream, this,  null);
                        // 启动接收消息的线程
                        messageAgent.run();



                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                };




            }

        }catch (Exception e) {

        }

    }

    @Override
    public ServerSocket createServer() {
        int tried = 0;
        // 创建serversocket
        while (tried ++ < maxTriedTimes) {
            int serverPortToUse = alreadyUsed.getLast() + 1;
            try {
                ServerSocket serverSocket = new ServerSocket(serverPortToUse);
                break;
            } catch (Exception e) {

            }
        }

        //

        return null;
    }
}
