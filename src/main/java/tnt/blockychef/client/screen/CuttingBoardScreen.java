package tnt.blockychef.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.client.CoordinatesUV;
import tnt.blockychef.common.menu.CuttingBoardMenu;

public class CuttingBoardScreen extends AbstractContainerScreen<CuttingBoardMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/cutting_board.png");
    private static final CoordinatesUV ARROW_UV = new CoordinatesUV(176, 0, 201, 11);

    public CuttingBoardScreen(CuttingBoardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 174;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        renderTooltip(poseStack, mouseX, mouseY);
    }
}
