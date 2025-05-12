package top.yitianyuye.refine;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Logan
 * @since 3/27/25 22:18
 * The role for sending and receiving data regardless of client or server
 **/
public class MessageAgent implements Runnable, Closeable {
    private final User user;
    private final InputStream input;
    private final OutputStream output;
    private final ChatClient chatClient;
    private final int commandOcupiedLength = 4;
    private final int messageBodyLengthDescribe = 32;
    private final int oneTimeReadBytes = 1024;
    private final Thread receiveThread;
    private boolean starting = true;
    private ChatHistory chatHistory;


    public MessageAgent(InputStream in, OutputStream out, ChatClient chatClient,  User user) {
        input = in;
        output = out;
        this.chatClient = chatClient;
        this.user = user;
        receiveThread = new Thread(this);
        receiveThread.start();
    }


    /**
     * 重新启动接收消息线程
     * @return 线程启动成功true；失败false
     */
    public boolean starting() {
        if (!starting) {
            starting = true;
            receiveThread.start();
            return true;
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
        if (receiveThread != null) {
            starting = false;
        }
    }

    public void sendText(String text) throws IOException{
        output.write(MessageType.TEXT.getBytes());
        output.write(text.getBytes());
        output.flush();
    }

    /**
     * 取message的前四个比特作为消息类型
     * @param message 4 bytes
     * @return 消息类型
     */
    private MessageType judgeMessageType(byte[] message) {
        byte[] command = Arrays.copyOfRange(message, 0, 4);
        if (Arrays.equals(command, MessageType.TEXT.getBytes())) {
            return MessageType.TEXT;
        }

        if (Arrays.equals(command, MessageType.IMAGE.getBytes())) {
            return MessageType.IMAGE;
        }

        return null;
    }

    /**
     * 获取消息体的长度
     * @param message
     * @return
     */
    private Long getMessageBodyLength(byte[] message) {
        byte[] lengthBytes = Arrays.copyOfRange(message, 0, messageBodyLengthDescribe);
        //
        ByteBuffer bf = ByteBuffer.wrap(lengthBytes);
        return bf.getLong();
    }

    /**
     * receive message method
     */
    @Override
    public void run() {
        while(starting) {
            byte[] command = new byte[commandOcupiedLength];
            try {
                // judge command to specify message type
                int readCommandLength = input.read(command);
                if (readCommandLength != commandOcupiedLength) {
                    continue;
                }
                MessageType messageType = judgeMessageType(command);
                if (messageType == null) {
                    continue;
                }
                // recognize message body length
                byte[] messageBodyLength = new byte[messageBodyLengthDescribe];
                long messagLength = getMessageBodyLength(messageBodyLength);

                // read data
                ByteBuffer bf = ByteBuffer.allocate(oneTimeReadBytes);
                long start = 0;
                while(start < messagLength) {
                    byte[] bytes = input.readNBytes(oneTimeReadBytes);
                    bf.put(bytes);
                    switch(messageType) {
                        case TEXT -> {
                            String messageFromBytes = new String(bytes, StandardCharsets.UTF_8);
                            chatHistory.put(user, JOINNER.YOU, messageFromBytes);
                            chatClient.showTextMessage(user, JOINNER.YOU, messageFromBytes, start == 0, start < oneTimeReadBytes);
                        }
                        case IMAGE -> System.out.println("not supported");
                    }
                    start+=oneTimeReadBytes;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
