package tnt.tntlib.api.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public abstract class DialogScreen extends Screen {

    protected final Screen parentScreen;

    protected DialogResponseEvent confirmEventHandler = DialogResponseEvent.NO_RESPONSE;
    protected DialogResponseEvent cancelEventHandler = DialogResponseEvent.NO_RESPONSE;

    public DialogScreen(Screen parentScreen, Component title) {
        super(title);
        this.parentScreen = parentScreen;
    }

    protected void confirm() {
        confirmEventHandler.handleResponse(this);
        if (displayParentOnCloseEvent()) {
            displayParentScreen();
        }
    }

    protected void cancel() {
        cancelEventHandler.handleResponse(this);
        if (displayParentOnCloseEvent()) {
            displayParentScreen();
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        drawDefaultBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.allowKeyboardInteractions()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.cancel();
                return true;
            } else if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            } else {
                this.confirm();
                return true;
            }
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void displayParentScreen() {
        minecraft.setScreen(parentScreen);
    }

    protected boolean allowKeyboardInteractions() {
        return true;
    }

    protected boolean displayParentOnCloseEvent() {
        return true;
    }

    protected void drawDefaultBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
    }

    @FunctionalInterface
    public interface DialogResponseEvent {

        DialogResponseEvent NO_RESPONSE = dialog -> {};

        void handleResponse(DialogScreen dialog);
    }
}
