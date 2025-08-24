package org.example;

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

    public String validate(String email, String password) {
        String sql = "SELECT uid FROM User WHERE email=? AND password=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("uid");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
        }

        return null; // no match found
    }

    String validate(String sid) {
        String sql = "SELECT uid FROM Session WHERE sid=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sid);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("uid");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
        }

        return null; // no match found
    }

    String getSession(String userId) {
        String uuidv4 = UUID.randomUUID().toString();
        String sql = "INSERT INTO Session VALUES (?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuidv4);
            pstmt.setString(2, userId);

            pstmt.executeUpdate();
            return uuidv4;
        } catch (SQLException e) {
            System.err.println("Error creating session: " + e.getMessage());
        }

        return null;
    }

    public String createUser(String email, String name, String phoneNo, String password, String address) {
        String uid = UUID.randomUUID().toString();
        String sql = "INSERT INTO User VALUES (?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uid);
            pstmt.setString(2, email);
            pstmt.setString(3, name);
            pstmt.setString(4, phoneNo);
            pstmt.setString(5, password);
            pstmt.setString(6, address);

            pstmt.executeUpdate();
            return uid;
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }

        return null;
    }

    List<String> getConditions() {
        List<String> conditions = new ArrayList<>();
        String sql = "SELECT `condition` FROM Conditions";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    conditions.add(rs.getString("condition"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching conditions list: " + e.getMessage());
        }
        return conditions;
    }

    Map<Integer, String> getCategories() {
        Map<Integer, String> categories = new HashMap<>();
        String sql = "SELECT id, name FROM Category";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categories.put(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }

        return categories;
    }

    String addItem(String name, String model, int categoryId, String description,
                   int price, String warranty, String condition, String seller, int quantity) {
        String uuidv4 = UUID.randomUUID().toString();

        String sql = """
        INSERT INTO Item
        (id, name, model, category_id, description, price, warranty, `condition`, seller, quantity)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuidv4);      // id
            pstmt.setString(2, name);        // name
            pstmt.setString(3, model);       // model
            pstmt.setInt(4, categoryId);     // category_id
            pstmt.setString(5, description); // description
            pstmt.setInt(6, price);          // price
            pstmt.setString(7, warranty);    // warranty (as TEXT in ISO 8601 format)
            pstmt.setString(8, condition);   // condition
            pstmt.setString(9, seller);      // seller
            pstmt.setInt(10, quantity);      // quantity

            pstmt.executeUpdate();
            return uuidv4;
        } catch (SQLException e) {
            System.err.println("Error adding item: " + e.getMessage());
        }

        return null;
    }

    List<Types.Item> getItems(String userId) {
        List<Types.Item> items = new ArrayList<Types.Item>();
        String sql = "SELECT * FROM Item where seller=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    items.add(Types.Item.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching items: " + e.getMessage());
        }
        return items;
    }

    public List<Types.Item> searchItem(String keyword, int offset, int limit, Integer price) {
        List<Types.Item> items = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM Item WHERE quantity > 0 AND (name LIKE ? OR model LIKE ? OR description LIKE ?)"
        );

        if (price != null) {
            sql.append(" AND price = ?");
        }

        sql.append(" LIMIT ? OFFSET ?");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int index = 1;

            String pattern = "%" + keyword + "%";
            pstmt.setString(index++, pattern);
            pstmt.setString(index++, pattern);
            pstmt.setString(index++, pattern);

            if (price != null) {
                pstmt.setInt(index++, price);
            }

            pstmt.setInt(index++, limit);
            pstmt.setInt(index++, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(Types.Item.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching items: " + e.getMessage());
        }

        return items;
    }

    public Types.User getUserProfile(String userId) {
        String sql = "SELECT * FROM User WHERE uid=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Types.User.fromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user profile: " + e.getMessage());
        }
        return null;
    }

    boolean addToCart(String userId, String itemId, int quantity) {
        String sql = "INSERT INTO Cart (uid, item_id, quantity) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, itemId);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding to cart: " + e.getMessage());
            return false;
        }
    }

    boolean removeFromCart(String userId, String itemId) {
        String sql = "DELETE FROM Cart WHERE uid = ? AND item_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing from cart: " + e.getMessage());
            return false;
        }
    }

    List<Types.CartItem> getCartItems(String userId) {
        List<Types.CartItem> cartItems = new ArrayList<>();
        String sql = """
        SELECT i.* , c.quantity
        FROM Cart c
        JOIN Item i ON c.item_id = i.id
        WHERE c.uid = ?
    """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cartItems.add(new Types.CartItem(
                            Types.Item.fromResultSet(rs),
                            rs.getInt("quantity")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cart items: " + e.getMessage());
        }

        return cartItems;
    }

    // ToDo: Add Orders
    // Update Payment Status
    // Update Tracking ID and Partner

    @Override
    public void close() throws SQLException {
        if (conn != null) conn.close();
    }
}
