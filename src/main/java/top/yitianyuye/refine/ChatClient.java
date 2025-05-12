package top.yitianyuye.refine;


import java.net.ServerSocket;

/**
 * @author Logan
 * @since 3/27/25 20:38
 **/
public interface ChatClient {
    /**
     * 启动聊天
     * @param user
     */
    void start(User user);

    /**
     * 发送消息的方法，根据messageType，实现类可以决定可以发送哪些消息类型
     * @param user 接收消息的用户
     * @param messageType 消息类型
     * @param message 消息字节内容
     */
    void sendAction(User user, MessageType messageType, byte[] message);

    /**
     * 接收和展示文本消息
     * @param user 被展示消息的用户
     * @param joinner 身份
     * @param message 消息
     * @param isStart 是不是首次传输
     * @param isEnd 是否是最后一次传输(一条消息手都到达结尾）
     */
    void showTextMessage(User user, JOINNER joinner, String message,boolean isStart, boolean isEnd);

    /**
     * 关闭客户端
     */
    void shutdown();

    /**
     * 与ip建联socket连接
     */
    void connect();


    /**
     * 取消与某个用户的连接连接
     */
    void disconnect(User user);

    /**
     * 启动serversocket服务，端口默认为8888，负责接收连接请求
     *
     */
    void startDefaultServer();

    /**
     * 展示连接
     */
    void listConnections();

    /**
     * 创建serversocket等待客户端连接
     * @return
     */
    ServerSocket createServer();
}
