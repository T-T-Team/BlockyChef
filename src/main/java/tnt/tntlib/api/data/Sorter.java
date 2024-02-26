package tnt.tntlib.api.data;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.screen.widgets.DataManagerWidget;
import tnt.tntlib.api.screen.widgets.SorterButton;

import java.util.Comparator;
import java.util.Objects;

public interface Sorter<SRC> extends DataComponent<SRC> {

    SorterType<?> type();

    boolean isAscendingOrder();

    void setAscendingOrder(boolean ascending);

    Comparator<SRC> getComparator();

    class SimpleSorter<SRC> implements Sorter<SRC> {

        private final SorterType<SRC> type;
        private final UIFactory<SRC> uiFactory;
        private final boolean removable;
        private boolean asc;

        public SimpleSorter(SorterType<SRC> type, boolean asc, boolean removable) {
            this.type = type;
            this.asc = asc;
            this.removable = removable;
            this.uiFactory = this.createUI();
        }

        protected UIFactory<SRC> createUI() {
            return new UIFactory<>() {
                @Override
                public int getElementWidth(Font font) {
                    Component content = Component.literal(type.getComponentKey().getString() + " - " + (asc ? SorterButton.ASC : SorterButton.DESC).getString());
                    int base = font.width(content) + 10;
                    if (removable) {
                        base += 20;
                    }
                    return base;
                }

                @Override
                public AbstractWidget createGuiWidget(int x, int y, int width, int height, Screen parent, DataManagerWidget.ViewChangeHandler<SRC> changeHandler) {
                    return new SorterButton<>(x, y, width, height, SimpleSorter.this, changeHandler);
                }
            };
        }

        @Override
        public SorterType<?> type() {
            return type;
        }

        @Override
        public boolean isAscendingOrder() {
            return asc;
        }

        @Override
        public void setAscendingOrder(boolean ascending) {
            asc = ascending;
        }

        @Override
        public Comparator<SRC> getComparator() {
            Comparator<SRC> baseComparator = type.getComparatorInstance();
            return asc ? baseComparator : baseComparator.reversed();
        }

        @Override
        public boolean isRemovable() {
            return removable;
        }

        @Override
        public Sorter<SRC> copy() {
            return new SimpleSorter<>(type, asc, removable);
        }

        @Override
        public UIFactory<SRC> getUi() {
            return uiFactory;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimpleSorter<?> that = (SimpleSorter<?>) o;
            return Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }
    }
}
