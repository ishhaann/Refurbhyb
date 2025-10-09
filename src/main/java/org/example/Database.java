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

    public String validate(String sid) {
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

    public String getSession(String userId) {
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

    public String[] getConditions() {
        List<String> conditions = new ArrayList<String>();
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
        return conditions.toArray(new String[0]);
    }

    public Types.KeyValue[] getCategories() {
        List<Types.KeyValue> categories = new ArrayList<>();
        String sql = "SELECT id, name FROM Category";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new Types.KeyValue(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }

        return categories.toArray(new Types.KeyValue[0]);
    }

    public String addItem(String name, String model, int categoryId, String description,
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

    public List<Types.Item> getItems(String userId) {
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

    public Types.Item getItem(String itemId) {
        String sql = "SELECT * FROM Item where id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    return Types.Item.fromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching items: " + e.getMessage());
        }
        return null;
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

    public Types.Item searchItem(String id) {
        String sql = "SELECT * FROM Item WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Types.Item.fromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching items: " + e.getMessage());
        }

        return null;
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

    public boolean addToCart(String userId, String itemId, int quantity) {
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

    public boolean removeFromCart(String userId, String itemId) {
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

    public List<Types.CartItem> getCartItems(String userId) {
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

    public byte[] getFile(String fileId) {
        String sql = "SELECT file FROM Files WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("file");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching file: " + e.getMessage());
        }
        return null;
    }

    public String[] getFileIds(String item_id) {
        List<String> fileList = new ArrayList<>();
        String sql = "SELECT id FROM Files WHERE item_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item_id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fileList.add(rs.getString("id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching file: " + e.getMessage());
        }
        return fileList.toArray(new String[0]);
    }

    public boolean uploadFile(String itemId, byte[] fileData) {
        String sql = "INSERT INTO Files (id, item_id, file) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String uuidv4 = UUID.randomUUID().toString();
            pstmt.setString(1, uuidv4);
            pstmt.setString(2, itemId);
            pstmt.setBytes(3, fileData);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error uploading file: " + e.getMessage());
        }
        return false;
    }

    public boolean placeOrder(String userId, String itemId, int quantity, String transactionId) {
        String sql = "INSERT INTO `Order` (uid, item_id, quantity, transaction_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, itemId);
            pstmt.setInt(3, quantity);
            pstmt.setString(4, transactionId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error placing order: " + e.getMessage());
            return false;
        }
    }

    public List<Types.OrderItem> getUserOrders(String userId, String sellerId) {
        List<Types.OrderItem> items = new ArrayList<Types.OrderItem>();
        String sql = "SELECT * FROM `Order`";
        if(userId != null){
            sql+=" WHERE uid=?";
        } else if(sellerId != null){
            sql+=" WHERE item_id IN ( SELECT id FROM Item WHERE seller = ?)";
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId!=null?userId: sellerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    items.add(Types.OrderItem.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching items: " + e.getMessage());
        }
        return items;
    }

    public boolean verifyPaymentAndShip(int orderId, String trackingId, String partner) {
        String sql = "UPDATE `Order` SET payment_success=1, shipped=1, tracking_id=?, shipping_partner=? where id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trackingId);
            pstmt.setString(2, partner);
            pstmt.setInt(3, orderId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            return false;
        }
    }

    public boolean removeItem(String itemId) {
        String sql = "DELETE FROM Item WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing from cart: " + e.getMessage());
            return false;
        }
    }

    public boolean incrementQuantity(String itemId, int quantity) {
        String sql = "UPDATE Item SET quantity=quantity + ? WHERE id=? AND quantity + ? >= 0";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setString(2, itemId);
            pstmt.setInt(3, quantity);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error incrementing quantity: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void close() throws SQLException {
        if (conn != null) conn.close();
    }
}
