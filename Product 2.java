public class Product {
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
