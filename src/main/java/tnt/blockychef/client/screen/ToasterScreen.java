package tnt.blockychef.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import squeek.appleskin.helpers.KeyHelper;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.ToasterBlockEntity;
import tnt.blockychef.common.menu.ToasterMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.packet.C2S_ToasterEvent;

public class ToasterScreen extends AbstractContainerScreen<ToasterMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/toaster.png");
    private static final Component TOAST_BUTTON = Component.translatable("screen.blockychef.toaster.widget.toast");

    private Button toastButton;
    private Button decreaseButton, increaseButton;

    public ToasterScreen(ToasterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 175;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        toastButton = addRenderableWidget(new Button.Builder(TOAST_BUTTON, this::toastButtonClicked)
                .pos(leftPos + 77, topPos + 67).size(92, 20).build()
        );
        decreaseButton = addRenderableWidget(new Button.Builder(Component.literal("-"), this::decreaseTimerClicked)
                .pos(leftPos + 126, topPos + 44).size(20, 20).build()
        );
        increaseButton = addRenderableWidget(new Button.Builder(Component.literal("+"), this::increaseTimerClicked)
                .pos(leftPos + 149, topPos + 44).size(20, 20).build()
        );

        updateButtonStatus();
    }

    @Override
    protected void containerTick() {
        updateButtonStatus();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
        int timer = menu.getBlockEntity().getToastingTimer() / 20;
        String text = String.valueOf(timer);
        int width = font.width(text);
        font.draw(poseStack, text, 77 + (49 - width) / 2.0F, 49, 0x404040);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    private void toastButtonClicked(Button button) {
        ToasterBlockEntity toaster = menu.getBlockEntity();
        toaster.setToasting(true);
        NetworkManager.dispatchServerPacket(C2S_ToasterEvent.toastEvent(toaster.getBlockPos(), true));
    }

    private void decreaseTimerClicked(Button button) {
        ToasterBlockEntity toaster = menu.getBlockEntity();
        int currentTime = toaster.getToastingTimer();
        if (currentTime <= ToasterBlockEntity.MIN_TIMER_VALUE) {
            return;
        }
        int add = scale(-ToasterBlockEntity.DEFAULT_TIMER_INCREMENT);
        toaster.setToastingTimer(currentTime + add);
        NetworkManager.dispatchServerPacket(C2S_ToasterEvent.timeEvent(toaster.getBlockPos(), toaster.getToastingTimer()));
    }

    private void increaseTimerClicked(Button button) {
        ToasterBlockEntity toaster = menu.getBlockEntity();
        int currentTime = toaster.getToastingTimer();
        if (currentTime >= ToasterBlockEntity.MAX_TIMER_VALUE) {
            return;
        }
        int add = scale(ToasterBlockEntity.DEFAULT_TIMER_INCREMENT);
        toaster.setToastingTimer(currentTime + add);
        NetworkManager.dispatchServerPacket(C2S_ToasterEvent.timeEvent(toaster.getBlockPos(), toaster.getToastingTimer()));
    }

    private int scale(int timer) {
        if (KeyHelper.isCtrlKeyDown()) {
            return timer < 0 ? -20 : 20;
        } else if (KeyHelper.isShiftKeyDown()) {
            return timer * 2;
        }
        return timer;
    }

    private void updateButtonStatus() {
        boolean status = !menu.getBlockEntity().isToasting();
        toastButton.active = status;
        decreaseButton.active = status;
        increaseButton.active = status;
    }
}
