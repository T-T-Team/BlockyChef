package tnt.blockychef.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.CuttingBoardBlockEntity;
import tnt.blockychef.common.food.recipe.CuttingBoardRecipe;
import tnt.blockychef.common.menu.CuttingBoardMenu;

public class CuttingBoardScreen extends AbstractContainerScreen<CuttingBoardMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/cutting_board.png");
    private static final Component START_CUTTING = Component.translatable("screen.blockychef.cutting_board.widget.start_cutting");
    private static final Component STOP_CUTTING = Component.translatable("screen.blockychef.cutting_board.widget.stop_cutting");

    private Button cutButton;

    public CuttingBoardScreen(CuttingBoardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 195;
        this.inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        cutButton = addRenderableWidget(
                new Button.Builder(START_CUTTING, btn -> {})
                        .pos(leftPos + 61, topPos + 78)
                        .size(54, 20)
                        .build()
        );
        updateButtonLabelAndState();

        CuttingBoardBlockEntity blockEntity = menu.getBlockEntity();
        if (blockEntity.hasMultipleRecipes()) {
            // TODO add recipe switch buttons
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        CuttingBoardBlockEntity entity = menu.getBlockEntity();
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        float progress = entity.getProcessingProgress(partialTicks);
        int arrowWidth = (int) (progress * 26);
        blit(poseStack, leftPos + 75, topPos + 38, 176, 0, arrowWidth, 12);

        CuttingBoardRecipe recipe = entity.getRecipe();
        if (recipe != null) {
            ItemStack[] outputs = recipe.getOutputs();
            for (int i = 0; i < outputs.length; i++) {
                int slotIndex = CuttingBoardBlockEntity.SLOT_OUTPUTS[i];
                ItemStack slotItem = entity.getItem(slotIndex);
                if (slotItem.isEmpty()) {
                    RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
                    itemRenderer.renderGuiItem(poseStack, outputs[i], leftPos + 134, topPos + 18 + i * 18);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void containerTick() {
        updateButtonLabelAndState();
    }

    private void updateButtonLabelAndState() {
        CuttingBoardBlockEntity entity = menu.getBlockEntity();
        cutButton.active = entity.getRecipe() != null;
        cutButton.setMessage(entity.isProcessing() ? STOP_CUTTING : START_CUTTING);
    }
}
