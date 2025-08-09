package org.example;

import java.sql.*;

public class Main {
    static Connection conn;

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:./test.db");
            System.out.println("Opened database successfully");
            // createTables();
        } catch (Exception e) {
            System.err.println("Database Error: " + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Closing Error: " + e.getMessage());
            }
        }
    }

    /*
    static void createTables() throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS Buyer (
                            email VARCHAR(50) PRIMARY KEY,
                            name VARCHAR(50),
                            phoneNo BIGINT,
                            password VARCHAR(50)
                        );
                    """);
        } finally {
            stmt.close();
        }
    }
    */
}