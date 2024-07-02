package tnt.tntlib.api.data;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tnt.tntlib.api.screen.DialogScreen;
import tnt.tntlib.api.screen.widgets.DataManagerWidget;

import java.util.function.Predicate;

public interface Filter<SRC> extends DataComponent<SRC>, Predicate<SRC> {

    FilterType<?> type();

    @OnlyIn(Dist.CLIENT)
    @Override
    FilterUiFactory<SRC> getUi();

    interface FilterUiFactory<SRC> extends UIFactory<SRC> {

        @OnlyIn(Dist.CLIENT)
        Component getFilterValueForDisplay();

        @OnlyIn(Dist.CLIENT)
        DialogScreen createFilterDialog(Screen parentScreen, DataManagerWidget.ViewChangeHandler<SRC> viewChangeHandler);
    }
}
