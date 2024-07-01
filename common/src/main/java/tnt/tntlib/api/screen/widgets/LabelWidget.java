package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

import java.util.Objects;

public class LabelWidget extends AbstractWidget {

    private final Font font;
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    private int textColor = 0xFFFFFF;
    private boolean shadow;
    private int padding = 5;

    public LabelWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, Font font) {
        super(pX, pY, pWidth, pHeight, Objects.requireNonNull(pMessage));
        this.font = font;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        GraphicsHelper.drawAlignedText(pGuiGraphics, getMessage(), font, horizontalAlignment, verticalAlignment, getX(), getY(), getWidth(), getHeight(), textColor, shadow, horizontalAlignment.adjustPadding(padding), verticalAlignment.adjustPadding(padding));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, getMessage());
    }

    @Override
    protected boolean isValidClickButton(int pButton) {
        return false;
    }
}
