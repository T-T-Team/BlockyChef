package tnt.blockychef.aa;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class DataFilters<T> implements Predicate<T> {

    private final Set<Filter<T>> filters = new LinkedHashSet<>();

    public void addFilter(Filter<T> filter) {
        filters.add(filter);
    }

    public void removeFilter(Filter<T> filter) {
        filters.remove(filter);
    }

    public Collection<Filter<T>> values() {
        return filters;
    }

    @Override
    public boolean test(T t) {
        for (Filter<T> filter : filters) {
            if (!filter.test(t)) {
                return false;
            }
        }
        return true;
    }

    public static class SimpleFilter<T, P extends Predicate<T>> implements Filter<T> {

        private final String key;
        private P filter;
        private final Function<P, Component> filterFormatter;
        private boolean forced;

        public SimpleFilter(String key, P filter, Function<P, Component> filterFormatter) {
            this.key = key;
            this.filter = filter;
            this.filterFormatter = filterFormatter;
        }

        public SimpleFilter<T, P> force() {
            this.forced = true;
            return this;
        }

        public void setFilter(P filter) {
            this.filter = filter;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public boolean test(T t) {
            return filter.test(t);
        }

        @OnlyIn(Dist.CLIENT)
        public int guiWidth(Font font) {
            int i = font.width(getKey()) + font.width(filterFormatter.apply(filter)) + 15;
            if (forced) {
                i += 20;
            }
            return i;
        }

        @Override
        public ListManagementWidget convertToGui(int x, int y, int width, int height) {
            return null;
        }

        public Component getKey() {
            return Component.translatable("filter.key." + key);
        }
    }

    public interface Filter<T> extends Predicate<T> {

        String key();

        @OnlyIn(Dist.CLIENT)
        int guiWidth(Font font);

        @OnlyIn(Dist.CLIENT)
        ListManagementWidget convertToGui(int x, int y, int width, int height);
    }
}
