package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.gui.components.AbstractWidget;
import tnt.tntlib.api.UiHelper;

import java.util.List;

public class ComponentListWidget<T> extends ContainerWidget {

    private final WidgetFactory<T> factory;
    private List<T> data;
    private List<T> oldData;
    private int index;
    private int displayedCount;

    public ComponentListWidget(int pX, int pY, int pWidth, int pHeight, List<T> data, WidgetFactory<T> factory) {
        super(pX, pY, pWidth, pHeight);
        this.data = data;
        this.oldData = data;
        this.factory = factory;
        init();
    }

    public void setData(List<T> data) {
        this.data = data;
        this.oldData = data;
        init();
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return UiHelper.handleMouseScrolled(pDelta, index, displayedCount, data.size(), newIndex -> {
            this.index = newIndex;
            init();
        });
    }

    public void init() {
        clear();
        int scrollbarWidth = 4;
        int realWidth = data.size() > displayedCount ? width - scrollbarWidth : width;
        displayedCount = 0;
        int offset = 0;
        List<T> actualData = data;
        if (actualData.size() != oldData.size()) {
            index = 0;
        }
        for (int i = index; i < actualData.size(); i++) {
            T dataElement = data.get(i);
            AbstractWidget widget = factory.create(getX(), getY() + offset, realWidth, dataElement);
            if (widget == null || offset + widget.getHeight() > height) {
                break;
            }
            offset += widget.getHeight();
            displayedCount++;
            addRenderableWidget(widget);
        }
        oldData = actualData;
        if (data.size() > displayedCount) {
            addRenderableWidget(new ScrollbarWidget(getX() + width - scrollbarWidth, getY(), scrollbarWidth, getHeight(), () -> index, actualData::size, () -> displayedCount));
        }
    }
}
