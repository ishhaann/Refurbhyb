package org.example.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Main;
import org.example.Types;
import org.example.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

// Home page handler
public class HomeHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                Map<String, String> query = Utils.parseQuery(exchange.getRequestURI().getQuery());
                Map<String, String> cookies = Utils.parseCookies(exchange.getRequestHeaders().getFirst("Cookie"));
                String uid = "";
                if (cookies.containsKey("session")) {
                    uid = Main.db.validate(cookies.get("session"));
                }
                if (uid != null) {
                    Types.User user = Main.db.getUserProfile(uid);
                    String response = Utils.render(Utils.getHTML("home.html"), Map.of(
                        "name", user.name()
                        )
                    );
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    exchange.getResponseHeaders().add("Set-Cookie", "session=; Max-Age=0; Path=/");
                    exchange.getResponseHeaders().add("Location", "/login");
                    exchange.sendResponseHeaders(302, -1);
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