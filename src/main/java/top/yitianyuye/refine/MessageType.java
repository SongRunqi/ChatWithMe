package top.yitianyuye.refine;

/**
 * @author Logan
 * @since 3/27/25 22:53
 * message types, decide by the beginning 4 bytes
 **/
public enum MessageType {
    TEXT(new byte[]{0, 0, 0, 0}),
    IMAGE(new byte[]{0, 0, 0, 1});

    private final byte[] code;
    public byte[] getBytes() {
        return code;
    }
    MessageType(byte[] code) {
        this.code = code;
    }
}
