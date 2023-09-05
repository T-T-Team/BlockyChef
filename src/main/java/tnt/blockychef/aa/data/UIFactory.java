package tnt.blockychef.aa.data;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;

public interface UIFactory<SRC> {

    int getElementWidth(Font font);

    AbstractWidget createGuiWidget(int x, int y, int width, int height, Runnable updateTrigger);
}
