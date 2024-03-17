package tnt.blockychef.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.DoughMakerBlockEntity;
import tnt.blockychef.common.menu.DoughMakerMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.C2S_InitiateRecipeProcessing;

public class DoughMakerScreen extends AbstractContainerScreen<DoughMakerMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/dough_maker.png");
    private static final Component TEXT_PROCESS = Component.translatable("screen.blockychef.dough_maker.widget.process");

    private Button processButton;

    public DoughMakerScreen(DoughMakerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 175;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        processButton = addRenderableWidget(Button.builder(TEXT_PROCESS, this::processButtonClicked)
                .pos(leftPos + 89, topPos + 64)
                .size(80, 20)
                .build()
        );
        processButton.active = false;
    }

    @Override
    protected void containerTick() {
        DoughMakerBlockEntity doughMaker = menu.getBlockEntity();
        processButton.active = doughMaker.hasRecipe() && !doughMaker.isProcessing();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        DoughMakerBlockEntity blockEntity = menu.getBlockEntity();
        float mixProgress = blockEntity.getProcessingProgress(partialTicks);
        int arrowWidth = (int) (mixProgress * 26);
        graphics.blit(TEXTURE, leftPos + 75, topPos + 43, 176, 0, arrowWidth, 12);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void processButtonClicked(Button button) {
        DoughMakerBlockEntity blockEntity = menu.getBlockEntity();
        blockEntity.startProcessing();
        NetworkManager.DISPATCHER.sendToServer(new C2S_InitiateRecipeProcessing(blockEntity.getBlockPos()));
    }
}