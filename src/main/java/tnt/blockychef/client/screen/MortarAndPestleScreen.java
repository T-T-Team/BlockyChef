package tnt.blockychef.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.MortarAndPestleBlockEntity;
import tnt.blockychef.common.menu.MortarAndPestleMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.packet.C2S_InitiateRecipeProcessing;

public class MortarAndPestleScreen extends AbstractContainerScreen<MortarAndPestleMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/mortar_and_pestle.png");
    private static final Component TEXT_GRIND = Component.translatable("screen.blockychef.mortar_and_pestle.widget.grind");

    private Button grindButton;

    public MortarAndPestleScreen(MortarAndPestleMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 175;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        grindButton = addRenderableWidget(Button.builder(TEXT_GRIND, this::grindButtonClicked)
                .pos(leftPos + 89, topPos + 64)
                .size(80, 20)
                .build()
        );
    }

    @Override
    protected void containerTick() {
        MortarAndPestleBlockEntity mortarAndPestle = menu.getBlockEntity();
        grindButton.active = mortarAndPestle.hasRecipe() && !mortarAndPestle.isGrinding();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        MortarAndPestleBlockEntity blockEntity = menu.getBlockEntity();
        float grindProgress = blockEntity.getGrindingProgress(partialTicks);
        int arrowWidth = (int) (grindProgress * 26);
        graphics.blit(TEXTURE, leftPos + 75, topPos + 43, 176, 0, arrowWidth, 12);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void grindButtonClicked(Button button) {
        MortarAndPestleBlockEntity blockEntity = menu.getBlockEntity();
        blockEntity.startProcessing();
        NetworkManager.dispatchServerPacket(new C2S_InitiateRecipeProcessing(blockEntity.getBlockPos()));
    }
}
