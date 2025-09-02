package org.example.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Main;
import org.example.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

// Root page handler
public class LoginHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                Map<String, String> query = Utils.parseQuery(exchange.getRequestURI().getQuery());
                Map<String, String> cookies = Utils.parseCookies(exchange.getRequestHeaders().getFirst("Cookie"));
                if(cookies.containsKey("session")){
                    exchange.getResponseHeaders().add("Location", "/home");
                    exchange.sendResponseHeaders(302, -1);
                    return;
                }
                if (query.isEmpty()) {
                    String response = Utils.getHTML("login.html");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    String email = query.get("email");
                    String password = query.get("password");
                    String uid = Main.db.validate(email, password);
                    if (uid == null) {
                        System.out.println("Invalid email or password");
                        String response = Utils.getHTML("login.html");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else {
                        String session = Main.db.getSession(uid);
                        exchange.getResponseHeaders().add("Set-Cookie", "session=" + session + "; Path=/; HttpOnly");
                        exchange.getResponseHeaders().add("Location", "/home");
                        exchange.sendResponseHeaders(302, -1); // 302 Found, -1 means no response body
                    }
                }
            } else {
                String response = "Method Not Allowed";
                exchange.getResponseHeaders().add("Allow", "GET");
                exchange.sendResponseHeaders(405, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
}