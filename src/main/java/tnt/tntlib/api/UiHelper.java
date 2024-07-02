package tnt.tntlib.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public final class UiHelper {

    public static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(new ResourceLocation("widget/button"), new ResourceLocation("widget/button_disabled"), new ResourceLocation("widget/button_highlighted"));

    public static boolean handleMouseScrolled(double amount, int currScrollIndex, int displaySize, int dataSize, IntConsumer onScroll) {
        int next = currScrollIndex - (int) amount;
        if (next >= 0 && next <= dataSize - displaySize) {
            onScroll.accept(next);
            return true;
        }
        if (currScrollIndex < 0 || currScrollIndex > dataSize - displaySize) {
            onScroll.accept(0);
            return true;
        }
        return false;
    }

    public static void drawScrollbar(GuiGraphics graphics, int x, int y, int width, int height, int totalElements, int displayedElement, int currentIndex) {

    }

    public static Consumer<String> createEditboxResponderWithSuggestion(EditBox editbox, Component suggestion, Consumer<String> handler) {
        return string -> {
            if (string.isEmpty()) {
                editbox.setHint(suggestion);
            } else {
                editbox.setHint(null);
                handler.accept(string);
            }
        };
    }

    private UiHelper() {}
}
