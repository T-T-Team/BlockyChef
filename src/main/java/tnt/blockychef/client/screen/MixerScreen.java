package tnt.blockychef.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.client.screen.widget.ValueCycleButton;
import tnt.blockychef.common.block.entity.MixerBlockEntity;
import tnt.blockychef.common.food.recipe.MixerRecipe;
import tnt.blockychef.common.menu.MixerMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.packet.C2S_MixerEvent;

public class MixerScreen extends AbstractContainerScreen<MixerMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/mixer.png");
    private static final Component MIX_BUTTON = Component.translatable("screen.blockychef.mixer.widget.mix");

    private Button mixButton;

    public MixerScreen(MixerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 175;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        ValueCycleButton<MixerRecipe.RpmValue> btn = addRenderableWidget(new ValueCycleButton<>(leftPos + 7, topPos + 56, 72, 20, MixerRecipe.RpmValue.values(), this::rpmValueChanged));
        btn.setByIndex(menu.getBlockEntity().getCurrentRpmIndex());
        btn.setFormatter(MixerRecipe.RpmValue::getTranslatedText);
        mixButton = addRenderableWidget(new Button.Builder(MIX_BUTTON, this::mixButtonPressed)
                .pos(leftPos + 97, topPos + 56).size(72, 20)
                .build()
        );
    }

    @Override
    protected void containerTick() {
        mixButton.active = menu.getBlockEntity().canBlend();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        renderTooltip(stack, mouseX, mouseY);
    }

    private void rpmValueChanged(MixerRecipe.RpmValue value) {
        MixerBlockEntity blockEntity = menu.getBlockEntity();
        blockEntity.setSelectedRpm(value);
        NetworkManager.dispatchServerPacket(new C2S_MixerEvent(blockEntity.getBlockPos(), false, value.ordinal()));
    }

    private void mixButtonPressed(Button button) {
        MixerBlockEntity blockEntity = menu.getBlockEntity();
        blockEntity.blend(minecraft.player);
        NetworkManager.dispatchServerPacket(new C2S_MixerEvent(blockEntity.getBlockPos(), true, -1));
    }
}
