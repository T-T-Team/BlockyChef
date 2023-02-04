package tnt.blockychef.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefItems;

@Mod.EventBusSubscriber(modid = BlockyChef.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CreativeTabs {

    @SubscribeEvent
    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(BlockyChef.resource("item"), builder -> builder
                .icon(() -> new ItemStack(BlockyChefItems.TOMATO))
                .title(Component.translatable("itemGroup.blockychef.items"))
                .displayItems((featureFlags, output, hasOp) -> ForgeRegistries.ITEMS.getValues().stream()
                        .filter(item -> isOurs(item) && !isBlock(item))
                        .forEach(output::accept))
        );
        event.registerCreativeModeTab(BlockyChef.resource("block"), builder -> builder
                .icon(() -> new ItemStack(BlockyChefBlocks.STOVE))
                .title(Component.translatable("itemGroup.blockychef.blocks"))
                .displayItems((featureFlags, output, hasOp) -> ForgeRegistries.ITEMS.getValues().stream()
                        .filter(item -> isOurs(item) && isBlock(item))
                        .forEach(output::accept))
        );
    }

    private static boolean isOurs(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(BlockyChef.MODID);
    }

    private static boolean isBlock(Item item) {
        return item.getClass().equals(BlockItem.class);
    }
}
