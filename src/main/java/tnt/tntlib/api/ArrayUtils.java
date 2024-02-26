package tnt.tntlib.api;

import java.util.Arrays;
import java.util.function.IntFunction;

public final class ArrayUtils {

    public static <T> T[] indexedFill(T[] arr, IntFunction<T> factory) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = factory.apply(i);
        }
        return arr;
    }

    @SafeVarargs
    public static <T> T[] merge(IntFunction<T[]> factory, T[]... arrays) {
        int total = Arrays.stream(arrays).mapToInt(t -> t.length).sum();
        T[] t = factory.apply(total);
        int start = 0;
        for (T[] arr : arrays) {
            System.arraycopy(arr, 0, t, start, arr.length);
            start += arr.length;
        }
        return t;
    }

    public static <T> boolean containsElement(T[] array, T element) {
        for (T t : array) {
            if (t == element) {
                return true;
            }
        }
        return false;
    }

    private ArrayUtils() {}
}
