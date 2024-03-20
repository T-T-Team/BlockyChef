package tnt.blockychef.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import tnt.blockychef.BlockyChef;
import tnt.tntlib.api.ColorPalette;
import tnt.blockychef.common.block.entity.GrillBlockEntity;
import tnt.blockychef.common.heat.HeatValues;
import tnt.blockychef.common.menu.GrillMenu;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.C2S_RegulateTemperature;
import tnt.blockychef.network.message.C2S_SendApplianceEvent;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

import java.util.Locale;

public class GrillScreen extends AbstractContainerScreen<GrillMenu> {

    private static final ResourceLocation TEXTURE = BlockyChef.resource("textures/screen/grill.png");

    private static final int GRILL_SLOT_X = 44;
    private static final int GRILL_SLOT_Y = 17;
    private static final int GRILL_SLOT_X_OFFSET = 36;
    private static final int GRILL_SLOT_Y_OFFSET = 37;
    private static final Component FLIP = Component.translatable("label.blockychef.flip");

    private Button[] flipButtons;

    public GrillScreen(GrillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 208;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        flipButtons = new Button[6];
        for (int i = 0; i < 6; i++) {
            final int index = i; // (:
            int x = i % 3;
            int y = i / 3;
            flipButtons[i] = addRenderableWidget(new Button.Builder(FLIP, btn -> flip(btn, index))
                    .pos(leftPos + GRILL_SLOT_X + x * GRILL_SLOT_X_OFFSET - 4, topPos + GRILL_SLOT_Y + y * GRILL_SLOT_Y_OFFSET + 20)
                    .size(24, 12)
                    .build()
            );
            flipButtons[i].active = false;
        }

        addRenderableWidget(new Button.Builder(Component.literal("-"), this::decreaseTemperature)
                .pos(leftPos + 144, topPos + 109)
                .size(12, 12)
                .build()
        );
        addRenderableWidget(new Button.Builder(Component.literal("+"), this::increaseTemperature)
                .pos(leftPos + 157, topPos + 109)
                .size(12, 12)
                .build()
        );
    }

    @Override
    protected void containerTick() {
        GrillBlockEntity grill = menu.getBlockEntity();
        for (int i = 0; i < flipButtons.length; i++) {
            flipButtons[i].active = grill.canFlip(i);
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        GrillBlockEntity grill = menu.getBlockEntity();
        for (int i = 0; i < 6; i++) {
            int x = i % 3;
            int y = i / 3;

            int slotX = leftPos + GRILL_SLOT_X + x * GRILL_SLOT_X_OFFSET;
            int slotY = topPos + GRILL_SLOT_Y + y * GRILL_SLOT_Y_OFFSET;
            float progress = grill.getProgression(i, false);
            float progressFlipped = grill.getProgression(i, true);
            renderProgressionBar(pGuiGraphics, slotX + 19, slotY - 1, progress, ColorPalette.PROGRESSION_BAR_GOOD);
            renderProgressionBar(pGuiGraphics, slotX + 22, slotY - 1, progressFlipped, ColorPalette.PROGRESSION_BAR_GOOD);

            float burn = grill.getBurnProgression(i, false);
            float burnFlipped = grill.getBurnProgression(i, true);
            renderProgressionBar(pGuiGraphics, slotX - 5, slotY - 1, burn, ColorPalette.PROGRESSION_BAR_BAD);
            renderProgressionBar(pGuiGraphics, slotX - 8, slotY - 1, burnFlipped, ColorPalette.PROGRESSION_BAR_BAD);
        }

        // fuel
        int fuelAmount = grill.getFuel();
        if (fuelAmount > 0) {
            int size = 14;
            float fuelPct = fuelAmount / 1600.0F;
            int progress = Mth.ceil(fuelPct * (size - 1.0F)) + 1;
            pGuiGraphics.blitSprite(TeapotScreen.LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - progress, leftPos + 62, topPos + 107 - progress, 14, progress);
        }

        // temperature
        float temperatureProgress = grill.getTemperature() / HeatValues.MAX_TEMPERATURE;
        pGuiGraphics.fill(leftPos + 165, topPos + 7, leftPos + 168, topPos + 107, ColorPalette.PROGRESSION_BAR_BACKGROUND);
        pGuiGraphics.fill(leftPos + 165, topPos + 107 - (int) (100 * temperatureProgress), leftPos + 168, topPos + 107, ColorPalette.TEMPERATURE_BAR);
    }

    protected void renderProgressionBar(GuiGraphics graphics, int x, int y, float amount, int color) {
        int height = 18;
        int width = 2;

        graphics.fill(x, y, x + width, y + height, ColorPalette.PROGRESSION_BAR_BACKGROUND);
        int progress = Math.min((int) (height * amount), height);
        graphics.fill(x, y + height - progress, x + width, y + height, color);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(pGuiGraphics, pMouseX, pMouseY);

        GraphicsHelper.drawRightAlignedText(pGuiGraphics, Component.literal(String.valueOf(HeatValues.MAX_TEMPERATURE)), font, 163, 8, ColorPalette.GUI_TEXT_GRAY);
        GraphicsHelper.drawRightAlignedText(pGuiGraphics, Component.literal("0"), font, 163, 98, ColorPalette.GUI_TEXT_GRAY);

        float setTemperature = menu.getBlockEntity().getHeatSource().getConfiguredHeat(null);
        GraphicsHelper.drawAlignedText(pGuiGraphics, Component.literal(String.format(Locale.ROOT, "%.1f", setTemperature)), font, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, 145, 8, 18, 90, ColorPalette.GUI_TEXT_GRAY);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    private void flip(Button button, int index) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_SendApplianceEvent(menu.getBlockEntity(), index));
    }

    private void increaseTemperature(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_RegulateTemperature(menu.getBlockEntity().getBlockPos(), null, false));
    }

    private void decreaseTemperature(Button button) {
        NetworkManager.DISPATCHER.sendToServer(new C2S_RegulateTemperature(menu.getBlockEntity().getBlockPos(), null, true));
    }
}
