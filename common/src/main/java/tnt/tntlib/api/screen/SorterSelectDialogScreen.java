package tnt.tntlib.api.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.data.Sorter;
import tnt.tntlib.api.data.SorterType;
import tnt.tntlib.api.screen.widgets.ColorButton;
import tnt.tntlib.api.screen.widgets.ComponentListWidget;
import tnt.tntlib.api.screen.widgets.DataManagerWidget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SorterSelectDialogScreen<T> extends ModalDialogScreen {

    public static final Component TITLE = Component.translatable("screen.dialog.select_sorter").withStyle(ChatFormatting.BOLD);

    private final Collection<SorterType<T>> availableSorters;
    private final Set<Sorter<T>> activeSorters;
    private final DataManagerWidget.ViewChangeHandler<T> viewChangeHandler;

    public SorterSelectDialogScreen(Screen parentScreen, Collection<SorterType<T>> availableSorters, Set<Sorter<T>> activeSorters, DataManagerWidget.ViewChangeHandler<T> viewHandler) {
        super(parentScreen, TITLE);
        this.availableSorters = availableSorters;
        this.activeSorters = activeSorters;
        this.viewChangeHandler = viewHandler;
        this.dialogWidth = 180;
        this.dialogHeight = 150;
    }

    @Override
    protected void init() {
        super.init();
        Set<SorterType<?>> activeSorterTypes = activeSorters.stream()
                .map(Sorter::type)
                .collect(Collectors.toSet());
        ComponentListWidget<SorterType<T>> listWidget = new ComponentListWidget<>(leftPosition + 5, topPosition + 25, dialogWidth - 10, dialogHeight - 30, new ArrayList<>(availableSorters), (x, y, w, t) -> {
            ColorButton button = new ColorButton(x, y, w, 20, t.getComponentKey(), btn -> {
                Sorter<T> sorter = t.createDefault();
                viewChangeHandler.consume(dataview -> dataview.sorters().add(sorter));
                confirm();
            });
            button.setPrimaryColorSchema(0, 0, 0);
            button.setSecondaryColorSchema(0, 0x44FFFFFF, 0);
            button.setTextColorSchema(0xFFFFFFFF, 0xFFFFFF00, 0xFF888888);
            button.active = !activeSorterTypes.contains(t);
            return button;
        });
        listWidget.setData(new ArrayList<>(availableSorters));
        addRenderableWidget(listWidget);
    }

    @Override
    protected boolean allowKeyboardInteractions() {
        return false;
    }
}
