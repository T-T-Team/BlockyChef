package tnt.blockychef.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.MixingBowlBlockEntity;
import tnt.blockychef.common.menu.MixingBowlMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.packet.C2S_InitiateRecipeProcessing;

public class MixingBowlScreen extends AbstractContainerScreen<MixingBowlMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/mixing_bowl.png");
    private static final Component TEXT_MIX = Component.translatable("screen.blockychef.mixing_bowl.widget.mix");

    private Button mixButton;

    public MixingBowlScreen(MixingBowlMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 175;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        mixButton = addRenderableWidget(Button.builder(TEXT_MIX, this::mixButtonClicked)
                .pos(leftPos + 89, topPos + 64)
                .size(80, 20)
                .build()
        );
    }

    @Override
    protected void containerTick() {
        MixingBowlBlockEntity mixingBowl = menu.getBlockEntity();
        mixButton.active = mixingBowl.hasRecipe() && !mixingBowl.isMixing();
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        MixingBowlBlockEntity blockEntity = menu.getBlockEntity();
        float mixProgress = blockEntity.getMixingProgress(partialTicks);
        int arrowWidth = (int) (mixProgress * 26);
        blit(stack, leftPos + 75, topPos + 43, 176, 0, arrowWidth, 12);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        renderTooltip(stack, mouseX, mouseY);
    }

    private void mixButtonClicked(Button button) {
        MixingBowlBlockEntity blockEntity = menu.getBlockEntity();
        blockEntity.startProcessing();
        NetworkManager.dispatchServerPacket(new C2S_InitiateRecipeProcessing(blockEntity.getBlockPos()));
    }
}