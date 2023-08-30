package tnt.blockychef.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.StoveBlockEntity;
import tnt.blockychef.common.menu.StoveMenu;

public class StoveScreen extends AbstractContainerScreen<StoveMenu> {

    public static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/stove.png");

    public StoveScreen(StoveMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 175;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new Button.Builder(Component.literal("-"), this::reduceTemperature)
                .pos(leftPos + 42, topPos + 67)
                .size(20, 20)
                .build()
        );
        addRenderableWidget(new Button.Builder(Component.literal("+"), this::increaseTemperature)
                .pos(leftPos + 114, topPos + 67)
                .size(20, 20)
                .build()
        );
    }

    private void reduceTemperature(Button button) {
        menu.getBlockEntity().getStoveHeatSource().getRegulationHandler().decrease();
    }

    private void increaseTemperature(Button button) {
        menu.getBlockEntity().getStoveHeatSource().getRegulationHandler().increase();
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        pGuiGraphics.drawString(font, String.valueOf(StoveBlockEntity.TEMPERATURE_LIMIT), 150, 13, 0x404040, false);
        pGuiGraphics.drawString(font, "0", 156, 81, 0x404040, false);

        float setTemperature = menu.getBlockEntity().getStoveHeatSource().getRaw();
        pGuiGraphics.drawString(font, String.format("%.1f", setTemperature), 148, 13 + (81 - 13) / 2.0F, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        StoveBlockEntity stove = menu.getBlockEntity();
        StoveBlockEntity.CookingSlot[] slots = stove.getSlots();
        for (StoveBlockEntity.CookingSlot slot : slots) {
            int slotIndex = slot.getSlotIndex();
            int slotX = leftPos + 44 + (slotIndex % 3) * 36;
            int slotY = topPos + 17 + (slotIndex / 3) * 28;

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
        graphics.fill(leftPos + 8, topPos + top, leftPos + 12, topPos + height, 0xFF666666);
        graphics.fill(leftPos + 164, topPos + top, leftPos + 168, topPos + height, 0xFF666666);
        graphics.fill(leftPos + 8, topPos + top + (int) ((height - top) * stove.getEnergyBufferValue()), leftPos + 12, topPos + height, 0xFFE2B100);
        graphics.fill(leftPos + 164, topPos + top + (int) ((height - top) * stove.getHeatAmount()), leftPos + 168, topPos + height, 0xFFFF0000);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, mouseX, mouseY, pPartialTick);
        renderTooltip(pGuiGraphics, mouseX, mouseY);
        StoveBlockEntity stove = menu.getBlockEntity();
        if (mouseX >= leftPos + 8 && mouseX <= leftPos + 12 && mouseY >= topPos + 16 && mouseY <= topPos + 85) {
            pGuiGraphics.renderTooltip(font, Component.translatable("label.blockychef.energy", stove.getStoredEnergyAmount()), mouseX, mouseY);
        }
        if (mouseX >= leftPos + 164 && mouseX <= leftPos + 168 && mouseY >= topPos + 16 && mouseY <= topPos + 85) {
            pGuiGraphics.renderTooltip(font, Component.translatable("label.blockychef.temperature", String.format("%.1f", stove.getActualTemperature())), mouseX, mouseY);
        }
    }
}
