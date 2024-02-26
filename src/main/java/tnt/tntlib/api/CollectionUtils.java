package tnt.tntlib.api;

import java.util.Collection;

public final class CollectionUtils {

    public static <T> boolean containsAny(Collection<T> c1, Collection<T> c2) {
        for (T t : c1) {
            if (c2.contains(t)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean containsAll(Collection<T> c1, Collection<T> c2) {
        for (T t : c1) {
            if (!c2.contains(t)) {
                return false;
            }
        }
        return true;
    }

    private CollectionUtils() {}
}
