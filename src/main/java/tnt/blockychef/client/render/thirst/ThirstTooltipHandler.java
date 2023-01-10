package tnt.blockychef.client.render.thirst;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;

public final class ThirstTooltipHandler {

    public static void registerTooltipFactory(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ThirstTooltip.class, ThirstTooltipComponent::new);
    }

    public static void gatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        if (event.isCanceled())
            return;
        ItemStack itemStack = event.getItemStack();
        // TODO get thirst stats from itemstack
        // TODO register
    }

    private static class ThirstTooltipComponent implements ClientTooltipComponent {

        private final ThirstTooltip tooltip;

        public ThirstTooltipComponent(ThirstTooltip tooltip) {
            this.tooltip = tooltip;
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public int getWidth(Font font) {
            return 0;
        }
    }

    private static class ThirstTooltip implements TooltipComponent {
        // TODO implement
    }
}
