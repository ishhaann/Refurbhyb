import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// ===== ENUM: Role =====
enum Role {
    BUYER, SELLER, ADMIN
}

// ===== CLASS: User =====
class User {
    private final int id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private String password;

    public User(int id, String name, String email, String phone, Role role, String password) {
        this.id = id;
        this.name = name;
        this.email = email.toLowerCase();
        this.phone = phone;
        this.role = role;
        this.password = password;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', phone='" + phone + "', role=" + role + "}";
    }
}

// ===== CLASS: Product =====
class Product {
    private final int id;
    private String name;
    private String description;
    private double price;
    private String condition;
    private String category;
    private int sellerUserId;
    private String sellerContact;

    public Product(int id, String name, String description, double price, String condition,
                   String category, int sellerUserId, String sellerContact) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.condition = condition;
        this.category = category;
        this.sellerUserId = sellerUserId;
        this.sellerContact = sellerContact;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCondition() { return condition; }
    public String getCategory() { return category; }
    public String getSellerContact() { return sellerContact; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price +
                ", condition='" + condition + "', category='" + category + "'}";
    }
}

// ===== CLASS: UserStore =====
class UserStore {
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

// ===== CLASS: ProductStore =====
class ProductStore {
    private final List<Product> products = new ArrayList<>();
    private final AtomicInteger idSeq = new AtomicInteger(1001);

    public ProductStore() {
        // Seed demo products
        add("iPhone 12", "64GB, minimal scratches", 28000, "Good", "Phone", 2, "alice@refurbhub.com / 8888888888");
        add("Dell Latitude 7490", "i5, 16GB RAM, 512GB SSD", 25000, "Very Good", "Laptop", 2, "alice@refurbhub.com / 8888888888");
    }

    public Product add(String name, String desc, double price, String condition, String category,
                       int sellerUserId, String sellerContact) {
        Product p = new Product(idSeq.getAndIncrement(), name, desc, price, condition, category, sellerUserId, sellerContact);
        products.add(p);
        return p;
    }

    public boolean removeById(int id, int requesterUserId, boolean isAdmin) {
        return products.removeIf(p -> p.getId() == id && (isAdmin || p.toString().contains("sellerUserId=" + requesterUserId)));
    }

    public List<Product> all() { return products; }

    public List<Product> search(String keyword) {
        String q = keyword.toLowerCase();
        return products.stream().filter(p ->
                p.getName().toLowerCase().contains(q) ||
                p.getCategory().toLowerCase().contains(q) ||
                p.toString().toLowerCase().contains(q)
        ).collect(Collectors.toList());
    }

    public Product findById(int id) {
        for (Product p : products) if (p.getId() == id) return p;
        return null;
    }
}

// ===== MAIN APP =====
public class RefurbHubApp {
    private final Scanner in = new Scanner(System.in);
    private final UserStore userStore = new UserStore();
    private final ProductStore productStore = new ProductStore();
    private User currentUser = null;

    public static void main(String[] args) {
        new RefurbHubApp().run();
    }

    private void run() {
        System.out.println("=== RefurbHub  ===");
        boolean running = true;
        while (running) {
            if (currentUser == null) running = guestMenu();
            else {
                switch (currentUser.getRole()) {
                    case BUYER -> buyerMenu();
                    case SELLER -> sellerMenu();
                    case ADMIN -> adminMenu();
                }
            }
        }
        System.out.println("Goodbye!");
    }

    // --- Menus ---
    private boolean guestMenu() {
        System.out.println("\n-- Guest Menu --");
        System.out.println("1) Register");
        System.out.println("2) Login");
        System.out.println("3) Browse Products");
        System.out.println("4) Search Products");
        System.out.println("0) Exit");
        int ch = askInt("Choose: ");
        switch (ch) {
            case 1 -> doRegister();
            case 2 -> doLogin();
            case 3 -> showProducts(productStore.all());
            case 4 -> doSearch();
            case 0 -> { return false; }
            default -> System.out.println("Invalid choice.");
        }
        return true;
    }

