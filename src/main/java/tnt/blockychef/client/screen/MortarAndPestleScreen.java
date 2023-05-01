package tnt.blockychef.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.MortarAndPestleBlockEntity;
import tnt.blockychef.common.menu.MortarAndPestleMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.packet.C2S_MortarAndPestleInitiateGrinding;

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
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        renderTooltip(stack, mouseX, mouseY);
    }

    private void grindButtonClicked(Button button) {
        MortarAndPestleBlockEntity blockEntity = menu.getBlockEntity();
        blockEntity.startGrinding();
        NetworkManager.dispatchServerPacket(new C2S_MortarAndPestleInitiateGrinding(blockEntity.getBlockPos()));
    }
}
