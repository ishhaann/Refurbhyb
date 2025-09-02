package org.example.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Main;
import org.example.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

// file route handler
public class ImageHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                Map<String, String> query = Utils.parseQuery(exchange.getRequestURI().getQuery());
                if(query.containsKey("id")){
                    byte[] image = Main.db.getFile(query.get("id"));
                    if(image != null){
                        exchange.getResponseHeaders().add("Content-Type", "image/png");
                        exchange.sendResponseHeaders(200, image.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(image);
                        }
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
            String response = "Not Found";
            exchange.sendResponseHeaders(404, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}