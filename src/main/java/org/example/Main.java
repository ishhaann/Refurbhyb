package org.example;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.example.routes.LoginHandler;
import org.example.routes.RootHandler;

public class Main {
    public static Database db;
    public static void main(String[] args) throws IOException {
        db = new Database("jdbc:mysql://localhost:3306/refurby?user=root&password=mysql");

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Define context (route)
        server.createContext("/", new RootHandler());
        server.createContext("/login", new LoginHandler());

        // Start server
        server.setExecutor(null); // creates a default executor
        System.out.println("Server started at http://localhost:8080/");
        server.start();
    }
}
