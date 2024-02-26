package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.GraphicsHelper;

public class ColorButton extends AbstractButton {

    private final ButtonPressHandler pressHandler;
    private int primaryColor;
    private int primaryColorHover;
    private int primaryColorDisabled;
    private int secondaryColor;
    private int secondaryColorHover;
    private int secondaryColorDisabled;
    private int textColor;
    private int textColorHover;
    private int textColorDisabled;
    private boolean textShadow;
    private int borderSize;

    public ColorButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, ButtonPressHandler pressHandler) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.pressHandler = pressHandler;
        this.primaryColor = 0xFF << 24;
        this.primaryColorHover = primaryColor;
        this.primaryColorDisabled = primaryColor;
        this.secondaryColor = 0xFFFFFFFF;
        this.secondaryColorHover = secondaryColor;
        this.secondaryColorDisabled = secondaryColor;
        this.textColor = 0xFFFFFF;
        this.textColorHover = 0xFFFF00;
        this.textColorDisabled = 0x555555;
        this.borderSize = 1;
    }

    public void setPrimaryColorSchema(int color, int hoverColor, int disabledColor) {
        this.primaryColor = color;
        this.primaryColorHover = hoverColor;
        this.primaryColorDisabled = disabledColor;
    }

    public void setSecondaryColorSchema(int color, int hoverColor, int disabledColor) {
        this.secondaryColor = color;
        this.secondaryColorHover = hoverColor;
        this.secondaryColorDisabled = disabledColor;
    }

    public void setTextColorSchema(int color, int hoverColor, int disabledColor) {
        this.textColor = color;
        this.textColorHover = hoverColor;
        this.textColorDisabled = disabledColor;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public void setTextShadow(boolean textShadow) {
        this.textShadow = textShadow;
    }

    @Override
    public void onPress() {
        pressHandler.onPressed(this);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int secondary = selectColor(secondaryColor, secondaryColorHover, secondaryColorDisabled);
        pGuiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), secondary);
        int primary = selectColor(primaryColor, primaryColorHover, primaryColorDisabled);
        pGuiGraphics.fill(getX() + borderSize, getY() + borderSize, getX() + getWidth() - borderSize, getY() + getHeight() - borderSize, primary);
        drawText(pGuiGraphics, pMouseX, pMouseY, pPartialTick, selectColor(textColor, textColorHover, textColorDisabled));
    }

    protected void drawText(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, int color) {
        GraphicsHelper.drawCenteredText(pGuiGraphics, getMessage(), Minecraft.getInstance().font, getX(), getY(), getWidth(), getHeight(), selectColor(textColor, textColorHover, textColorDisabled), textShadow);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    protected int selectColor(int main, int hover, int disabled) {
        return active ? (isHovered ? hover : main) : disabled;
    }

    protected boolean isTextShadow() {
        return textShadow;
    }
}
