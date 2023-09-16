package tnt.blockychef.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.PanBlockEntity;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.heat.HeatValues;
import tnt.blockychef.common.heat.RegulatedHeatSource;
import tnt.blockychef.common.menu.PanMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.C2S_RegulateTemperature;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

import java.util.Locale;

public class PanScreen extends AbstractContainerScreen<PanMenu> {

    public static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/pan.png");
    private static final Vector2i[] SLOT_POSITIONS = {
            new Vector2i(80, 8), new Vector2i(103, 37), new Vector2i(95, 67),
            new Vector2i(65, 67), new Vector2i(57, 37)
    };

    public PanScreen(PanMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 188;
    }

    @Override
    protected void init() {
        super.init();
        HeatSource heatSource = HeatHelper.getHeatSource(minecraft.level, menu.getBlockEntity().getBlockPos(), Direction.DOWN);
        if (heatSource instanceof RegulatedHeatSource regulatedHeatSource) {
            // TODO heat controls
        }
    }
    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        GraphicsHelper.drawRightAlignedText(pGuiGraphics, Component.literal(String.valueOf(HeatValues.MAX_TEMPERATURE)), font, 163, 17, 0x404040);
        GraphicsHelper.drawRightAlignedText(pGuiGraphics, Component.literal("0"), font, 163, 78, 0x404040);

        HeatSource heatSource = HeatHelper.getHeatSource(minecraft.level, menu.getBlockEntity().getBlockPos(), Direction.DOWN);
        float setTemperature = heatSource.getHeat(Direction.UP);
        GraphicsHelper.drawAlignedText(pGuiGraphics, Component.literal(String.format(Locale.ROOT, "%.1f", setTemperature)), font, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, 145, 17, 18, 68, 0x404040);
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
            graphics.fill(slotX - 4, slotY - 1, slotX - 2, slotY + 17, 0xFF666666);
            graphics.fill(slotX + 18, slotY - 1, slotX + 20, slotY + 17, 0xFF666666);
            if (cookingProgress > 0.0F) {
                graphics.fill(slotX - 4, slotY + 17 - (int) (18 * cookingProgress), slotX - 2, slotY + 17, 0xFF00DD00);
            }
            if (burnProgress > 0.0F) {
                graphics.fill(slotX + 18, slotY + 17 - (int) (18 * burnProgress), slotX + 20, slotY + 17, 0xFFFF0000);
            }
        }
        int height = 85;
        int top = 16;
        //graphics.fill(leftPos + 8, topPos + top, leftPos + 12, topPos + height, 0xFF666666);
        graphics.fill(leftPos + 164, topPos + top, leftPos + 168, topPos + height, 0xFF666666);
        //graphics.fill(leftPos + 8, topPos + top + (int) ((height - top) * pan.getEnergyBufferValue()), leftPos + 12, topPos + height, 0xFFE2B100);
        graphics.fill(leftPos + 164, topPos + top + (int) ((height - top) * (1.0F - pan.getTemperature() / HeatValues.MAX_TEMPERATURE)), leftPos + 168, topPos + height, 0xFFFF0000);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, mouseX, mouseY, pPartialTick);
        renderTooltip(pGuiGraphics, mouseX, mouseY);
        PanBlockEntity pan = menu.getBlockEntity();
        /*if (mouseX >= leftPos + 8 && mouseX <= leftPos + 12 && mouseY >= topPos + 16 && mouseY <= topPos + 85) {
            pGuiGraphics.renderTooltip(font, Component.translatable("label.blockychef.energy", pan.getStoredEnergyAmount()), mouseX, mouseY);
        }*/
        if (mouseX >= leftPos + 164 && mouseX <= leftPos + 168 && mouseY >= topPos + 16 && mouseY <= topPos + 85) {
            pGuiGraphics.renderTooltip(font, Component.translatable("label.blockychef.temperature", String.format(Locale.ROOT, "%.1f", pan.getTemperature())), mouseX, mouseY);
        }
    }

    private void reduceTemperature(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_RegulateTemperature(menu.getBlockEntity().getBlockPos(), Direction.DOWN, true));
    }

    private void increaseTemperature(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_RegulateTemperature(menu.getBlockEntity().getBlockPos(), Direction.DOWN, false));
    }
}
