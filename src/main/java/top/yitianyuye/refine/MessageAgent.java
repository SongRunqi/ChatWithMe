package top.yitianyuye.refine;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageAgent implements Runnable, Closeable {
    private final User user;
    private final InputStream input;
    private final OutputStream output;
    private final ChatClient chatClient;
    private final int COMMAND_LENGTH = 4;
    private final int MESSAGE_LENGTH_BYTES = 4; // Changed from 32 to 4 for int
    private final int BUFFER_SIZE = 1024;
    private final Thread receiveThread;
    private volatile boolean running = true;
    private ChatHistory chatHistory;

    public MessageAgent(InputStream in, OutputStream out, ChatClient chatClient, User user) {
        this.input = in;
        this.output = out;
        this.chatClient = chatClient;
        this.user = user;
        this.chatHistory = new ChatHistory(); // Initialize chatHistory
        this.receiveThread = new Thread(this);
        this.receiveThread.start();
    }

    @Override
    public void close() throws IOException {
        running = false;
        if (receiveThread != null) {
            receiveThread.interrupt();
        }
        input.close();
        output.close();
    }

    public void sendText(String text) throws IOException {
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(COMMAND_LENGTH + MESSAGE_LENGTH_BYTES + textBytes.length);

        // Write command
        buffer.put(MessageType.TEXT.getBytes());

        // Write message length
        buffer.putInt(textBytes.length);

        // Write message
        buffer.put(textBytes);

        output.write(buffer.array());
        output.flush();
    }

    private MessageType judgeMessageType(byte[] command) {
        if (Arrays.equals(command, MessageType.TEXT.getBytes())) {
            return MessageType.TEXT;
        }
        if (Arrays.equals(command, MessageType.IMAGE.getBytes())) {
            return MessageType.IMAGE;
        }
        return null;
    }

    private int getMessageBodyLength(byte[] lengthBytes) {
        return ByteBuffer.wrap(lengthBytes).getInt();
    }

    @Override
    public void run() {
        try {
            while (running) {
                // Read command
                byte[] command = new byte[COMMAND_LENGTH];
                int bytesRead = readFully(input, command);
                if (bytesRead < COMMAND_LENGTH) {
                    break; // Connection closed
                }

                MessageType messageType = judgeMessageType(command);
                if (messageType == null) {
                    continue;
                }

                // Read message length
                byte[] lengthBytes = new byte[MESSAGE_LENGTH_BYTES];
                bytesRead = readFully(input, lengthBytes);
                if (bytesRead < MESSAGE_LENGTH_BYTES) {
                    break;
                }

                int messageLength = getMessageBodyLength(lengthBytes);
                if (messageLength <= 0 || messageLength > 1024 * 1024) { // Max 1MB
                    continue;
                }

                // Read message body
                byte[] messageBody = new byte[messageLength];
                bytesRead = readFully(input, messageBody);
                if (bytesRead < messageLength) {
                    break;
                }

                // Process message
                switch (messageType) {
                    case TEXT -> {
                        String message = new String(messageBody, StandardCharsets.UTF_8);
                        chatHistory.put(user, JOINNER.YOU, message);
                        chatClient.showTextMessage(user, JOINNER.YOU, message, true, true);
                    }
                    case IMAGE -> System.out.println("Image messages not yet supported");
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int readFully(InputStream in, byte[] buffer) throws IOException {
        int totalRead = 0;
        while (totalRead < buffer.length) {
            int read = in.read(buffer, totalRead, buffer.length - totalRead);
            if (read == -1) {
                return totalRead;
            }
            totalRead += read;
        }
        return totalRead;
    }
}