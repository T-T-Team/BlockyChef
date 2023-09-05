package tnt.blockychef.aa.data;

import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public interface Filter<SRC> extends DataComponent<SRC>, Predicate<SRC> {

    FilterType<?> type();

    @Override
    FilterUiFactory<SRC> getUi();

    interface FilterUiFactory<SRC> extends UIFactory<SRC> {

        Component getFilterValueForDisplay();
    }
}
