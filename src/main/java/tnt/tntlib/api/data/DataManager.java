package tnt.tntlib.api.data;

import java.util.List;
import java.util.Objects;

public class DataManager<SRC> {

    private View<SRC> view;
    private List<SRC> data;
    private List<SRC> mutatedData;

    private DataChangeListener<SRC> dataChangeListener;

    public DataManager(View<SRC> view, List<SRC> data) {
        this.view = Objects.requireNonNull(view).copy();
        this.data = Objects.requireNonNull(data);
        refreshData();
    }

    public DataManager<SRC> withDataChangeListener(DataChangeListener<SRC> listener) {
        this.dataChangeListener = listener;
        refreshData();
        return this;
    }

    public void addFilter(Filter<SRC> filter) {
        this.view.filters().add(filter);
        setView(view);
    }

    public void removeFilter(Filter<SRC> filter) {
        this.view.filters().remove(filter);
        setView(view);
    }

    public void addSorter(Sorter<SRC> sorter) {
        this.view.sorters().add(sorter);
        setView(view);
    }

    public void removeSorter(Sorter<SRC> sorter) {
        this.view.sorters().remove(sorter);
        setView(view);
    }

    public void setData(List<SRC> data) {
        this.data = data;
        refreshData();
    }

    public void setView(View<SRC> view) {
        this.view = view;
        refreshData();
    }

    public List<SRC> getAdjustedData() {
        return mutatedData;
    }

    public DataChangeListener<SRC> getDataChangeListener() {
        return dataChangeListener;
    }

    protected void refreshData() {
        this.mutatedData = view.apply(data.stream()).toList();
        invokeDataChangeEvent();
    }

    protected void invokeDataChangeEvent() {
        if (dataChangeListener != null) {
            dataChangeListener.onDataChanged(mutatedData, view);
        }
    }

    @FunctionalInterface
    public interface DataChangeListener<SRC> {
        void onDataChanged(List<SRC> data, View<SRC> usedView);
    }
}
