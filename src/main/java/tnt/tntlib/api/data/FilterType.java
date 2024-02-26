package tnt.tntlib.api.data;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public final class FilterType<SRC> {

    private final ResourceLocation key;
    private final Function<FilterType<SRC>, Filter<SRC>> defaultFilterProvider;

    public FilterType(ResourceLocation key, Function<FilterType<SRC>, Filter<SRC>> defaultFilterProvider) {
        this.key = key;
        this.defaultFilterProvider = defaultFilterProvider;
    }

    public Filter<SRC> createDefault() {
        return defaultFilterProvider.apply(this);
    }

    public ResourceLocation getKey() {
        return key;
    }

    public Component getComponentKey() {
        return Component.translatable("filter.key." + key.toLanguageKey());
    }

    public static <SRC> Predicate<SRC> toFilter(Collection<Filter<SRC>> filters) {
        return src -> {
            for (Filter<SRC> filter : filters) {
                if (!filter.test(src)) {
                    return false;
                }
            }
            return true;
        };
    }
}
