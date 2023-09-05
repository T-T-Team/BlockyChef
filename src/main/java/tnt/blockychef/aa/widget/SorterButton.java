package tnt.blockychef.aa.widget;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import tnt.blockychef.aa.data.Sorter;

public class SorterButton<SRC> extends ColorButton {

    public static final Component ASC = Component.translatable("sorter.value.asc");
    public static final Component DESC = Component.translatable("sorter.value.desc");
    private final Component key;
    private final Component value;

    public SorterButton(int pX, int pY, int pWidth, int pHeight, Sorter<SRC> sorter, Runnable updateTrigger) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY, btn -> {
            boolean val = sorter.isAscendingOrder();
            sorter.setAscendingOrder(!val);
            updateTrigger.run();
        });
        this.key = sorter.type().getComponentKey();
        this.value = sorter.isAscendingOrder() ? ASC : DESC;
        updateText();
    }

    protected void updateText() {
        setMessage(Component.literal(key.getString() + " - " + value.getString()));
    }
}
