package tnt.blockychef.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
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
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.packet.C2S_RecipeSelectorEvent;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.Localizations;
import tnt.blockychef.util.RenderHelper;

public class CuttingBoardScreen extends AbstractContainerScreen<CuttingBoardMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/cutting_board.png");

    private Button cutButton;
    private Button prevRecipe, nextRecipe;

    public CuttingBoardScreen(CuttingBoardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 195;
        this.inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        cutButton = addRenderableWidget(
                new Button.Builder(Localizations.CANCEL, this::processButtonClicked)
                        .pos(leftPos + 61, topPos + 78)
                        .size(54, 20)
                        .build()
        );

        CuttingBoardBlockEntity blockEntity = menu.getBlockEntity();
        int index = blockEntity.getRecipeIndex();
        int maxIndex = blockEntity.getAvailableRecipeCount() - 1;
        prevRecipe = addRenderableWidget(new Button.Builder(Component.literal("<"), this::prevRecipeClicked)
                .size(20, 20)
                .pos(leftPos + 40, topPos + 78)
                .build()
        );
        prevRecipe.active = index > 0;
        nextRecipe = addRenderableWidget(new Button.Builder(Component.literal(">"), this::nextRecipeClicked)
                .size(20, 20)
                .pos(leftPos + 116, topPos + 78)
                .build()
        );
        nextRecipe.active = index >= 0 && index < maxIndex;

        updateButtonLabelAndState();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        CuttingBoardBlockEntity entity = menu.getBlockEntity();
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        float progress = entity.getProcessingProgress(partialTicks);
        int arrowWidth = (int) (progress * 26);
        graphics.blit(TEXTURE, leftPos + 75, topPos + 38, 176, 0, arrowWidth, 12);

        float f = RenderHelper.ease(Helper.pulse(minecraft.level.getGameTime(), 50L), RenderHelper.Easing.SINE_IO);
        float minColor = 0.4F;
        float maxColor = 0.9F;
        float color = minColor + f * (maxColor - minColor);
        CuttingBoardRecipe recipe = entity.getRecipe();
        if (recipe != null) {
            ItemStack[] outputs = recipe.getOutputs();
            for (int i = 0; i < outputs.length; i++) {
                int slotIndex = CuttingBoardBlockEntity.SLOT_OUTPUTS[i];
                ItemStack slotItem = entity.getItem(slotIndex);
                if (slotItem.isEmpty()) {
                    RenderSystem.setShaderColor(color, color, color, 1.0F);
                    graphics.renderItem(outputs[i], leftPos + 134, topPos + 18 + i * 18);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void containerTick() {
        updateButtonLabelAndState();
    }

    private void processButtonClicked(Button button) {
        CuttingBoardBlockEntity entity = menu.getBlockEntity();
        boolean active = !entity.isProcessing();
        NetworkManager.dispatchServerPacket(new C2S_RecipeSelectorEvent(entity.getBlockPos(), C2S_RecipeSelectorEvent.EventType.PROCESSING, active));
    }

    private void prevRecipeClicked(Button button) {
        CuttingBoardBlockEntity entity = menu.getBlockEntity();
        entity.changeRecipe(-1);
        init(minecraft, width, height);
        NetworkManager.dispatchServerPacket(new C2S_RecipeSelectorEvent(entity.getBlockPos(), C2S_RecipeSelectorEvent.EventType.RECIPE, false));
    }

    private void nextRecipeClicked(Button button) {
        CuttingBoardBlockEntity entity = menu.getBlockEntity();
        entity.changeRecipe(1);
        init(minecraft, width, height);
        NetworkManager.dispatchServerPacket(new C2S_RecipeSelectorEvent(entity.getBlockPos(), C2S_RecipeSelectorEvent.EventType.RECIPE, true));
    }

    private void updateButtonLabelAndState() {
        CuttingBoardBlockEntity entity = menu.getBlockEntity();
        Component label = Localizations.CANCEL;
        cutButton.active = entity.getRecipe() != null;
        int index = entity.getRecipeIndex();
        int max = entity.getAvailableRecipeCount() - 1;
        if (cutButton.active) {
            label = entity.getRecipe().getProcessingType().getTranslatedComponent();
        }
        cutButton.setMessage(entity.isProcessing() ? Localizations.CANCEL : label);
        prevRecipe.active = index > 0;
        nextRecipe.active = index >= 0 && index < max;
    }
}
