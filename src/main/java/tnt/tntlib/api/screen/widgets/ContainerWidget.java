package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;

import java.util.ArrayList;
import java.util.List;

public class ContainerWidget extends AbstractWidget implements ContainerEventHandler {

    private final List<GuiEventListener> listeners = new ArrayList<>();
    private final List<AbstractWidget> widgets = new ArrayList<>();
    private GuiEventListener focused;
    private boolean dragging;

    public ContainerWidget(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY);
    }

    public <W extends AbstractWidget> W addRenderableWidget(W widget) {
        widgets.add(widget);
        return addGuiEventListener(widget);
    }

    public <L extends GuiEventListener> L addGuiEventListener(L listener) {
        this.listeners.add(listener);
        return listener;
    }

    public void clear() {
        listeners.clear();
        widgets.clear();
        focused = null;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderChildren(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderChildren(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        widgets.forEach(widget -> widget.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick));
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        boolean result = ContainerEventHandler.super.mouseClicked(pMouseX, pMouseY, pButton);
        if (!result && this.focused != null) {
            this.setFocused(null);
        }
        return result;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDeltaX, double pDeltaY) {
        return ContainerEventHandler.super.mouseScrolled(pMouseX, pMouseY, pDeltaX, pDeltaY);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        super.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return ContainerEventHandler.super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return ContainerEventHandler.super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return listeners;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    public GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(GuiEventListener focused) {
        if (this.focused != null) {
            this.focused.setFocused(false);
        }
        if (focused != null) {
            focused.setFocused(true);
        }
        this.focused = focused;
    }

    @Override
    public void setX(int pX) {
        int diff = getX() - pX;
        super.setX(pX);
        widgets.forEach(widget -> widget.setX(widget.getX() + diff));
    }

    @Override
    public void setY(int pY) {
        int diff = getY() - pY;
        super.setY(pY);
        widgets.forEach(widget -> widget.setY(widget.getY() + diff));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    protected List<GuiEventListener> getListeners() {
        return listeners;
    }

    protected List<AbstractWidget> getWidgets() {
        return widgets;
    }
}
