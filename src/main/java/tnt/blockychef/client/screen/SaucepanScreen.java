package tnt.blockychef.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.SaucepanBlockEntity;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.heat.HeatValues;
import tnt.blockychef.common.heat.RegulatedHeatSource;
import tnt.blockychef.common.menu.SaucepanMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.C2S_InitiateRecipeProcessing;
import tnt.blockychef.network.message.C2S_RegulateTemperature;
import tnt.blockychef.network.message.C2S_SendApplianceEvent;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

import java.util.Locale;

public class SaucepanScreen extends AbstractContainerScreen<SaucepanMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/saucepan.png");

    private Button cookButton;
    private Button stirButton;

    public SaucepanScreen(SaucepanMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 219;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        stirButton = addRenderableWidget(new Button.Builder(Component.translatable("label.blockychef.stir"), this::stir)
                .pos(leftPos + 8, topPos + 108)
                .size(40, 15)
                .build()
        );
        cookButton = addRenderableWidget(new Button.Builder(Component.translatable("label.blockychef.cook"), this::cook)
                .pos(leftPos + 8, topPos + 90)
                .size(40, 15)
                .build()
        );
        stirButton.active = false;
        cookButton.active = false;

        HeatSource heatSource = HeatHelper.getHeatSource(minecraft.level, menu.getBlockEntity().getBlockPos(), Direction.DOWN);
        if (heatSource instanceof RegulatedHeatSource) {
            addRenderableWidget(new Button.Builder(Component.literal("-"), this::reduceTemperature)
                    .pos(leftPos + 142, topPos + 120)
                    .size(12, 12)
                    .build()
            );
            addRenderableWidget(new Button.Builder(Component.literal("+"), this::addTemperature)
                    .pos(leftPos + 156, topPos + 120)
                    .size(12, 12)
                    .build()
            );
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(pGuiGraphics, pMouseX, pMouseY);
        GraphicsHelper.drawRightAlignedText(pGuiGraphics, Component.literal(String.valueOf(HeatValues.MAX_TEMPERATURE)), font, 163, 17, 0x404040);
        GraphicsHelper.drawRightAlignedText(pGuiGraphics, Component.literal("0"), font, 163, 107, 0x404040);

        HeatSource heatSource = HeatHelper.getHeatSource(minecraft.level, menu.getBlockEntity().getBlockPos(), Direction.DOWN);
        float setTemperature = heatSource.getConfiguredHeat(Direction.UP);
        GraphicsHelper.drawAlignedText(pGuiGraphics, Component.literal(String.format(Locale.ROOT, "%.1f", setTemperature)), font, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, 145, 17, 18, 102, 0x404040);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        SaucepanBlockEntity saucepan = menu.getBlockEntity();
        float burnAmount = saucepan.getBurnAmount();
        float progress = saucepan.getProgress();

        pGuiGraphics.fill(leftPos + 97, topPos + 61, leftPos + 99, topPos + 83, 0xFF8B8B8B);
        if (burnAmount > 0) {
            pGuiGraphics.fill(leftPos + 97, topPos + 83, leftPos + 99, topPos + 83 - (int)(22 * burnAmount), 0xFFFF0000);
        }

        if (progress > 0) {
            pGuiGraphics.blit(TEXTURE, leftPos + 79, topPos + 61, 176, 0, 16, (int) (progress * 22));
        }

        int height = 115;
        int top = 16;
        pGuiGraphics.fill(leftPos + 164, topPos + top, leftPos + 168, topPos + height, 0xFF666666);
        pGuiGraphics.fill(leftPos + 164, topPos + top + (int) ((height - top) * (1.0F - saucepan.getTemperature() / HeatValues.MAX_TEMPERATURE)), leftPos + 168, topPos + height, 0xFFFF0000);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        SaucepanBlockEntity pan = menu.getBlockEntity();
        if (pMouseX >= leftPos + 164 && pMouseX <= leftPos + 168 && pMouseY >= topPos + 16 && pMouseY <= topPos + 115) {
            pGuiGraphics.renderTooltip(font, Component.translatable("label.blockychef.temperature", String.format(Locale.ROOT, "%.1f", pan.getTemperature())), pMouseX, pMouseY);
        }
    }

    @Override
    protected void containerTick() {
        SaucepanBlockEntity saucepan = menu.getBlockEntity();
        cookButton.active = saucepan.canCook();
        stirButton.active = saucepan.canStir();
    }

    private void cook(Button button) {
        SaucepanBlockEntity saucepan = menu.getBlockEntity();
        saucepan.startProcessing();
        NetworkManager.DISPATCHER.sendToServer(new C2S_InitiateRecipeProcessing(saucepan.getBlockPos()));
    }

    private void stir(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_SendApplianceEvent(menu.getBlockEntity(), SaucepanBlockEntity.STIR_EVENT_ID));
    }

    private void addTemperature(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_RegulateTemperature(menu.getBlockEntity().getBlockPos(), Direction.DOWN, false));
    }

    private void reduceTemperature(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_RegulateTemperature(menu.getBlockEntity().getBlockPos(), Direction.DOWN, true));
    }
}
