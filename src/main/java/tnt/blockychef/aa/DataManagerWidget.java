package tnt.blockychef.aa;

import tnt.tntlib.api.screen.widgets.ContainerWidget;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class DataManagerWidget<T> extends ContainerWidget {

    private BiConsumer<DataManagerWidget<T>, List<T>> dataHandler = (mng, list) -> {};
    private View<T> usedView;

    public DataManagerWidget(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
    }

    public void setDataHandler(BiConsumer<DataManagerWidget<T>, List<T>> dataHandler) {
        this.dataHandler = dataHandler;
    }

    public void setView(View<T> view) {
        usedView = view;
        // TODO redo data
    }

    public View<T> getView() {
        return usedView;
    }

    public record View<T>(@Nullable String name, boolean system, List<?> filters, List<?> sorters) {
    }
}
