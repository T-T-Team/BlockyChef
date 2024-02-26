package tnt.tntlib.api.data;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.screen.DialogScreen;
import tnt.tntlib.api.screen.TextFilterDialog;
import tnt.tntlib.api.screen.widgets.DataManagerWidget;
import tnt.tntlib.api.screen.widgets.FilterButton;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class TextFilter<T> implements Filter<T> {

    private final FilterType<T> filterType;
    private final Function<T, String> toString;
    private final BiPredicate<String, String> filter;
    private final boolean removable;
    private String expression;

    public TextFilter(FilterType<T> filterType, Function<T, String> toString, BiPredicate<String, String> filter, String expression, boolean removable) {
        this.filterType = filterType;
        this.toString = toString;
        this.filter = filter;
        this.expression = expression;
        this.removable = removable;
    }

    @Override
    public boolean test(T t) {
        String text = toString.apply(t);
        return filter.test(text, expression);
    }

    @Override
    public FilterType<T> type() {
        return filterType;
    }

    @Override
    public Filter<T> copy() {
        return new TextFilter<>(filterType, toString, filter, expression, removable);
    }

    @Override
    public boolean isRemovable() {
        return removable;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public FilterUiFactory<T> getUi() {
        return new FilterUiFactory<>() {
            @Override
            public int getElementWidth(Font font) {
                Component content = Component.literal(filterType.getComponentKey().getString() + " - " + expression);
                int size = font.width(content) + 10;
                if (removable) {
                    size += 20;
                }
                return size;
            }

            @Override
            public AbstractWidget createGuiWidget(int x, int y, int width, int height, Screen parent, DataManagerWidget.ViewChangeHandler<T> changeHandler) {
                return new FilterButton<>(x, y, width, height, TextFilter.this, parent, changeHandler);
            }

            @Override
            public Component getFilterValueForDisplay() {
                return Component.literal(expression);
            }

            @Override
            public DialogScreen createFilterDialog(Screen parentScreen, DataManagerWidget.ViewChangeHandler<T> viewChangeHandler) {
                return new TextFilterDialog<>(parentScreen, TextFilter.this, viewChangeHandler);
            }
        };
    }
}
