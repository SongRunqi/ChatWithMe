package top.yitianyuye.refine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Logan
 * @since 3/30/25 09:51
 **/
public class ChatHistory {
    private List<Message> data;
    private Map<User, List<Message>> userData = new ConcurrentHashMap<>();

    public List<Message> getData() {
        return data;
    }

    public void setData(List<Message> data) {
        this.data = data;
    }

    public void put(User user, JOINNER joinner, String message) {
        Message msg = Message.build().message(message).joinner(joinner);
        List<Message> specifiedUserMessages = userData.computeIfAbsent(user, v -> new ArrayList<>());
        specifiedUserMessages.add(msg);
    }
}
