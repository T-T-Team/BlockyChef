package tnt.blockychef.aa.data;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import tnt.blockychef.aa.widget.FilterButton;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class EnumListFilter<T, E extends Enum<E>> implements Filter<T> {

    private final FilterType<T> type;
    private final Function<T, Collection<E>> extractor;
    private final BiPredicate<EnumSet<E>, Collection<E>> filterFunction;
    private EnumSet<E> allowedValues;
    private boolean removable;

    public EnumListFilter(FilterType<T> type, Function<T, Collection<E>> extractor, BiPredicate<EnumSet<E>, Collection<E>> filterFunction, EnumSet<E> allowedValues, boolean removable) {
        this.type = type;
        this.extractor = extractor;
        this.filterFunction = filterFunction;
        this.allowedValues = allowedValues;
        this.removable = removable;
    }

    @Override
    public boolean test(T t) {
        Collection<E> values = extractor.apply(t);
        return filterFunction.test(allowedValues, values);
    }

    @Override
    public FilterType<?> type() {
        return type;
    }

    @Override
    public boolean isRemovable() {
        return removable;
    }

    @Override
    public Filter<T> copy() {
        return new EnumListFilter<>(type, extractor, filterFunction, EnumSet.copyOf(allowedValues), removable);
    }

    @Override
    public FilterUiFactory<T> getUi() {
        return new FilterUiFactory<>() {
            @Override
            public int getElementWidth(Font font) {
                Component text = Component.literal(type.getComponentKey().getString() + " - " + getFilterValueForDisplay().getString());
                int base = font.width(text) + 10;
                if (removable) {
                    base += 20;
                }
                return base;
            }

            @Override
            public AbstractWidget createGuiWidget(int x, int y, int width, int height, Runnable updateTrigger) {
                return new FilterButton<>(x, y, width, height, EnumListFilter.this, updateTrigger);
            }

            @Override
            public Component getFilterValueForDisplay() {
                StringBuilder builder = new StringBuilder();
                builder.append("[");
                Collection<E> coll = EnumListFilter.this.allowedValues;
                int displayLimit = Math.min(coll.size(), 5);
                Iterator<E> it = coll.iterator();
                int counter = 0;
                while (it.hasNext()) {
                    E e = it.next();
                    String value = e.name().toUpperCase(Locale.ROOT);
                    builder.append(value);
                    if (it.hasNext() && ++counter < displayLimit) {
                        builder.append(",");
                    } else {
                        break;
                    }
                }
                if (coll.size() > 5) {
                    builder.append(",...");
                }
                builder.append("]");
                return Component.literal(builder.toString());
            }
        };
    }
}
