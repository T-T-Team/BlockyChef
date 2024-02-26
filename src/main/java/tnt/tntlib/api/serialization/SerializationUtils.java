package tnt.tntlib.api.serialization;

import java.util.OptionalInt;

public class SerializationUtils {

    public static OptionalInt tryParseInt(String text) {
        try {
            return OptionalInt.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    public static int tryParseInt(String text, int fallback) {
        return tryParseInt(text).orElse(fallback);
    }
}
