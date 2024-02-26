package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.gui.components.AbstractWidget;

public class GridWidget extends ContainerWidget {

    private int margin = 30;

    private int columns;
    private int rows;

    public GridWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
        calculateDimensions();
    }

    public void setMargin(int margin) {
        this.margin = margin;
        calculateDimensions();
    }

    @Override
    public <W extends AbstractWidget> W addRenderableWidget(W widget) {
        int widgetIndex = getWidgets().size();
        int widgetX = getX() + (widgetIndex % columns) * margin;
        int widgetY = getY() + (widgetIndex / columns) * margin;
        widget.setX(widgetX);
        widget.setY(widgetY);
        return super.addRenderableWidget(widget);
    }

    @Override
    public void setX(int pX) {
        super.setX(pX);
        calculateDimensions();
    }

    @Override
    public void setY(int pY) {
        super.setY(pY);
        calculateDimensions();
    }

    @Override
    public void setWidth(int pWidth) {
        super.setWidth(pWidth);
        calculateDimensions();
    }

    @Override
    public void setHeight(int value) {
        super.setHeight(value);
        calculateDimensions();
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getTotalRowCountFor(int elementCount) {
        int remainder = elementCount % columns > 0 ? 1 : 0;
        return elementCount / columns + remainder;
    }

    private void calculateDimensions() {
        columns = getWidth() / margin;
        rows = getHeight() / margin;
    }
}
