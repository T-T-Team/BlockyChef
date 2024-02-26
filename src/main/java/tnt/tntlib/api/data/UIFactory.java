package tnt.tntlib.api.data;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tnt.tntlib.api.screen.widgets.DataManagerWidget;

public interface UIFactory<SRC> {

    @OnlyIn(Dist.CLIENT)
    int getElementWidth(Font font);

    @OnlyIn(Dist.CLIENT)
    AbstractWidget createGuiWidget(int x, int y, int width, int height, Screen parent, DataManagerWidget.ViewChangeHandler<SRC> changeHandler);
}
