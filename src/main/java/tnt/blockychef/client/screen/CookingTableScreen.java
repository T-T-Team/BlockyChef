package tnt.blockychef.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.CookingTableBlockEntity;
import tnt.blockychef.common.menu.CookingTableMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.C2S_InitiateRecipeProcessing;

public class CookingTableScreen extends AbstractContainerScreen<CookingTableMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/cooking_table.png");
    private static final Component TEXT_CRAFT = Component.translatable("screen.blockychef.cooking_table.widget.craft");

    private Button craftBtn;

    public CookingTableScreen(CookingTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.imageHeight = 190;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        craftBtn = addRenderableWidget(Button.builder(TEXT_CRAFT, this::initiateCrafting)
                .pos(leftPos + 89, topPos + 85)
                .size(80, 20)
                .build());
        craftBtn.active = false;
    }

    @Override
    protected void containerTick() {
        CookingTableBlockEntity table = menu.getBlockEntity();
        craftBtn.active = table.canCraft();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        CookingTableBlockEntity blockEntity = menu.getBlockEntity();
        float progress = blockEntity.getAssemblyProgress(partialTicks);
        int arrowWidth = (int) (progress * 22);
        graphics.blit(TEXTURE, leftPos + 84, topPos + 47, 176, 0, arrowWidth, 16);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void initiateCrafting(Button button) {
        CookingTableBlockEntity table = menu.getBlockEntity();
        table.startProcessing();
        NetworkManager.DISPATCHER.sendToServer(new C2S_InitiateRecipeProcessing(table.getBlockPos()));
    }
}
