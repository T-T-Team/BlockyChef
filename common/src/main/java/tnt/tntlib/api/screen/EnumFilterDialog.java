package tnt.tntlib.api.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.data.EnumListFilter;
import tnt.tntlib.api.screen.widgets.ComponentListWidget;
import tnt.tntlib.api.screen.widgets.DataManagerWidget;
import tnt.tntlib.api.screen.widgets.ObjectCheckboxWidget;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class EnumFilterDialog<T, E extends Enum<E>> extends ModalDialogScreen {

    private final EnumListFilter<T, E> filter;
    private final DataManagerWidget.ViewChangeHandler<T> changeHandler;

    public EnumFilterDialog(Screen parent, EnumListFilter<T, E> filter, DataManagerWidget.ViewChangeHandler<T> changeHandler) {
        super(parent, filter.type().getComponentKey());
        this.filter = filter;
        this.changeHandler = changeHandler;
        this.dialogWidth = 180;
        this.dialogHeight = 150;
    }

    @Override
    protected void init() {
        super.init();
        Class<E> type = filter.getEnumType();
        List<E> elementList = Arrays.asList(type.getEnumConstants());
        Set<E> selectedValues = filter.getAllowedValues();
        addRenderableWidget(new ComponentListWidget<>(leftPosition + 5, topPosition + 15, dialogWidth - 10, dialogHeight - 45, elementList, (x, y, w, data) -> {
            ObjectCheckboxWidget<E> checkboxWidget = new ObjectCheckboxWidget<>(data, x, y, w, 20, selectedValues.contains(data));
            checkboxWidget.setComponentProvider(e -> Component.literal(e.name()));
            checkboxWidget.setSelectListener(widget -> {
                selectedValues.add(widget.getElement());
                changeHandler.consume(view -> view.filters().add(filter));
            });
            checkboxWidget.setUnselectListener(widget -> {
                selectedValues.remove(widget.getElement());
                changeHandler.consume(view -> view.filters().add(filter));
            });
            return checkboxWidget;
        }));
        addRenderableWidget(new Button.Builder(CommonComponents.GUI_CONTINUE, btn -> confirm())
                .size(dialogWidth - 10, 20)
                .pos(leftPosition + 5, topPosition + dialogHeight - 25)
                .build()
        );
    }
}
