package tnt.blockychef.common;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class CreativeTabs {

    public static final CreativeModeTab BLOCKY_CHEF = new CreativeModeTab("blockychef.main") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registry.TOMATO);
        }
    };
}
