package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.gui.components.AbstractWidget;

import javax.annotation.Nullable;

@FunctionalInterface
public interface WidgetFactory<T> {

    @Nullable
    AbstractWidget create(int x, int y, int width, T data);
}
