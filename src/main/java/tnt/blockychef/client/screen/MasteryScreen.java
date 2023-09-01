package tnt.blockychef.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.food.mastery.PlayerMasteryDataProvider;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

import java.util.List;

public class MasteryScreen extends Screen {

    private static final Component TITLE = Component.translatable("screen.blockychef.masteries");
    private static final int COLUMS = 8;
    private static final int ROWS = 9;
    private static final int SPACING = 35;
    private static final int MARGIN_TOP = 50;
    private static final int MASTERY_SIZE = 20;
    // TODO filters

    private int scrollIndex;
    private int rowCount;

    public MasteryScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        PlayerMasteryDataProvider.getMasteryData(minecraft.player).ifPresent(dataProvider -> {
            List<MasteryData> data = BlockyChef.MASTERY_MANAGER.getFullMasteryList().stream().map(mastery -> {
                Item item = mastery.item();
                int cookCount = dataProvider.getCookedCount(item);
                CookingMastery.Tier tier = mastery.getTier(cookCount);
                return new MasteryData(mastery, cookCount, tier);
            }).toList();
            // TODO filters and sorters
            main:
            for (int y = scrollIndex; y < scrollIndex + ROWS; y++) {
                for (int x = 0; x < COLUMS; x++) {
                    int index = x + (y * COLUMS);
                    if (index >= data.size()) {
                        rowCount = y + 1;
                        if (x == 0) {
                            rowCount -= 1;
                        }
                        break main;
                    }
                    MasteryWidget widget = new MasteryWidget(SPACING + x * SPACING, MARGIN_TOP + y * SPACING, MASTERY_SIZE, MASTERY_SIZE, CommonComponents.EMPTY, data.get(index));
                    addRenderableWidget(widget);
                }
            }
        });
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        pGuiGraphics.fill(0, 0, width, height, 0x66 << 24);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        int nextIndex = scrollIndex - (int) pDelta;
        if (nextIndex >= 0 && nextIndex + COLUMS < rowCount) {
            this.scrollIndex = nextIndex;
            init(minecraft, width, height);
            return true;
        }
        return false;
    }

    public static final class MasteryWidget extends AbstractWidget {

        private final MasteryData data;
        private final ItemStack cachedItemStack;

        public MasteryWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, MasteryData data) {
            super(pX, pY, pWidth, pHeight, pMessage);
            this.data = data;
            this.cachedItemStack = new ItemStack(data.mastery().item());
            this.setTooltip(Tooltip.create(cachedItemStack.getDisplayName()));
        }

        @Override
        protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            int border = 2;
            pGuiGraphics.renderItem(cachedItemStack, getX() + border, getY() + border);
            CookingMastery.Tier.Badge badge = data.tier().badge();
            if (badge != CookingMastery.Tier.Badge.NONE) {
                ResourceLocation icon = badge.getIconPath();
                RenderSystem.enableBlend();
                int badgeSize = MASTERY_SIZE + border * 2;
                pGuiGraphics.blit(icon, getX() - border, getY() + border, 0, 0, badgeSize, badgeSize, badgeSize, badgeSize);
                RenderSystem.disableBlend();
            }
            GraphicsHelper.drawAlignedText(pGuiGraphics, Component.literal(data.cookCount() + "x"), Minecraft.getInstance().font, HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM, getX(), getY(), getWidth(), getHeight(), 0xFFFFFF, true, 0, 16);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        }
    }

    private record MasteryData(CookingMastery mastery, int cookCount, CookingMastery.Tier tier) {}
}
