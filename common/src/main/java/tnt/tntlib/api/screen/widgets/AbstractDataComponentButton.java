package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

public abstract class AbstractDataComponentButton extends ColorButton {

    protected final Component key;
    protected final Component value;
    private final boolean removable;

    public AbstractDataComponentButton(int x, int y, int width, int height, ButtonPressHandler pressHandler, Component key, Component value, boolean removable) {
        super(x, y, width, height, CommonComponents.EMPTY, pressHandler);
        this.key = key;
        this.value = value;
        this.removable = removable;
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

    public boolean isRemovable() {
        return removable;
    }
}
