package tnt.blockychef.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.menu.ToasterMenu;

public class ToasterScreen extends AbstractContainerScreen<ToasterMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/toaster.png");
    private static final Component TOAST_BUTTON = Component.translatable("screen.blockychef.toaster.widget.toast");

    public ToasterScreen(ToasterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 175;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new Button.Builder(TOAST_BUTTON, this::toastButtonClicked)
                .pos(leftPos + 97, topPos + 53).size(72, 20).build()
        );
        addRenderableWidget(new Button.Builder(Component.literal("<"), this::decreaseTimerClicked)
                .pos(leftPos + 7, topPos + 53).size(20, 20).build()
        );
        addRenderableWidget(new Button.Builder(Component.literal(">"), this::increaseTimerClicked)
                .pos(leftPos + 57, topPos + 53).size(20, 20).build()
        );
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    private void toastButtonClicked(Button button) {
        // TODO implement
    }

    private void decreaseTimerClicked(Button button) {
        // TODO implement
    }

    private void increaseTimerClicked(Button button) {
        // TODO implement
    }
}
