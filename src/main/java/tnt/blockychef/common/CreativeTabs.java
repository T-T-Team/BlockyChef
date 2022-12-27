package tnt.blockychef.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import tnt.blockychef.BlockyChef;

@Mod.EventBusSubscriber(modid = BlockyChef.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CreativeTabs {

    @SubscribeEvent
    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(BlockyChef.resource("main"), builder -> builder
                .icon(() -> new ItemStack(Registry.TOMATO))
                .title(Component.translatable("itemGroup.blockychef.main"))
                .displayItems((featureFlags, output, hasOp) -> ForgeRegistries.ITEMS.getValues().stream()
                        .filter(item -> ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(BlockyChef.MODID))
                        .forEach(output::accept))
        );
    }
}
