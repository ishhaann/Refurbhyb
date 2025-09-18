package refurb;

import java.sql.*;
import java.util.*;

public class Database implements AutoCloseable {
    Connection conn;

    Database(String url) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url);
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    public User register(String name, String email, String phone, Role role, String password) {
        String checkSql = "SELECT 1 FROM User WHERE email = ?";
        String insertSql = "INSERT INTO User (name, email, phone, role, password) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, email);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return null;
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.setString(3, phone);
                insertStmt.setString(4, role == Role.SELLER ? "SELLER" : "BUYER");
                insertStmt.setString(5, password);

                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows == 0)
                    return null;

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        User u = new User(generatedKeys.getInt(1), name, email, phone, role, password);
                        return u;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
        }

        return null;
    }

    public User login(String email, String password) {
        String sql = "SELECT * FROM User WHERE email=? AND password=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            (rs.getString("role") == "SELLER") ? Role.SELLER : Role.BUYER,
                            rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
        }

        return null;
    }

    public List<User> allUsers() {
        String sql = "SELECT * FROM User";
        List<User> users = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            (rs.getString("role") == "SELLER") ? Role.SELLER : Role.BUYER,
                            rs.getString("password")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
        }

        return users;
    }

    public Product addProduct(String name, String desc, double price, String condition, String category,
            int sellerUserId, String sellerContact) {
        String Sql = "INSERT INTO Product (name, description, price, product_condition, category, sellerUserId, sellerContact) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertStmt = conn.prepareStatement(Sql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, name);
            insertStmt.setString(2, desc);
            insertStmt.setDouble(3, price);
            insertStmt.setString(4, condition);
            insertStmt.setString(5, category);
            insertStmt.setInt(6, sellerUserId);
            insertStmt.setString(7, sellerContact);

            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows == 0)
                return null;

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Product p = new Product(generatedKeys.getInt(1), name, desc, price, condition, category,
                            sellerUserId, sellerContact);
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
        }

        return null;
    }

    public boolean removeProduct(int id, int requesterUserId, boolean isAdmin) {
        String Sql = "DELETE FROM Product WHERE id=?";
        if (!isAdmin) {
            Sql += " AND sellerUserId=?";
        }

        try (PreparedStatement insertStmt = conn.prepareStatement(Sql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setInt(1, id);
            if (!isAdmin) {
                insertStmt.setInt(2, requesterUserId);
            }

            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0)
                return true;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
        }

        return false;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> results = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE " +
                "LOWER(name) LIKE ? OR " +
                "LOWER(category) LIKE ? OR " +
                "LOWER(description) LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String q = "%" + keyword.toLowerCase() + "%";
            pstmt.setString(1, q);
            pstmt.setString(2, q);
            pstmt.setString(3, q);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getString("product_condition"),
                            rs.getString("category"),
                            rs.getInt("sellerUserId"),
                            rs.getString("sellerContact"));
                    results.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }

        return results;
    }

    public Product findProductById(int id) {
        String sql = "SELECT * FROM Product WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getString("product_condition"),
                            rs.getString("category"),
                            rs.getInt("sellerUserId"),
                            rs.getString("sellerContact"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding product by ID: " + e.getMessage());
        }

        return null;
    }

    public List<Product> allProducts() {
        List<Product> results = new ArrayList<>();
        String sql = "SELECT * FROM Product";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getString("product_condition"),
                            rs.getString("category"),
                            rs.getInt("sellerUserId"),
                            rs.getString("sellerContact"));
                    results.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }

        return results;
    }

    @Override
    public void close() throws SQLException {
        if (conn != null)
            conn.close();
    }
}