import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ProductStore {
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
        return products.removeIf(p -> p.getId() == id && (isAdmin || requesterUserId == p.sellerUserId));
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
