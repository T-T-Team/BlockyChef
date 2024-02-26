package tnt.tntlib.api.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.data.TextFilter;
import tnt.tntlib.api.screen.widgets.DataManagerWidget;

public class TextFilterDialog<T> extends ModalDialogScreen {

    private static final Component SUGGESTION = Component.translatable("label.suggest.enter_text");
    private final TextFilter<T> filter;
    private final DataManagerWidget.ViewChangeHandler<T> changeHandler;

    public TextFilterDialog(Screen parent, TextFilter<T> filter, DataManagerWidget.ViewChangeHandler<T> changeHandler) {
        super(parent, filter.type().getComponentKey());
        this.filter = filter;
        this.changeHandler = changeHandler;
        this.dialogWidth = 150;
        this.dialogHeight = 65;
    }

    @Override
    protected void init() {
        super.init();

        EditBox editBox = addRenderableWidget(new EditBox(font, leftPosition + 5, topPosition + 15, dialogWidth - 10, 20, CommonComponents.EMPTY));
        editBox.setResponder(text -> {
            editBox.setSuggestion(text.isEmpty() ? SUGGESTION.getString() : null);
            filter.setExpression(text);
            changeHandler.consume(view -> {
                if (text.isEmpty()) {
                    view.filters().remove(filter);
                } else {
                    view.filters().add(filter);
                }
            });
        });
        editBox.setValue(filter.getExpression());
        addRenderableWidget(new Button.Builder(CommonComponents.GUI_CONTINUE, btn -> confirm())
                .size(dialogWidth - 10, 20)
                .pos(leftPosition + 5, topPosition + dialogHeight - 25)
                .build()
        );
    }
}
