import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UserStore {
    private final List<User> users = new ArrayList<>();
    private final AtomicInteger idSeq = new AtomicInteger(1);

    public UserStore() {
        // Default demo users
        register("Admin", "admin@refurbhub.com", "9999999999", Role.ADMIN, "admin123");
        register("Alice Seller", "alice@refurbhub.com", "8888888888", Role.SELLER, "alice123");
        register("Bob Buyer", "bob@refurbhub.com", "7777777777", Role.BUYER, "bob123");
    }

    public User register(String name, String email, String phone, Role role, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) return null;
        }
        User u = new User(idSeq.getAndIncrement(), name, email, phone, role, password);
        users.add(u);
        return u;
    }

    public User login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public List<User> all() { return users; }
}
