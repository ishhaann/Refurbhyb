package org.example;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try (Database db = new Database("jdbc:sqlite:./test.db")) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
