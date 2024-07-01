package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;

import java.util.function.IntSupplier;

public class ScrollbarWidget extends AbstractWidget {

    private final IntSupplier currentIndex;
    private final IntSupplier totalSize;
    private final IntSupplier displaySize;

    public ScrollbarWidget(int pX, int pY, int pWidth, int pHeight, IntSupplier currentIndex, IntSupplier totalSize, IntSupplier displaySize) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY);
        this.currentIndex = currentIndex;
        this.totalSize = totalSize;
        this.displaySize = displaySize;
    }

    @Override
    protected boolean isValidClickButton(int pButton) {
        return false; // TODO implement navigation via clicking
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return false; // TODO implement dragging
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int index = currentIndex.getAsInt();
        int max = totalSize.getAsInt();
        int display = displaySize.getAsInt();
        double step = 1.0 / max * getHeight();
        int scrollbarStart = Mth.floor(index * step);
        int scrollbarLength = Mth.ceil(display * step);
        int x1 = getX();
        int x2 = x1 + getWidth();
        int y1 = getY();
        int y2 = y1 + getHeight();
        graphics.fill(x1, y1, x2, y2, 0xFF333333);
        graphics.fill(x1, y1 + scrollbarStart, x2, y1 + scrollbarStart + scrollbarLength, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }
}
