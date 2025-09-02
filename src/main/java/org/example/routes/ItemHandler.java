package org.example.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Main;
import org.example.Types;
import org.example.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


public class ItemHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange){
            String path = exchange.getRequestURI().getPath();

            String[] parts = path.split("/");
            String id = parts.length > 2 ? parts[2] : null;

            if (id == null || id.isEmpty()) {
                String response = "Missing item id";
                exchange.sendResponseHeaders(400, response.length());
                exchange.getResponseBody().write(response.getBytes());
                return;
            }
            Types.Item item = Main.db.searchItem(id);
            if (item == null) {
                String response = "Invalid item id";
                exchange.sendResponseHeaders(404, response.length());
                exchange.getResponseBody().write(response.getBytes());
                return;
            }
            StringBuilder images = new StringBuilder();
            for (String imageId : Main.db.getFileIds(item.id())) {
                images.append(String.format("<img src='/image?id=%s' alt='%s'>", imageId, item.name()));
            }
            String response = Utils.render(Utils.getHTML("item.html"), Map.of(
                "image", images.toString(),
                "name", item.name(),
                "price", ""+item.price(),
                "description", item.description(),
                "seller", item.seller(),
                "warranty", item.warranty().toString()
            ));
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}