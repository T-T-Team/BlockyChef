package tnt.blockychef.aa.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import tnt.blockychef.aa.data.Filter;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

public class FilterButton<SRC> extends ColorButton {

    private final Component key;
    private final Component value;
    private final boolean removable;

    public FilterButton(int pX, int pY, int pWidth, int pHeight, Filter<SRC> filter, Runnable updateTrigger) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY, btn -> {
            // TODO open filter edit dialog
            updateTrigger.run();
        });
        this.key = filter.type().getComponentKey();
        this.value = filter.getUi().getFilterValueForDisplay();
        this.removable = filter.isRemovable();
        updateText();
    }

    @Override
    protected void drawText(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, int color) {
        int xOffset = removable ? -10 : 0;
        GraphicsHelper.drawAlignedText(pGuiGraphics, getMessage(), Minecraft.getInstance().font, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, getX(), getY(), getWidth(), getHeight(), color, isTextShadow(), xOffset, 0);
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        if (removable) {
            return active && visible && pMouseX >= getX() && pMouseY >= getY() && pMouseX < getX() + width - 20 && pMouseY < getY() + height;
        }
        return super.clicked(pMouseX, pMouseY);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if (removable) {
            return active && visible && pMouseX >= getX() && pMouseY >= getY() && pMouseX < getX() + width - 20 && pMouseY < getY() + height;
        }
        return super.isMouseOver(pMouseX, pMouseY);
    }

    protected void updateText() {
        setMessage(Component.literal(key.getString() + " - " + value.getString()));
    }
}