    private void buyerMenu() {
        System.out.println("\n-- Buyer Menu (" + currentUser.getName() + ") --");
        System.out.println("1) Browse all products");
        System.out.println("2) Search products");
        System.out.println("3) Get seller contact by Product ID");
        System.out.println("9) Logout");
        int ch = askInt("Choose: ");
        switch (ch) {
            case 1 -> showProducts(productStore.all());
            case 2 -> doSearch();
            case 3 -> getContact();
            case 9 -> logout();
        }
    }

    private void sellerMenu() {
        System.out.println("\n-- Seller Menu (" + currentUser.getName() + ") --");
        System.out.println("1) Add product");
        System.out.println("2) View my listings");
        System.out.println("3) Remove product by ID");
        System.out.println("9) Logout");
        int ch = askInt("Choose: ");
        switch (ch) {
            case 1 -> addProduct();
            case 2 -> showProducts(productStore.all());
            case 3 -> removeProduct(false);
            case 9 -> logout();
        }
    }

    private void adminMenu() {
        System.out.println("\n-- Admin Menu (" + currentUser.getName() + ") --");
        System.out.println("1) View all users");
        System.out.println("2) View all products");
        System.out.println("3) Remove ANY product");
        System.out.println("9) Logout");
        int ch = askInt("Choose: ");
        switch (ch) {
            case 1 -> userStore.all().forEach(System.out::println);
            case 2 -> showProducts(productStore.all());
            case 3 -> removeProduct(true);
            case 9 -> logout();
        }
    }

    // --- Actions ---
    private void doRegister() {
        String name = ask("Name: ");
        String email = ask("Email: ");
        String phone = ask("Phone: ");
        System.out.println("Role (1=BUYER, 2=SELLER): ");
        int r = askInt("");
        Role role = (r == 2) ? Role.SELLER : Role.BUYER;
        String pw = ask("Password: ");
        User u = userStore.register(name, email, phone, role, pw);
        if (u == null) System.out.println("Email already exists.");
        else System.out.println("Registered successfully.");
    }

    private void doLogin() {
        String email = ask("Email: ");
        String pw = ask("Password: ");
        User u = userStore.login(email, pw);
        if (u != null) {
            currentUser = u;
            System.out.println("Welcome, " + u.getName() + " (" + u.getRole() + ")");
        } else System.out.println("Invalid credentials.");
    }

    private void logout() { currentUser = null; }

    private void addProduct() {
        String name = ask("Name: ");
        String desc = ask("Description: ");
        double price = askDouble("Price: ");
        String condition = ask("Condition: ");
        String category = ask("Category: ");
        String contact = ask("Contact (email/phone): ");
        Product p = productStore.add(name, desc, price, condition, category, currentUser.getId(), contact);
        System.out.println("Product added with ID " + p.getId());
    }

    private void removeProduct(boolean admin) {
        int id = askInt("Enter product ID: ");
        boolean ok = productStore.removeById(id, currentUser.getId(), admin);
        System.out.println(ok ? "Removed." : "Not found.");
    }

    private void doSearch() {
        String q = ask("Search keyword: ");
        showProducts(productStore.search(q));
    }

    private void getContact() {
        int id = askInt("Enter Product ID: ");
        Product p = productStore.findById(id);
        if (p != null) System.out.println("Seller contact: " + p.getSellerContact());
        else System.out.println("Not found.");
    }

    private void showProducts(List<Product> list) {
        System.out.println("\n--- Products ---");
        if (list.isEmpty()) { System.out.println("(none)"); return; }
        for (Product p : list) {
            System.out.println("ID:" + p.getId() + " | " + p.getName() + " | ₹" + p.getPrice() +
                    " | " + p.getCondition() + " | " + p.getCategory() +
                    " | Contact: " + p.getSellerContact());
        }
    }

    // --- Helpers ---
    private String ask(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    private int askInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(ask(prompt)); }
            catch (Exception e) { System.out.println("Enter valid number."); }
        }
    }

    private double askDouble(String prompt) {
        while (true) {
            try { return Double.parseDouble(ask(prompt)); }
            catch (Exception e) { System.out.println("Enter valid number."); }
        }
    }
}

