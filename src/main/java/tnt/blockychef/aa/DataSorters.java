package tnt.blockychef.aa;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public final class DataSorters<T> {

    private final Set<Sorter<T>> sorters = new LinkedHashSet<>();

    public void add(Sorter<T> sorter) {
        sorters.add(sorter);
    }

    public void remove(Sorter<T> sorter) {
        sorters.remove(sorter);
    }

    public Collection<Sorter<T>> values() {
        return sorters;
    }

    // TODO convert to widget list

    @Nullable
    public Comparator<T> getComparator() {
        Comparator<T> comparator = null;
        for (Sorter<T> sorter : sorters) {
            if (comparator == null) {
                comparator = sorter.getComparator();
            } else {
                comparator = comparator.thenComparing(sorter.getComparator());
            }
        }
        return comparator;
    }

    public DataSorters<T> copy() {
        DataSorters<T> sorters = new DataSorters<>();
        sorters.sorters.addAll(this.sorters.stream().map(Sorter::copy).toList());
        return sorters;
    }

    public static final class BaseSorter<T> implements Sorter<T> {

        private final String key;
        private final Supplier<Comparator<T>> baseComparatorProvider;
        private boolean reverseOrder;
        private boolean forced;

        public BaseSorter(String key, Supplier<Comparator<T>> baseComparatorProvider) {
            this(key, baseComparatorProvider, false);
        }

        public BaseSorter(String key, Supplier<Comparator<T>> baseComparatorProvider, boolean reverseOrder) {
            this.key = key;
            this.baseComparatorProvider = baseComparatorProvider;
            this.reverseOrder = reverseOrder;
        }

        public BaseSorter<T> force() {
            this.forced = true;
            return this;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public void clicked() {
            reverseOrder = !reverseOrder;
        }

        @Override
        public Comparator<T> getComparator() {
            Comparator<T> comparator = baseComparatorProvider.get();
            if (reverseOrder) {
                comparator = comparator.reversed();
            }
            return comparator;
        }

        @Override
        public Sorter<T> copy() {
            BaseSorter<T> sorter = new BaseSorter<>(key, baseComparatorProvider, reverseOrder);
            if (forced) {
                return sorter.force();
            }
            return sorter;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public int guiWidth(Font font) {
            int i = font.width(getName()) + font.width(getValue()) + 15;
            if (!forced) {
                i += 20;
            }
            return i;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public ListManagementWidget convertToGui(int x, int y, int width, int height, RefreshCallback callback) {
            return new ListManagementWidget(x, y, width, height, getName(), getValue(), !forced, () -> {
                clicked();
                callback.refresh();
            });
        }

        public Component getName() {
            return Component.translatable("sorter.key." + key);
        }

        public Component getValue() {
            return reverseOrder ? Component.translatable("sorter.value.desc") : Component.translatable("sorter.value.asc");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BaseSorter<?> that = (BaseSorter<?>) o;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    public interface Sorter<T> {

        String key();

        void clicked();

        Comparator<T> getComparator();

        Sorter<T> copy();

        @OnlyIn(Dist.CLIENT)
        int guiWidth(Font font);

        @OnlyIn(Dist.CLIENT)
        ListManagementWidget convertToGui(int x, int y, int width, int height, RefreshCallback callback);
    }
}
