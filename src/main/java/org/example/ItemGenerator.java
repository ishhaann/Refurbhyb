package org.example;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public class ItemGenerator {
    private static final Random random = new Random();

    public static Types.Item randomItem() {
        return new Types.Item(
                UUID.randomUUID().toString(), // random unique id
                "Item " + random.nextInt(1000), // name
                "Model-" + (100 + random.nextInt(900)), // model
                random.nextInt(5) + 1, // categoryId 1–5
                "This is a description for item " + random.nextInt(1000),
                50 + random.nextInt(950), // price 50–999
                LocalDateTime.now().plusDays(random.nextInt(365)), // warranty within 1 year
                randomCondition(),
                "seller" + random.nextInt(10) // seller1–seller10
        );
    }

    private static String randomCondition() {
        String[] conditions = {"New", "Like New", "Used", "Refurbished"};
        return conditions[random.nextInt(conditions.length)];
    }

    // Infinite stream of random items
    public static Stream<Types.Item> infiniteItems() {
        return Stream.generate(ItemGenerator::randomItem);
    }

    // Limited list
    public static List<Types.Item> sampleItems(int count) {
        return infiniteItems().limit(count).toList();
    }
}
