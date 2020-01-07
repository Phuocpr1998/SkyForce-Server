package skyforce.entry;

import com.google.gson.annotations.Expose;

public class User {
    @Expose
    private int id;
    @Expose
    private String name;
    @Expose
    private boolean ready;

    public static int UserCount = 0;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
        this.ready = false;
        UserCount++;
    }

    public int getUuid() {
        return id;
    }

    public void setUuid(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
