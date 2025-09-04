package org.example.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Main;
import org.example.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

// Root page handler
public class SignUpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                Map<String, String> query = Utils.parseQuery(exchange.getRequestURI().getQuery());
                String email = query.get("email");
                String name = query.get("name");
                String phoneNo = query.get("phoneNo");
                String password =  query.get("password");
                String address = query.get("address");
                if (email != null && name != null && phoneNo !=null && password !=null && address != null) {
                    try {
                        Main.db.createUser(email, name, phoneNo, password, address);
                        exchange.getResponseHeaders().add("Location", "/signin");
                        exchange.sendResponseHeaders(302, -1);
                    }  catch (Exception e) {
                        String response = Utils.render(Utils.getHTML("signup.html"), Map.of(
                                "error", e.toString()
                        ));
                        exchange.sendResponseHeaders(500, response.getBytes().length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                } else {
                    String response = Utils.render(Utils.getHTML("signup.html"), Map.of(
                            "error", ""
                    ));
                    if (query.isEmpty()) {
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                    } else {
                        exchange.sendResponseHeaders(400, response.getBytes().length);
                    }
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
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