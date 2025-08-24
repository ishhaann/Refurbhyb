package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static String getHTML(String fileName) throws IOException {
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream("web/" + fileName)) {
            if (is == null) {
                throw new IOException("HTML file not found: " + fileName);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;

        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                map.put(pair[0], pair[1]);
            } else {
                map.put(pair[0], "");
            }
        }
        return map;
    }

}
