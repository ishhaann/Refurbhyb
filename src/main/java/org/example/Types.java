package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Types {
    public record Item(
            String id,
            String name,
            String model,
            int categoryId,
            String description,
            int price,
            LocalDateTime warranty,
            String condition,
            String seller
    ) {
        public static Item fromResultSet(ResultSet rs) throws SQLException {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime warrantyDate = rs.getString("warranty") != null
                    ? LocalDateTime.parse(rs.getString("warranty"), formatter)
                    : null;


            return new Item(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("model"),
                    rs.getInt("category_id"),
                    rs.getString("description"),
                    rs.getInt("price"),
                    warrantyDate,
                    rs.getString("condition"),
                    rs.getString("seller")
            );
        }
    }

    public record User(
            String uid,
            String email,
            String name,
            String phoneNo,
            String password,
            String address
    ) {
        public static User fromResultSet(ResultSet rs) throws SQLException {
            return new User(
                    rs.getString("uid"),
                    rs.getString("email"),
                    rs.getString("name"),
                    rs.getString("phoneNo"),
                    rs.getString("password"),
                    rs.getString("address")
            );
        }
    }

    public record CartItem( Item item, Integer quantity){}

    public record OrderItem(
    int id,
    String uid,
    String item_id,
    int quantity,
    String transaction_id,
    boolean payment_success,
    boolean shipped,
    String tracking_id,
    String shipping_partner,
    LocalDateTime created_at){
        public static OrderItem fromResultSet(ResultSet rs) throws SQLException {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime orderDate = rs.getString("created_at") != null
                    ? LocalDateTime.parse(rs.getString("created_at"), formatter)
                    : null;
            return new OrderItem(
                rs.getInt("id"),
                rs.getString("uid"),
                rs.getString("item_id"),
                rs.getInt("quantity"),
                rs.getString("transaction_id"),
                rs.getBoolean("payment_success"),
                rs.getBoolean("shipped"),
                rs.getString("tracking_id"),
                rs.getString("shipping_partner"),
                orderDate
            );
        }
    }

    public record KeyValue(int key, String value) {
    @Override
    public String toString() {
        return value;
    }
    }

}
