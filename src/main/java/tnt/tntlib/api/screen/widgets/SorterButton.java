package tnt.tntlib.api.screen.widgets;

import net.minecraft.network.chat.Component;
import tnt.tntlib.api.data.Sorter;

public class SorterButton<SRC> extends AbstractDataComponentButton {

    public static final Component ASC = Component.translatable("sorter.value.asc");
    public static final Component DESC = Component.translatable("sorter.value.desc");

    public SorterButton(int pX, int pY, int pWidth, int pHeight, Sorter<SRC> sorter, DataManagerWidget.ViewChangeHandler<SRC> handler) {
        super(pX, pY, pWidth, pHeight, btn -> {
            boolean val = sorter.isAscendingOrder();
            sorter.setAscendingOrder(!val);
            handler.consume(dataView -> {});
        }, sorter.type().getComponentKey(), sorter.isAscendingOrder() ? ASC : DESC, sorter.isRemovable());
    }
}
