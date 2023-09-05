package tnt.blockychef.aa.widget;

import net.minecraft.client.gui.components.AbstractButton;

@FunctionalInterface
public interface ButtonPressHandler {
    void onPressed(AbstractButton button);
}
