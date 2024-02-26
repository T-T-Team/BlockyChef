package tnt.tntlib.api.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.data.Filter;
import tnt.tntlib.api.data.FilterType;
import tnt.tntlib.api.screen.widgets.ColorButton;
import tnt.tntlib.api.screen.widgets.ComponentListWidget;
import tnt.tntlib.api.screen.widgets.DataManagerWidget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterSelectDialogScreen<T> extends ModalDialogScreen {

    public static final Component TITLE = Component.translatable("screen.dialog.select_filter").withStyle(ChatFormatting.BOLD);

    private final Collection<FilterType<T>> availableFilters;
    private final Set<Filter<T>> activeFilters;
    private final DataManagerWidget.ViewChangeHandler<T> viewChangeHandler;

    public FilterSelectDialogScreen(Screen parentScreen, Collection<FilterType<T>> availableFilters, Set<Filter<T>> activeFilters, DataManagerWidget.ViewChangeHandler<T> viewHandler) {
        super(parentScreen, TITLE);
        this.availableFilters = availableFilters;
        this.activeFilters = activeFilters;
        this.viewChangeHandler = viewHandler;
        this.dialogWidth = 180;
        this.dialogHeight = 150;
    }

    @Override
    protected void init() {
        super.init();
        Set<FilterType<?>> activeFilterTypes = activeFilters.stream()
                .map(Filter::type)
                .collect(Collectors.toSet());
        ComponentListWidget<FilterType<T>> listWidget = new ComponentListWidget<>(leftPosition + 5, topPosition + 25, dialogWidth - 10, dialogHeight - 30, new ArrayList<>(availableFilters), (x, y, w, t) -> {
            ColorButton button = new ColorButton(x, y, w, 20, t.getComponentKey(), btn -> {
                Filter<T> filter = t.createDefault();
                DialogScreen screen = filter.getUi().createFilterDialog(parentScreen, viewChangeHandler);
                minecraft.setScreen(screen);
            });
            button.setPrimaryColorSchema(0, 0, 0);
            button.setSecondaryColorSchema(0, 0x44FFFFFF, 0);
            button.setTextColorSchema(0xFFFFFFFF, 0xFFFFFF00, 0xFF888888);
            button.active = !activeFilterTypes.contains(t);
            return button;
        });
        listWidget.setData(new ArrayList<>(availableFilters));
        addRenderableWidget(listWidget);
    }

    @Override
    protected boolean allowKeyboardInteractions() {
        return false;
    }
}
