import java.util.*;

public class RefurbHubApp {
    private final Scanner in = new Scanner(System.in);
    private final UserStore userStore = new UserStore();
    private final ProductStore productStore = new ProductStore();
    private User currentUser = null;

    public static void main(String[] args) {
        new RefurbHubApp().run();
    }

    private void run() {
        System.out.println("=== RefurbHub (OLX-style) ===");
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

    // Guest Menu
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

    // Buyer Menu
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

    // Seller Menu
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

    // Admin Menu
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

    // Actions
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

    // Helpers
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
