package tnt.blockychef.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.PanBlockEntity;
import tnt.blockychef.common.block.entity.TeapotBlockEntity;
import tnt.blockychef.common.food.fluid.FluidContainer;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.heat.HeatValues;
import tnt.blockychef.common.heat.RegulatedHeatSource;
import tnt.blockychef.common.menu.TeapotMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.C2S_RegulateTemperature;
import tnt.tntlib.api.FluidRenderHelper;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

import java.util.List;
import java.util.Locale;

public class TeapotScreen extends AbstractContainerScreen<TeapotMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/teapot.png");
    private static final ResourceLocation LIT_PROGRESS_SPRITE = new ResourceLocation("container/furnace/lit_progress");
    private static final ResourceLocation BUBBLES_SPRITE = new ResourceLocation("container/brewing_stand/bubbles");

    public TeapotScreen(TeapotMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 188;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        HeatSource heatSource = HeatHelper.getHeatSource(minecraft.level, menu.getBlockEntity().getBlockPos(), Direction.DOWN);
        if (heatSource instanceof RegulatedHeatSource) {
            addRenderableWidget(new Button.Builder(Component.literal("-"), this::reduceTemperature)
                    .pos(leftPos + 143, topPos + 89)
                    .size(12, 12)
                    .build()
            );
            addRenderableWidget(new Button.Builder(Component.literal("+"), this::increaseTemperature)
                    .pos(leftPos + 156, topPos + 89)
                    .size(12, 12)
                    .build()
            );
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        TeapotBlockEntity teapot = menu.getBlockEntity();
        float waterBoilProgress = teapot.getWaterBoilProgress();
        float cookingProgress = teapot.getCookProgress();
        if (waterBoilProgress > 0) {
            int sizeY = 14;
            int progress = Mth.ceil(waterBoilProgress * (sizeY - 1.0F)) + 1;
            pGuiGraphics.blitSprite(LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - progress, leftPos + 113, topPos + 88 - progress, 14, progress);
        }
        if (cookingProgress > 0) {
            int sizeY = 29;
            int progress = Mth.ceil(cookingProgress * (sizeY - 1.0F)) + 1;
            pGuiGraphics.blitSprite(BUBBLES_SPRITE, 12, 29, 0, 29 - progress, leftPos + 61, topPos + 34 + 29 - progress, 12, progress);
        }

        int height = 85;
        int top = 16;
        pGuiGraphics.fill(leftPos + 164, topPos + top, leftPos + 168, topPos + height, 0xFF666666);
        pGuiGraphics.fill(leftPos + 164, topPos + top + (int) ((height - top) * (1.0F - menu.getBlockEntity().getTemperature() / HeatValues.MAX_TEMPERATURE)), leftPos + 168, topPos + height, 0xFFFF0000);

        FluidContainer container = teapot.getFluidContainer();
        List<FluidStack> fluids = container.getFluids();
        if (!fluids.isEmpty()) {
            FluidStack fluidStack = fluids.get(0);
            FluidRenderHelper.renderFluid(fluidStack, pGuiGraphics, leftPos + 112, topPos + 8, 16, 63, TeapotBlockEntity.CAPACITY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(pGuiGraphics, pMouseX, pMouseY);

        GraphicsHelper.drawRightAlignedText(pGuiGraphics, Component.literal(String.valueOf(HeatValues.MAX_TEMPERATURE)), font, 163, 17, 0x404040);
        GraphicsHelper.drawRightAlignedText(pGuiGraphics, Component.literal("0"), font, 163, 78, 0x404040);

        HeatSource heatSource = HeatHelper.getHeatSource(minecraft.level, menu.getBlockEntity().getBlockPos(), Direction.DOWN);
        float setTemperature = heatSource.getConfiguredHeat(Direction.UP);
        GraphicsHelper.drawAlignedText(pGuiGraphics, Component.literal(String.format(Locale.ROOT, "%.1f", setTemperature)), font, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, 145, 17, 18, 68, 0x404040);

        pGuiGraphics.blit(TEXTURE, 112, 8, 200, 176, 0, 16, 63, 256, 256);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        TeapotBlockEntity teapot = menu.getBlockEntity();
        if (pMouseX >= leftPos + 164 && pMouseX <= leftPos + 168 && pMouseY >= topPos + 16 && pMouseY <= topPos + 85) {
            pGuiGraphics.renderTooltip(font, Component.translatable("label.blockychef.temperature", String.format(Locale.ROOT, "%.1f", teapot.getTemperature())), pMouseX, pMouseY);
        }
    }

    private void reduceTemperature(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_RegulateTemperature(menu.getBlockEntity().getBlockPos(), Direction.DOWN, true));
    }

    private void increaseTemperature(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_RegulateTemperature(menu.getBlockEntity().getBlockPos(), Direction.DOWN, false));
    }
}
