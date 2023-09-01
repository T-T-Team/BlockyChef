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
import tnt.blockychef.common.food.mastery.MasteryGroup;
import tnt.blockychef.common.food.mastery.PlayerMasteryDataProvider;
import tnt.tntlib.api.GraphicsHelper;
import tnt.tntlib.api.HorizontalAlignment;
import tnt.tntlib.api.VerticalAlignment;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class MasteryScreen extends Screen {

    private static final Component TITLE = Component.translatable("screen.blockychef.masteries");
    private static final int COLUMS = 12;
    private static final int ROWS = 7;
    private static final int SPACING = 35;
    private static final int MARGIN_TOP = 50;
    private static final int MASTERY_SIZE = 20;

    private final EnumSet<MasteryGroup> displayedGroups = EnumSet.allOf(MasteryGroup.class);
    private int scrollIndex;
    private int rowCount;

    public MasteryScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        int left = 5;
        int top = 5;
        for (MasteryGroup group : MasteryGroup.values()) {
            GroupFilterWidget widget = new GroupFilterWidget(group, displayedGroups::contains, this::filterChanged);
            int widgetWidth = font.width(widget.getMessage());
            if (left + widgetWidth >= width) {
                top += 15;
                left = 5;
            }
            widget.setX(left);
            widget.setY(top);
            widget.setWidth(widgetWidth);
            widget.setHeight(15);
            left += widgetWidth + 5;
            addRenderableWidget(widget);
        }
        PlayerMasteryDataProvider.getMasteryData(minecraft.player).ifPresent(dataProvider -> {
            List<MasteryData> data = BlockyChef.MASTERY_MANAGER.getFullMasteryList().stream()
                    .map(mastery -> {
                        Item item = mastery.item();
                        int cookCount = dataProvider.getCookedCount(item);
                        CookingMastery.Tier tier = mastery.getTier(cookCount);
                        return new MasteryData(mastery, cookCount, tier);
                    })
                    .filter(t -> displayedGroups.contains(t.mastery().group()))
                    .sorted(Comparator.comparingInt(MasteryData::cookCount).reversed().thenComparing(t -> t.mastery().item().getDescription().getString()))
                    .toList();
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

    private void filterChanged(MasteryGroup group, boolean wasActive) {
        if (wasActive) {
            displayedGroups.remove(group);
        } else {
            displayedGroups.add(group);
        }
        if (displayedGroups.isEmpty()) {
            displayedGroups.add(MasteryGroup.NONE);
        }
        init(minecraft, width, height);
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

        private static final Component MAX_LEVEL = Component.translatable("label.blockychef.tier.max");
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
            Component text;
            if (badge == CookingMastery.Tier.Badge.GOLD) {
                text = MAX_LEVEL;
            } else {
                text = Component.literal(data.cookCount() + "x");
            }
            GraphicsHelper.drawAlignedText(pGuiGraphics, text, Minecraft.getInstance().font, HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM, getX(), getY(), getWidth(), getHeight(), 0xFFFFFF, true, 0, 16);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        }
    }

    private record MasteryData(CookingMastery mastery, int cookCount, CookingMastery.Tier tier) {
    }

    private static final class GroupFilterWidget extends AbstractWidget {

        private final MasteryGroup group;
        private final Predicate<MasteryGroup> active;
        private final BiConsumer<MasteryGroup, Boolean> callback;

        public GroupFilterWidget(MasteryGroup group, Predicate<MasteryGroup> active, BiConsumer<MasteryGroup, Boolean> callback) {
            super(0, 0, 0, 0, Component.translatable("blockychef.mastery.group." + group.name().toLowerCase(Locale.ROOT)));
            this.group = group;
            this.active = active;
            this.callback = callback;
        }

        @Override
        protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            boolean selected = active.test(group);
            if (isHovered) {
                pGuiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x22FFFFFF);
            }
            GraphicsHelper.drawCenteredText(pGuiGraphics, getMessage(), Minecraft.getInstance().font, getX(), getY(), getWidth(), getHeight(), selected ? 0xFFFFFF : 0x666666, true);
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            boolean isActive = active.test(group);
            callback.accept(group, isActive);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

        }
    }
}
