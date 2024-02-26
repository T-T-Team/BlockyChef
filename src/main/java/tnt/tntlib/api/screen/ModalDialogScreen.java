package tnt.tntlib.api.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

public abstract class ModalDialogScreen extends DialogScreen {

    protected int dialogWidth = 150;
    protected int dialogHeight = 80;
    protected int leftPosition;
    protected int topPosition;

    public ModalDialogScreen(Screen parentScreen, Component title) {
        super(parentScreen, title);
    }

    @Override
    protected void init() {
        parentScreen.init(minecraft, width, height);
        this.leftPosition = (width - dialogWidth) / 2;
        this.topPosition = (height - dialogHeight) / 2;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderParentScreenBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        PoseStack stack = pGuiGraphics.pose();
        stack.pushPose();
        stack.translate(0, 0, zIndexOffset());
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        stack.popPose();
    }

    @Override
    protected void drawDefaultBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground(graphics, mouseX, mouseY, partialTicks);
        graphics.fill(leftPosition, topPosition, leftPosition + dialogWidth, topPosition + dialogHeight, 0xFFFFFFFF);
        graphics.fill(leftPosition + 1, topPosition + 1, leftPosition + dialogWidth - 1, topPosition + dialogHeight -1, 0xFF000000);
        GraphicsHelper.drawAlignedText(graphics, title, font, HorizontalAlignment.CENTER, VerticalAlignment.TOP, leftPosition, topPosition, dialogWidth, dialogHeight, 0xFFFFFF, false, 0, 5);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return pMouseX >= leftPosition && pMouseX <= leftPosition + dialogWidth && pMouseY >= topPosition && pMouseY <= topPosition + dialogHeight;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (shouldHandleOutsideClick(mouseX, mouseY, button) && !isMouseOver(mouseX, mouseY)) {
            return handleClickedOutside(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected boolean shouldHandleOutsideClick(double mouseX, double mouseY, int button) {
        return true;
    }

    protected boolean handleClickedOutside(double mouseX, double mouseY, int button) {
        cancel();
        return true;
    }

    protected void renderParentScreenBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.parentScreen.render(graphics, mouseX, mouseY, partialTicks);
    }

    protected int zIndexOffset() {
        return 999;
    }

    protected void addDefaultControlButtons(int margin) {
        int availableWidth = dialogWidth - margin * 3;
        int singleButtonWidth = availableWidth / 2;
        addRenderableWidget(new Button.Builder(CommonComponents.GUI_CANCEL, btn -> cancel())
                .size(singleButtonWidth, 20)
                .pos(leftPosition + margin, topPosition + dialogHeight - 20 - margin)
                .build()
        );
        addRenderableWidget(new Button.Builder(CommonComponents.GUI_PROCEED, btn -> confirm())
                .size(singleButtonWidth, 20)
                .pos(leftPosition + dialogWidth - margin - singleButtonWidth, topPosition + dialogHeight - 20 - margin)
                .build()
        );
    }
}
