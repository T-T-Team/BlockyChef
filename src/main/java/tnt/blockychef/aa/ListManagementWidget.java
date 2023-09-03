package tnt.blockychef.aa;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;
import tnt.tntlib.api.screen.widgets.ContainerWidget;
import tnt.tntlib.api.screen.widgets.LabelWidget;

public class ListManagementWidget extends ContainerWidget {

    private final Runnable onClick;

    public ListManagementWidget(int x, int y, int width, int height, Component name, Component value, boolean removable, Runnable onClick) {
        super(x, y, width, height);
        this.onClick = onClick;
        Font font = Minecraft.getInstance().font;
        int realWidth = removable ? (width - 20) : width;
        LabelWidget keyWidget = addRenderableWidget(new LabelWidget(x, y, realWidth, height, name, font));
        keyWidget.setHorizontalAlignment(HorizontalAlignment.LEFT);
        keyWidget.setVerticalAlignment(VerticalAlignment.CENTER);
        keyWidget.setPadding(4);
        LabelWidget valueWidget = addRenderableWidget(new LabelWidget(x, y, realWidth, height, value, font));
        valueWidget.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        valueWidget.setVerticalAlignment(VerticalAlignment.CENTER);
        valueWidget.setPadding(4);
        if (removable) {
            addRenderableWidget(new SimpleButton(x + realWidth, y, 20, 20, "x", t -> {})); // TODO remove
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        boolean result = super.mouseClicked(pMouseX, pMouseY, pButton);
        if (!result && isMouseOver(pMouseX, pMouseY)) {
            if (isValidClickButton(pButton) && clicked(pMouseX, pMouseY)) {
                playDownSound(Minecraft.getInstance().getSoundManager());
                onClick.run();
                return true;
            }
        }
        return result;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFFFFFFFF);
        pGuiGraphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, 0xFF << 24);
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private static final class SimpleButton extends Button {

        public SimpleButton(int pX, int pY, int pWidth, int pHeight, String text, OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, Component.literal(text), pOnPress, pMessageSupplier -> Component.literal(text));
        }

        @Override
        protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            GraphicsHelper.drawCenteredText(pGuiGraphics, getMessage(), Minecraft.getInstance().font, getX(), getY(), getWidth(), getHeight(), 0xFFFFFF);
        }
    }
}
