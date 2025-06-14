package tnt.blockychef.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.food.mastery.MasteryGroup;
import tnt.blockychef.common.food.mastery.PlayerMasteryDataProvider;
import tnt.tntlib.api.*;
import tnt.tntlib.api.data.*;
import tnt.tntlib.api.screen.widgets.DataManagerWidget;
import tnt.tntlib.api.screen.widgets.GridWidget;

import java.util.*;

public class MasteryScreen extends Screen {

    private static final Component TITLE = Component.translatable("screen.blockychef.masteries");
    // Filters
    private static final FilterType<MasteryData> FILTER_GROUP = new FilterType<>(BlockyChef.resource("group"), t -> new EnumListFilter<>(t, data -> data.mastery.groups(), MasteryScreen::filterOrAll, EnumSet.noneOf(MasteryGroup.class), MasteryGroup.class, true));
    private static final FilterType<MasteryData> FILTER_MASTERY = new FilterType<>(BlockyChef.resource("mastery"), t -> new TextFilter<>(t, data -> ForgeRegistries.ITEMS.getKey(data.mastery.item()).toString(), String::contains, "", true));
    // Sorters
    private static final SorterType<MasteryData> SORT_COOK_COUNT = new SorterType<>(BlockyChef.resource("cook_count"), () -> Comparator.comparingInt(MasteryData::cookCount), type -> new Sorter.SimpleSorter<>(type, false, true));
    private static final SorterType<MasteryData> SORT_MASTERY_NAME = new SorterType<>(BlockyChef.resource("mastery_name"), () -> Comparator.comparing(t -> t.mastery().item().getDescription().getString()), type -> new Sorter.SimpleSorter<>(type, true, true));
    private static final int PADDING = 15;
    private static final int GRID_SPACING = 40;
    private static final int MASTERY_SIZE = 20;
    private static final View<MasteryData> DEFAULT_VIEW = new View.SimpleView<>("System", new LinkedHashSet<>(), TNTUtils.createInit(new LinkedHashSet<>(), sorters -> {
        sorters.add(SORT_COOK_COUNT.createDefault());
        sorters.add(SORT_MASTERY_NAME.createDefault());
    }));

    private static View<MasteryData> lastView;
    private GridWidget grid;
    private int scrollIndex;
    private int masteryCount;

    public MasteryScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        PlayerMasteryDataProvider.getMasteryData(minecraft.player).ifPresent(dataProvider -> {
            List<MasteryData> data = BlockyChef.MASTERY_MANAGER.getFullMasteryList().stream()
                    .map(mastery -> {
                        Item item = mastery.item();
                        int cookCount = dataProvider.getCookedCount(item);
                        CookingMastery.Tier tier = mastery.getTier(cookCount);
                        return new MasteryData(mastery, cookCount, tier);
                    })
                    .toList();
            masteryCount = data.size();
            if (lastView == null) {
                lastView = DEFAULT_VIEW;
            }
            DataManagerWidget.DataManagerProperties<MasteryData> properties = new DataManagerWidget.DataManagerProperties<>(lastView, (renderData, dataview, canv, widgets) -> {
                grid = new GridWidget(canv.getX(), canv.getY(), canv.getWidth(), canv.getHeight());
                grid.setMargin(GRID_SPACING);
                main:
                for (int y = scrollIndex; y < scrollIndex + grid.getRows(); y++) {
                    for (int x = 0; x < grid.getColumns(); x++) {
                        int index = x + (y * grid.getColumns());
                        if (index >= renderData.size()) {
                            break main;
                        }
                        MasteryWidget widget = new MasteryWidget(0, 0, MASTERY_SIZE, MASTERY_SIZE, CommonComponents.EMPTY, renderData.get(index));
                        grid.addRenderableWidget(widget);
                    }
                }
                widgets.addWidget(grid);
                lastView = dataview;
            }, List.of(FILTER_GROUP, FILTER_MASTERY), Arrays.asList(SORT_COOK_COUNT, SORT_MASTERY_NAME));
            addRenderableWidget(new DataManagerWidget<>(PADDING, PADDING, width - 2 * PADDING, height - PADDING, properties, data, this));
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
        return UiHelper.handleMouseScrolled(pDelta, scrollIndex, grid.getRows(), grid.getTotalRowCountFor(masteryCount), value -> {
            scrollIndex = value;
            init(minecraft, width, height);
        });
    }

    public static final class MasteryWidget extends AbstractWidget {

        private static final Component MAX_LEVEL = Component.translatable("label.blockychef.tier.max").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA);
        private final MasteryData data;
        private final ItemStack cachedItemStack;

        public MasteryWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, MasteryData data) {
            super(pX, pY, pWidth, pHeight, pMessage);
            this.data = data;
            this.cachedItemStack = new ItemStack(data.mastery().item());
            this.setTooltip(Tooltip.create(cachedItemStack.getHoverName()));
        }

        @Override
        protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            int border = 2;
            pGuiGraphics.renderItem(cachedItemStack, getX() + border, getY() + border);
            CookingMastery.Tier.Badge badge = data.tier().badge();
            RenderSystem.enableBlend();
            int badgeSize = MASTERY_SIZE + border * 2;
            pGuiGraphics.blit(badge.getIconPath(), getX() - border, getY() + border, 0, 0, badgeSize, badgeSize, badgeSize, badgeSize);
            RenderSystem.disableBlend();
            Component text;
            if (badge == CookingMastery.Tier.Badge.DIAMOND) {
                text = MAX_LEVEL;
            } else {
                text = Component.literal(data.cookCount() + "x");
            }
            GraphicsHelper.drawAlignedText(pGuiGraphics, text, Minecraft.getInstance().font, HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM, getX(), getY(), getWidth(), getHeight(), 0xFFFFFF, true, 0, 16);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        }

        @Override
        protected boolean isValidClickButton(int pButton) {
            return false;
        }
    }

    private record MasteryData(CookingMastery mastery, int cookCount, CookingMastery.Tier tier) {
    }

    private static boolean filterOrAll(EnumSet<MasteryGroup> filter, Collection<MasteryGroup> groups) {
        if (filter.isEmpty())
            return true;
        return groups.containsAll(filter);
    }
}
