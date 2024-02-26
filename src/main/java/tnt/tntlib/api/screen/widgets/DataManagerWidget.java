package tnt.tntlib.api.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.data.*;
import tnt.tntlib.api.screen.FilterSelectDialogScreen;
import tnt.tntlib.api.screen.SorterSelectDialogScreen;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DataManagerWidget<SRC> extends ContainerWidget {

    public static final Component FILTER_LABEL = Component.translatable("label.filters");
    public static final Component ADD_FILTER_LABEL = Component.translatable("label.filters.add");
    public static final Component SORTER_LABEL = Component.translatable("label.sorters");
    public static final Component ADD_SORTER_LABEL = Component.translatable("label.sorters.add");

    private final DataManager<SRC> dataManager;
    private boolean filteringEnabled = true;
    private boolean sortingEnabled = true;

    public DataManagerWidget(int pX, int pY, int pWidth, int pHeight, DataManagerProperties<SRC> properties, List<SRC> data, Screen parentScreen) {
        super(pX, pY, pWidth, pHeight);
        this.dataManager = new DataManager<>(properties.view(), data).withDataChangeListener((adjustedData, dataview) -> {
            Set<Filter<SRC>> filters = dataview.filters();
            Set<Sorter<SRC>> sorters = dataview.sorters();
            Font font = Minecraft.getInstance().font;
            int startY = pY;
            clear();
            ViewChangeHandler<SRC> viewChangeHandler = consumer -> {
                consumer.accept(dataview);
                setView(dataview);
            };
            if (filteringEnabled) {
                int labelSize = font.width(FILTER_LABEL);
                LabelWidget label = addRenderableWidget(new LabelWidget(pX, startY, labelSize, 20, FILTER_LABEL, font));
                label.setPadding(0);

                Collection<FilterType<SRC>> availableFilters = properties.availableFilters();
                if (availableFilters.size() > 0 && filters.size() < availableFilters.size()) {
                    ColorButton button = addRenderableWidget(new ColorButton(pX + labelSize + 10, startY, font.width(ADD_FILTER_LABEL) + 10, 20, ADD_FILTER_LABEL, btn -> {
                        Minecraft mc = Minecraft.getInstance();
                        FilterSelectDialogScreen<SRC> filterSelect = new FilterSelectDialogScreen<>(parentScreen, availableFilters, filters, viewChangeHandler);
                        mc.setScreen(filterSelect);
                    }));
                    button.setPrimaryColorSchema(0, 0, 0);
                    button.setSecondaryColorSchema(0, 0x44FFFFFF, 0);
                }
                startY += 25;
                if (filters.size() > 0) {
                    int left = pX;
                    for (Filter<SRC> filter : filters) {
                        UIFactory<SRC> factory = filter.getUi();
                        int width = factory.getElementWidth(font);
                        if (left + width > pWidth) {
                            left = pX;
                            startY += 25;
                        }
                        addRenderableWidget(factory.createGuiWidget(left, startY, width, 20, parentScreen, viewChangeHandler));
                        if (filter.isRemovable()) {
                            ColorButton x = addRenderableWidget(new ColorButton(left + width - 20, startY, 20, 20, Component.literal("x"), btn -> {
                                filters.remove(filter);
                                setView(dataview);
                            }));
                            x.setPrimaryColorSchema(0, 0, 0);
                            x.setSecondaryColorSchema(0, 0, 0);
                            x.setTextColorSchema(0xFFFFFF, 0xAA0000, 0xFFFFFF);
                        }
                        left += width + 5;
                    }
                    startY += 25;
                }
            }
            if (sortingEnabled) {
                int labelSize = font.width(SORTER_LABEL);
                LabelWidget label = addRenderableWidget(new LabelWidget(pX, startY, labelSize, 20, SORTER_LABEL, font));
                label.setPadding(0);

                Collection<SorterType<SRC>> availableSorters = properties.availableSorters();
                if (availableSorters.size() > 0 && sorters.size() < availableSorters.size()) {
                    ColorButton button = addRenderableWidget(new ColorButton(pX + labelSize + 10, startY, font.width(ADD_SORTER_LABEL) + 10, 20, ADD_SORTER_LABEL, btn -> {
                        Minecraft mc = Minecraft.getInstance();
                        SorterSelectDialogScreen<SRC> sorterSelect = new SorterSelectDialogScreen<>(parentScreen, availableSorters, sorters, viewChangeHandler);
                        mc.setScreen(sorterSelect);
                    }));
                    button.setPrimaryColorSchema(0, 0, 0);
                    button.setSecondaryColorSchema(0, 0x44FFFFFF, 0);
                }

                startY += 25;
                if (sorters.size() > 0) {
                    int left = pX;
                    for (Sorter<SRC> sorter : sorters) {
                        UIFactory<SRC> factory = sorter.getUi();
                        int width = factory.getElementWidth(font);
                        if (left + width > pWidth) {
                            left = pX;
                            startY += 25;
                        }
                        addRenderableWidget(factory.createGuiWidget(left, startY, width, 20, parentScreen, viewChangeHandler));
                        if (sorter.isRemovable()) {
                            ColorButton x = addRenderableWidget(new ColorButton(left + width - 20, startY, 20, 20, Component.literal("x"), btn -> {
                                sorters.remove(sorter);
                                setView(dataview);
                            }));
                            x.setPrimaryColorSchema(0, 0, 0);
                            x.setSecondaryColorSchema(0, 0, 0);
                            x.setTextColorSchema(0xFFFFFF, 0xAA0000, 0xFFFFFF);
                        }
                        left += width + 5;
                    }
                    startY += 25;
                }
            }
            properties.event().onDataReady(adjustedData, dataview, new Rect2i(pX, startY, pWidth, pHeight - (startY - pY)), this::addRenderableWidget);
        });
    }

    public void setDataManagerProperties(boolean allowFilters, boolean allowSorting) {
        this.filteringEnabled = allowFilters;
        this.sortingEnabled = allowSorting;
    }

    public void addFilter(Filter<SRC> filter) {
        if (!filteringEnabled) {
            throw new UnsupportedOperationException("This data manager does not support filters");
        }
        this.dataManager.addFilter(filter);
    }

    public void removeFilter(Filter<SRC> filter) {
        this.dataManager.removeFilter(filter);
    }

    public void addSorter(Sorter<SRC> sorter) {
        if (!sortingEnabled) {
            throw new UnsupportedOperationException("This data manager does not support sorting");
        }
        this.dataManager.addSorter(sorter);
    }

    public void removeSorter(Sorter<SRC> sorter) {
        this.dataManager.removeSorter(sorter);
    }

    public void setView(View<SRC> view) {
        dataManager.setView(view);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public record DataManagerProperties<SRC>(View<SRC> view, DataReady<SRC> event, Collection<FilterType<SRC>> availableFilters, Collection<SorterType<SRC>> availableSorters) {}

    @FunctionalInterface
    public interface DataReady<SRC> {
        void onDataReady(List<SRC> data, View<SRC> view, Rect2i canvas, WidgetHandler handler);
    }

    @FunctionalInterface
    public interface WidgetHandler {
        <W extends AbstractWidget> W addWidget(W widget);
    }

    @FunctionalInterface
    public interface ViewChangeHandler<SRC> {
        void consume(Consumer<View<SRC>> viewConsumer);
    }
}
