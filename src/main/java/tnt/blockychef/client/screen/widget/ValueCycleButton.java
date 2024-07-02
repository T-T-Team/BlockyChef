package tnt.blockychef.client.screen.widget;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class ValueCycleButton<T> extends AbstractButton {

    private final T[] values;
    private final PressListener<T> listener;
    private int index;
    private Function<T, Component> formatter = t -> Component.literal(t.toString());

    public ValueCycleButton(int x, int y, int width, int height, T[] values, PressListener<T> listener) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.values = values;
        this.listener = listener;
        setByIndex(0);
    }

    @Override
    public void onPress() {
        setByIndex(index + 1);
        listener.onPress(getValue());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
        narration.add(NarratedElementType.TITLE, getMessage());
    }

    public void setFormatter(Function<T, Component> formatter) {
        this.formatter = formatter;
        this.updateLabels();
    }

    public void setByIndex(int index) {
        this.index = index % values.length;
        this.updateLabels();
    }

    public void updateLabels() {
        this.setMessage(formatter.apply(getValue()));
    }

    public T getValue() {
        return values[index];
    }

    @FunctionalInterface
    public interface PressListener<T> {
        void onPress(T value);
    }
}
