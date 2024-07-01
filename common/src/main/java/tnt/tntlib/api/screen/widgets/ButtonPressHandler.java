package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.gui.components.AbstractButton;

@FunctionalInterface
public interface ButtonPressHandler {
    void onPressed(AbstractButton button);
}
