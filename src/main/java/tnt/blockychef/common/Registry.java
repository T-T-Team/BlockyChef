package tnt.blockychef.common;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.CropsBlock;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BlockyChef.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Registry {

    // Blocks ---
    @ObjectHolder(value = "blockychef:tomato_crops", registryName = "block")
    public static final CropsBlock TOMATO_CROPS = null;
    @ObjectHolder(value = "blockychef:cucumber_crops", registryName = "block")
    public static final CropsBlock CUCUMBER_CROPS = null;

    // Items ---
    @ObjectHolder(value = "blockychef:tomato", registryName = "item")
    public static final Item TOMATO = null;
    @ObjectHolder(value = "blockychef:cucumber", registryName = "item")
    public static final Item CUCUMBER = null;

    private static final Item.Properties DEFAULT_ITEM_PROPERTIES = new Item.Properties().tab(CreativeTabs.BLOCKY_CHEF);
    private static List<Block> blockEntries = new ArrayList<>();

    @SubscribeEvent
    public static void registerObjects(RegisterEvent event) {
        event.register(ForgeRegistries.BLOCKS.getRegistryKey(), helper -> {
            BlockRegistryHelper blockHelper = (name, block, createItem) -> {
                helper.register(name, block);
                if (createItem) {
                    blockEntries.add(block);
                }
            };
            registerBlocks(blockHelper);
        });
        event.register(ForgeRegistries.ITEMS.getRegistryKey(), helper -> {
            registerItems(helper);
            for (Block block : blockEntries) {
                BlockItem blockItem = new BlockItem(block, new Item.Properties());
                helper.register(ForgeRegistries.BLOCKS.getKey(block), blockItem);
            }
            blockEntries = null;
        });
    }

    private static void registerBlocks(BlockRegistryHelper helper) {
        helper.register("tomato_crops", new CropsBlock(() -> TOMATO), false);
        helper.register("cucumber_crops", new CropsBlock(() -> CUCUMBER), false);
    }

    private static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register("tomato", new ItemNameBlockItem(TOMATO_CROPS, DEFAULT_ITEM_PROPERTIES));
        helper.register("cucumber", new ItemNameBlockItem(CUCUMBER_CROPS, DEFAULT_ITEM_PROPERTIES));
    }

    @FunctionalInterface // Registers blocks and schedules itemBlock registration
    private interface BlockRegistryHelper {
        void register(String name, Block block, boolean createItem);

        default void register(String name, Block block) {
            register(name, block, true);
        }
    }
}
