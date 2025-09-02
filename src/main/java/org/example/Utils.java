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

    public static Map<String, String> parseCookies(String cookiesHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookiesHeader == null || cookiesHeader.isEmpty()) {
            return cookies;
        }

        String[] pairs = cookiesHeader.split(";");
        for (String pair : pairs) {
            String[] parts = pair.trim().split("=", 2);
            if (parts.length == 2) {
                cookies.put(parts[0].trim(), parts[1].trim());
            } else if (parts.length == 1) {
                cookies.put(parts[0].trim(), "");
            }
        }
        return cookies;
    }

    public static String render(String template, Map<String, String> values) {
        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String placeholder = "{{ " + entry.getKey() + " }}";
            result = result.replace(placeholder, entry.getValue());
        }
        return result;
    }
}
