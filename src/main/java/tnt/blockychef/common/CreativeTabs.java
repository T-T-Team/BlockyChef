package tnt.blockychef.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefItems;

public final class CreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BlockyChef.MODID);

    public static final RegistryObject<CreativeModeTab> ITEM_TAB = TABS.register("item", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(BlockyChefItems.TOMATO))
            .title(Component.translatable("itemGroup.blockychef.items"))
            .displayItems((parameters, output) -> ForgeRegistries.ITEMS.getValues().stream()
                    .filter(item -> isOurs(item) && !isBlock(item))
                    .forEach(output::accept))
            .build()
    );
    public static RegistryObject<CreativeModeTab> BLOCK_TAB = TABS.register("block", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(BlockyChefBlocks.STOVE))
            .title(Component.translatable("itemGroup.blockychef.blocks"))
            .displayItems((parameters, output) -> ForgeRegistries.ITEMS.getValues().stream()
                    .filter(item -> isOurs(item) && isBlock(item))
                    .forEach(output::accept))
            .build()
    );

    private static boolean isOurs(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(BlockyChef.MODID);
    }

    private static boolean isBlock(Item item) {
        return item.getClass().equals(BlockItem.class);
    }
}
