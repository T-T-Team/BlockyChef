package tnt.blockychef.aa;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.screen.widgets.ContainerWidget;
import tnt.tntlib.api.screen.widgets.LabelWidget;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class DataManagerWidget<T> extends ContainerWidget {

    private BiConsumer<DataManagerWidget<T>, List<T>> dataHandler = (mng, list) -> {};
    private Consumer<DataManagerWidget<T>> refreshHandler = mng -> {};
    private View<T> usedView;
    private boolean filteringAllowed;
    private boolean sortingAllowed;
    private boolean viewManagementAllowed;
    private final DataSorters<T> sorters = new DataSorters<>();

    private List<T> baseData;
    private List<T> cachedData;
    private int managementPanelHeight;

    public DataManagerWidget(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
    }

    public void setData(List<T> data) {
        this.baseData = data;
        setView(usedView);
    }

    public void setDataHandler(BiConsumer<DataManagerWidget<T>, List<T>> dataHandler) {
        this.dataHandler = dataHandler;
    }

    public void setRefreshHandler(Consumer<DataManagerWidget<T>> refreshHandler) {
        this.refreshHandler = refreshHandler;
    }

    public void setFilteringMode(boolean allow) {
        this.filteringAllowed = allow;
    }

    public void setSortingMode(boolean allow) {
        this.sortingAllowed = allow;
    }

    public void setViewManagementMode(boolean allow) {
        this.viewManagementAllowed = allow;
    }

    public void setView(View<T> view) {
        usedView = view;
        clear();
        int panelTop = 0;
        int left = getX();
        Font font = Minecraft.getInstance().font;
        if (filteringAllowed) {
            Component text = Component.translatable("label.filters");
            addRenderableWidget(new LabelWidget(getX(), getY() + panelTop, font.width(text), 20, text, font));
            panelTop += 20;
        }
        if (sortingAllowed) {
            Component text = Component.translatable("label.sorting");
            addRenderableWidget(new LabelWidget(getX(), getY() + panelTop, font.width(text), 20, text, font));
            panelTop += 20;
        }
        for (DataSorters.Sorter<T> sorter : view.sorters.values()) {
            int width = sorter.guiWidth(font);
            if (left + width > getX() + getWidth()) {
                panelTop += 25;
                left = getX();
            }
            addRenderableWidget(sorter.convertToGui(left, getY() + panelTop, width, 20, this::refreshData));
            left += width + 5;
        }
        managementPanelHeight = panelTop + 25;
        if (baseData == null) {
            return;
        }
        cachedData = view.apply(baseData);
        if (dataHandler != null) {
            dataHandler.accept(this, cachedData);
        }
    }

    public View<T> getView(@Nullable String name) {
        return usedView.copy(name);
    }

    public Rect2i getCanvas() {
        return new Rect2i(
                getX(), getY() + managementPanelHeight, getWidth(), getHeight() - managementPanelHeight
        );
    }

    public record View<T>(@Nullable String name, boolean system, List<?> filters, DataSorters<T> sorters) implements UnaryOperator<List<T>> {
        @Override
        public List<T> apply(List<T> ts) {
            Stream<T> stream = ts.stream();
            Comparator<T> sort = sorters.getComparator();
            if (sort != null) {
                stream = stream.sorted(sort);
            }
            return stream.toList();
        }

        public View<T> copy(@Nullable String name) {
            return new View<>(name, false, filters(), sorters().copy());
        }
    }

    public void refreshData() {
        if (refreshHandler != null) {
            refreshHandler.accept(this);
        }
    }
}
