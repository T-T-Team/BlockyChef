package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class ObjectCheckboxWidget<T> extends Checkbox {

    private final T element;
    private Function<T, Component> componentProvider = t -> Component.literal(t.toString());
    private CheckboxSelectListener<T> selectListener = w -> {};
    private CheckboxSelectListener<T> unselectListener = w -> {};

    public ObjectCheckboxWidget(T element, int pX, int pY, int pWidth, int pHeight, boolean pSelected) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY, pSelected);
        this.element = element;
    }

    public ObjectCheckboxWidget(T element, int pX, int pY, int pWidth, int pHeight, boolean pSelected, boolean pShowLabel) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY, pSelected, pShowLabel);
        this.element = element;
    }

    public T getElement() {
        return element;
    }

    public void setComponentProvider(Function<T, Component> componentProvider) {
        this.componentProvider = componentProvider;
        this.setMessage(this.componentProvider.apply(element));
    }

    public void setSelectListener(CheckboxSelectListener<T> selectListener) {
        this.selectListener = selectListener;
    }

    public void setUnselectListener(CheckboxSelectListener<T> unselectListener) {
        this.unselectListener = unselectListener;
    }

    @Override
    public void onPress() {
        super.onPress();
        if (selected()) {
            selectListener.handle(this);
        } else {
            unselectListener.handle(this);
        }
    }

    @FunctionalInterface
    public interface CheckboxSelectListener<T> {
        void handle(ObjectCheckboxWidget<T> widget);
    }
}
