package top.yitianyuye.refine.terminal;

import top.yitianyuye.refine.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TerminalChatClient implements ChatClient, Runnable {
    private volatile boolean isRunning = true;
    private volatile boolean hasSession = false;
    private User owner;
    private final Map<String, User> connections = new ConcurrentHashMap<>();
    private final Map<String, MessageAgent> messageAgents = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private Thread serverThread;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private MessageAgent currentChatAgent;
    private User currentChatUser;
    private final int DEFAULT_PORT = 8888;

    @Override
    public void start(User user) {
        this.owner = user;
        startDefaultServer();

        Scanner scanner = new Scanner(System.in);
        printCommands();

        while (isRunning) {
            try {
                String input = scanner.nextLine().trim();

                if (hasSession) {
                    // In chat session
                    if (input.equals("/exit")) {
                        hasSession = false;
                        System.out.println("Exited chat session");
                        printCommands();
                    } else if (!input.isEmpty()) {
                        // Send message
                        if (currentChatAgent != null) {
                            currentChatAgent.sendText(input);
                            System.out.println("Me: " + input);
                        }
                    }
                } else {
                    // Command mode
                    handleCommand(input, scanner);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printCommands() {
        System.out.println("\n=== Chat Client Commands ===");
        System.out.println("shutdown - Shutdown the chat client");
        System.out.println("connect  - Connect to a new friend");
        System.out.println("list     - List all connections");
        System.out.println("chat     - Choose a connection and start chatting");
        System.out.println("============================\n");
    }

    private void handleCommand(String command, Scanner scanner) {
        switch (command) {
            case "shutdown" -> shutdown();
            case "connect" -> connect();
            case "list" -> listConnections();
            case "chat" -> startChatSession(scanner);
            default -> System.out.println("Unknown command: " + command);
        }
    }

    private void startChatSession(Scanner scanner) {
        if (connections.isEmpty()) {
            System.out.println("No connections available. Use 'connect' first.");
            return;
        }

        System.out.println("Select a user to chat with:");
        List<String> userIds = new ArrayList<>(connections.keySet());
        for (int i = 0; i < userIds.size(); i++) {
            System.out.println((i + 1) + ". " + userIds.get(i));
        }

        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice >= 0 && choice < userIds.size()) {
                String userId = userIds.get(choice);
                currentChatUser = connections.get(userId);
                currentChatAgent = messageAgents.get(userId);
                hasSession = true;
                System.out.println("Started chat with " + userId);
                System.out.println("Type '/exit' to exit chat session");
            } else {
                System.out.println("Invalid selection");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
        }
    }

    @Override
    public void sendAction(User user, MessageType messageType, byte[] message) {
        MessageAgent agent = messageAgents.get(user.getUid());
        if (agent != null) {
            try {
                if (messageType == MessageType.TEXT) {
                    agent.sendText(new String(message));
                }
            } catch (IOException e) {
                System.err.println("Failed to send message: " + e.getMessage());
            }
        }
    }

    @Override
    public void showTextMessage(User user, JOINNER joinner, String message, boolean isStart, boolean isEnd) {
        if (hasSession && user.equals(currentChatUser)) {
            // In active chat session
            if (isStart) {
                System.out.print("\n" + joinner + ": ");
            }
            System.out.print(message);
            if (isEnd) {
                System.out.println();
            }
        } else {
            // Not in active session, just notify
            System.out.println("\n[New message from " + user.getUid() + "]: " + message);
        }
    }

    @Override
    public void shutdown() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (MessageAgent agent : messageAgents.values()) {
                agent.close();
            }
            executor.shutdown();
            System.out.println("Chat client shut down");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter IP address: ");
        String ip = scanner.nextLine().trim();
        System.out.print("Enter port (default 8888): ");
        String portStr = scanner.nextLine().trim();
        int port = portStr.isEmpty() ? DEFAULT_PORT : Integer.parseInt(portStr);

        try {
            Socket socket = new Socket(ip, port);
            handleNewConnection(socket, true);
            System.out.println("Connected to " + ip + ":" + port);
        } catch (IOException e) {
            System.out.println("Failed to connect: " + e.getMessage());
        }
    }

    @Override
    public void disconnect(User user) {
        MessageAgent agent = messageAgents.remove(user.getUid());
        if (agent != null) {
            try {
                agent.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connections.remove(user.getUid());
        System.out.println("Disconnected from " + user.getUid());
    }

    @Override
    public void startDefaultServer() {
        serverThread = new Thread(this);
        serverThread.start();
    }

    @Override
    public void listConnections() {
        if (connections.isEmpty()) {
            System.out.println("No active connections");
        } else {
            System.out.println("Active connections:");
            for (User user : connections.values()) {
                System.out.println("- " + user.getUid() + " (" + user.getIp() + ":" + user.getPort() + ")");
            }
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = createServer();
            System.out.println("Server started on port " + serverSocket.getLocalPort());

            while (isRunning) {
                try {
                    Socket client = serverSocket.accept();
                    executor.execute(() -> handleNewConnection(client, false));
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting connection: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void handleNewConnection(Socket socket, boolean isOutgoing) {
        try {
            String userId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
            User user = new User();
            user.setUid(userId);
            user.setIp(socket.getInetAddress().getHostAddress());
            user.setPort(socket.getPort());
            user.setJoinner(isOutgoing ? JOINNER.YOU : JOINNER.ME);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            MessageAgent agent = new MessageAgent(in, out, this, user);

            connections.put(userId, user);
            messageAgents.put(userId, agent);
            user.setMessageAgent(agent);

            System.out.println("New connection established: " + userId);
        } catch (IOException e) {
            System.err.println("Failed to handle connection: " + e.getMessage());
        }
    }

    @Override
    public ServerSocket createServer() {
        int port = DEFAULT_PORT;
        int attempts = 0;

        while (attempts < 10) {
            try {
                return new ServerSocket(port);
            } catch (IOException e) {
                port++;
                attempts++;
            }
        }
        throw new RuntimeException("Could not create server socket");
    }


    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   Welcome to Terminal Chat App  ");
        System.out.println("=================================\n");

        // Create and configure the current user
        User currentUser = createUser();

        // Create and start the chat client
        TerminalChatClient chatClient = new TerminalChatClient();

        try {
            // Start the chat client with the current user
            chatClient.start(currentUser);
        } catch (Exception e) {
            System.err.println("Failed to start chat client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static User createUser() {
        Scanner scanner = new Scanner(System.in);
        User user = new User();

        // Get username
        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            username = "User" + System.currentTimeMillis();
        }

        // Set user properties
        user.setUid(username);
        user.setJoinner(JOINNER.ME);

        // Get local IP (simplified - in production you'd want better IP detection)
        try {
            String localIp = java.net.InetAddress.getLocalHost().getHostAddress();
            user.setIp(localIp);
        } catch (Exception e) {
            user.setIp("127.0.0.1");
        }

        System.out.println("Logged in as: " + username);
        System.out.println("Your IP: " + user.getIp());
        System.out.println();

        return user;
    }
}