package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import tnt.tntlib.api.data.Filter;
import tnt.tntlib.api.screen.DialogScreen;

public class FilterButton<SRC> extends AbstractDataComponentButton {

    public FilterButton(int pX, int pY, int pWidth, int pHeight, Filter<SRC> filter, Screen parentScreen, DataManagerWidget.ViewChangeHandler<SRC> changeHandler) {
        super(pX, pY, pWidth, pHeight, btn -> {
            DialogScreen screen = filter.getUi().createFilterDialog(parentScreen, changeHandler);
            Minecraft.getInstance().setScreen(screen);
        }, filter.type().getComponentKey(), filter.getUi().getFilterValueForDisplay(), filter.isRemovable());
    }
}
