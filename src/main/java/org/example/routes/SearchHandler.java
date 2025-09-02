package org.example.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.ItemGenerator;
import org.example.Main;
import org.example.Types;
import org.example.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

// Search page handler
public class SearchHandler implements HttpHandler {

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
//                    Types.User user = Main.db.getUserProfile(uid);
                    int page = Integer.parseInt(query.getOrDefault("page", "0"));
                    int limit = 50;
                    List<Types.Item> items = Main.db.searchItem(query.get("query"), page*limit, limit, null);
//                    List<Types.Item> items = ItemGenerator.sampleItems(limit);
                    StringBuilder itemHtml = new StringBuilder();
                    if (!items.isEmpty()) {
                        for (Types.Item item : items) {
                            String[] files = Main.db.getFileIds(item.id());
                            itemHtml.append(String.format("<div style='background-color:powderblue;'><a href='/item/%s'><img src='/image?id=%s'><p>%s</p><br><p>price: %d</p></a></div>", item.id(), files[0], item.name(), item.price()));
                            itemHtml.append("<hr>");
//                            itemHtml.append(String.format("<div><a href='/item/%s'><img src='https://static.wikia.nocookie.net/natsumeyuujinchou/images/e/ed/Nyanko_sensei_1.png'><p>%s</p><br><p>price: %d</p></a></div>", item.id(), item.name(), item.price()));
                        }
                    } else {
                        itemHtml.append("Couldn't Find the item");
                    }
                    itemHtml.append("<br>");
                    if(page > 0){
                        itemHtml.append(String.format("<form method='get' action='/search'><input type='hidden' name='query' value='%s'><input type='submit' value='Prev'><input type='hidden' name='page' value=%d></form>|", query.get("query"), page-1));
                    }
                    itemHtml.append(String.format("Page %d", page+1));
                    if(items.size()>=limit) {
                        itemHtml.append(String.format("|<form method='get' action='/search'><input type='hidden' name='query' value='%s'><input type='submit' value='Next'><input type='hidden' name='page' value=%d></form>", query.get("query"), page + 1));
                    }
                    String response = Utils.render(Utils.getHTML("search.html"), Map.of(
                            "item", itemHtml.toString(),
                            "query", query.get("query")
                            )
                    );
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
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