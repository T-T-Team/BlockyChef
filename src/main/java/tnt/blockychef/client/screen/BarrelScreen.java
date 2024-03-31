package tnt.blockychef.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.BarrelBlockEntity;
import tnt.blockychef.common.menu.BarrelMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.C2S_InitiateRecipeProcessing;

public class BarrelScreen extends AbstractContainerScreen<BarrelMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/barrel.png");
    private static final Component TEXT_FERMENT = Component.translatable("screen.blockychef.barrel.widget.ferment");

    private Button fermentButton;

    public BarrelScreen(BarrelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 175;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        fermentButton = addRenderableWidget(Button.builder(TEXT_FERMENT, this::mixButtonClicked)
                .pos(leftPos + 89, topPos + 64)
                .size(80, 20)
                .build()
        );
        fermentButton.active = false;
    }

    @Override
    protected void containerTick() {
        BarrelBlockEntity barrel = menu.getBlockEntity();
        fermentButton.active = barrel.hasRecipe() && !barrel.isFermenting();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        BarrelBlockEntity blockEntity = menu.getBlockEntity();
        float mixProgress = blockEntity.getFermentingProgress(partialTicks);
        int arrowWidth = (int) (mixProgress * 26);
        graphics.blit(TEXTURE, leftPos + 75, topPos + 43, 176, 0, arrowWidth, 12);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void mixButtonClicked(Button button) {
        BarrelBlockEntity blockEntity = menu.getBlockEntity();
        blockEntity.startProcessing();
        NetworkManager.DISPATCHER.sendToServer(new C2S_InitiateRecipeProcessing(blockEntity.getBlockPos()));
    }
}
