package tnt.blockychef.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector2i;
import tnt.blockychef.BlockyChef;
import tnt.tntlib.api.ColorPalette;
import tnt.blockychef.common.block.entity.PanBlockEntity;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.heat.HeatValues;
import tnt.blockychef.common.heat.RegulatedHeatSource;
import tnt.blockychef.common.init.BlockyChefFluids;
import tnt.blockychef.common.menu.PanMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.C2S_RegulateTemperature;
import tnt.blockychef.network.message.C2S_SendApplianceEvent;
import tnt.tntlib.api.FluidRenderHelper;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

import java.util.Locale;

public class PanScreen extends AbstractContainerScreen<PanMenu> {

    public static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/pan.png");
    private final FluidStack renderStack;
    private static final Vector2i[] SLOT_POSITIONS = {
            new Vector2i(80, 8), new Vector2i(107, 34), new Vector2i(97, 65),
            new Vector2i(63, 65), new Vector2i(53, 34)
    };

    public PanScreen(PanMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 188;
        this.renderStack = new FluidStack(BlockyChefFluids.OIL_FLUID.get(), 0);
    }

    @Override
    protected void init() {
        super.init();
        HeatSource heatSource = HeatHelper.getHeatSource(minecraft.level, menu.getBlockEntity().getBlockPos(), Direction.DOWN);
        if (heatSource instanceof RegulatedHeatSource regulatedHeatSource) {
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
        addRenderableWidget(new Button.Builder(Component.translatable("label.blockychef.stir"), this::stir)
                .pos(leftPos + 60, topPos + 86)
                .size(56, 16)
                .build()
        );
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        GraphicsHelper.drawRightAlignedText(pGuiGraphics, Component.literal(String.valueOf(HeatValues.MAX_TEMPERATURE)), font, 163, 17, 0x404040);
        GraphicsHelper.drawRightAlignedText(pGuiGraphics, Component.literal("0"), font, 163, 78, 0x404040);

        HeatSource heatSource = HeatHelper.getHeatSource(minecraft.level, menu.getBlockEntity().getBlockPos(), Direction.DOWN);
        float setTemperature = heatSource.getConfiguredHeat(Direction.UP);
        GraphicsHelper.drawAlignedText(pGuiGraphics, Component.literal(String.format(Locale.ROOT, "%.1f", setTemperature)), font, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, 145, 17, 18, 68, 0x404040);

        pGuiGraphics.blit(TEXTURE, 8, 24, 200, 176, 0, 16, 51, 256, 256);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        PanBlockEntity pan = menu.getBlockEntity();
        PanBlockEntity.PanCookingSlot[] slots = pan.getSlots();
        for (PanBlockEntity.PanCookingSlot slot : slots) {
            Vector2i slotPos = SLOT_POSITIONS[slot.getSlotIndex()];
            int slotX = leftPos + slotPos.x;
            int slotY = topPos + slotPos.y;
            float cookingProgress = slot.getProgress();
            float burnProgress = slot.getBurnProgress();
            graphics.fill(slotX - 4, slotY - 1, slotX - 2, slotY + 17, ColorPalette.PROGRESSION_BAR_BACKGROUND);
            graphics.fill(slotX + 18, slotY - 1, slotX + 20, slotY + 17, ColorPalette.PROGRESSION_BAR_BACKGROUND);
            if (cookingProgress > 0.0F) {
                graphics.fill(slotX - 4, slotY + 17 - (int) (18 * cookingProgress), slotX - 2, slotY + 17, ColorPalette.PROGRESSION_BAR_GOOD);
            }
            if (burnProgress > 0.0F) {
                graphics.fill(slotX + 18, slotY + 17 - (int) (18 * burnProgress), slotX + 20, slotY + 17, ColorPalette.PROGRESSION_BAR_BAD);
            }
        }
        int height = 85;
        int top = 16;
        graphics.fill(leftPos + 164, topPos + top, leftPos + 168, topPos + height, ColorPalette.PROGRESSION_BAR_BACKGROUND);
        graphics.fill(leftPos + 164, topPos + top + (int) ((height - top) * (1.0F - pan.getTemperature() / HeatValues.MAX_TEMPERATURE)), leftPos + 168, topPos + height, ColorPalette.TEMPERATURE_BAR);

        // Oil
        int oil = pan.getOil();
        if (oil > 0) {
            this.renderStack.setAmount(oil);
            FluidRenderHelper.renderFluid(renderStack, graphics, leftPos + 8, topPos + 24, 16, 51, PanBlockEntity.OIL_BUFFER_SIZE);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, mouseX, mouseY, pPartialTick);
        renderTooltip(pGuiGraphics, mouseX, mouseY);
        PanBlockEntity pan = menu.getBlockEntity();
        if (mouseX >= leftPos + 164 && mouseX <= leftPos + 168 && mouseY >= topPos + 16 && mouseY <= topPos + 85) {
            pGuiGraphics.renderTooltip(font, Component.translatable("label.blockychef.temperature", String.format(Locale.ROOT, "%.1f", pan.getTemperature())), mouseX, mouseY);
        }
        int oilValue = pan.getOil();
        if (mouseX >= leftPos + 7 && mouseX <= leftPos + 24 && mouseY >= topPos + 23 && mouseY <= topPos + 75) {
            pGuiGraphics.renderTooltip(font, Component.translatable("label.blockychef.oil", oilValue), mouseX, mouseY);
        }
    }

    private void reduceTemperature(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_RegulateTemperature(menu.getBlockEntity().getBlockPos(), Direction.DOWN, true));
    }

    private void increaseTemperature(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_RegulateTemperature(menu.getBlockEntity().getBlockPos(), Direction.DOWN, false));
    }

    private void stir(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_SendApplianceEvent(menu.getBlockEntity(), PanBlockEntity.STIR_EVENT_ID));
    }
}
