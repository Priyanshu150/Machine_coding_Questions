import java.util.*;

class User {
    private final int userId;
    private final String name;
    private final String contact; 

    public User(int userId, String name, String contact) {
        this.userId = userId;
        this.name = name;
        this.contact = contact;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }
}