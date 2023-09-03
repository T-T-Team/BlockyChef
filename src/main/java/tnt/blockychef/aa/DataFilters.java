package tnt.blockychef.aa;

import java.util.function.Predicate;

public class DataFilters<T> implements Predicate<T> {

    @Override
    public boolean test(T t) {
        return false;
    }

    public interface Filter<T> {}
}
