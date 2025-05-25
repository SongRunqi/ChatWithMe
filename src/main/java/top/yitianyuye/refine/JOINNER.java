package top.yitianyuye.refine;

public enum JOINNER {
    ME("Me"),
    YOU("You"),
    SYSTEM("System");

    private final String displayName;

    JOINNER(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}