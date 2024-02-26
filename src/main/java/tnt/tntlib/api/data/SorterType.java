package tnt.tntlib.api.data;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class SorterType<SRC> {

    private final ResourceLocation key;
    private final Supplier<Comparator<SRC>> comparatorFn;
    private final Function<SorterType<SRC>, Sorter<SRC>> defaultSorterProvider;

    public SorterType(ResourceLocation key, Supplier<Comparator<SRC>> comparatorFn, Function<SorterType<SRC>, Sorter<SRC>> defaultSorterProvider) {
        this.key = key;
        this.comparatorFn = comparatorFn;
        this.defaultSorterProvider = defaultSorterProvider;
    }

    public Comparator<SRC> getComparatorInstance() {
        return comparatorFn.get();
    }

    public Sorter<SRC> createDefault() {
        return defaultSorterProvider.apply(this);
    }

    public ResourceLocation getKey() {
        return key;
    }

    public Component getComponentKey() {
        return Component.translatable("sorter.key." + key.toLanguageKey());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SorterType<?> that = (SorterType<?>) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public static <SRC> Comparator<SRC> toComparator(Collection<Sorter<SRC>> sorters) {
        Comparator<SRC> comp = null;
        for (Sorter<SRC> sorter : sorters) {
            if (comp == null) {
                comp = sorter.getComparator();
            } else {
                comp = comp.thenComparing(sorter.getComparator());
            }
        }
        return comp;
    }
}
