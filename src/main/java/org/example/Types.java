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
}
