package tnt.blockychef.aa.data;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface View<SRC> extends UnaryOperator<Stream<SRC>> {

    String viewName();

    Set<Filter<SRC>> filters();

    Set<Sorter<SRC>> sorters();

    // TODO columns

    View<SRC> copy();

    record SimpleView<SRC>(String viewName, Set<Filter<SRC>> filters, Set<Sorter<SRC>> sorters) implements View<SRC> {

        @SuppressWarnings("unchecked")
        @Override
        public View<SRC> copy() {
            return new SimpleView<>(
                    viewName,
                    filters.stream().map(t -> (Filter<SRC>) t.copy()).collect(Collectors.toCollection(LinkedHashSet::new)),
                    sorters.stream().map(t -> (Sorter<SRC>) t.copy()).collect(Collectors.toCollection(LinkedHashSet::new))
            );
        }

        @Override
        public Stream<SRC> apply(Stream<SRC> srcStream) {
            if (!filters.isEmpty()) {
                srcStream = srcStream.filter(FilterType.toFilter(filters));
            }
            if (!sorters.isEmpty()) {
                srcStream = srcStream.sorted(SorterType.toComparator(sorters));
            }
            return srcStream;
        }
    }
}
