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
    @ObjectHolder(value = "blockychef:bell_pepper_crops", registryName = "block")
    public static final CropsBlock BELL_PEPPER_CROPS = null;
    @ObjectHolder(value = "blockychef:black_pepper_crops", registryName = "block")
    public static final CropsBlock BLACK_PEPPER_CROPS = null;
    @ObjectHolder(value = "blockychef:blueberry_crops", registryName = "block")
    public static final CropsBlock BLUEBERRY_CROPS = null;
    @ObjectHolder(value = "blockychef:broccoli_crops", registryName = "block")
    public static final CropsBlock BROCCOLI_CROPS = null;

    // Items ---
    @ObjectHolder(value = "blockychef:tomato", registryName = "item")
    public static final Item TOMATO = null;
    @ObjectHolder(value = "blockychef:cucumber", registryName = "item")
    public static final Item CUCUMBER = null;
    @ObjectHolder(value = "blockychef:bell_pepper", registryName = "item")
    public static final Item BELL_PEPPER = null;
    @ObjectHolder(value = "blockychef:black_pepper", registryName = "item")
    public static final Item BLACK_PEPPER = null;
    @ObjectHolder(value = "blockychef:blueberry", registryName = "item")
    public static final Item BLUEBERRY = null;
    @ObjectHolder(value = "blockychef:broccoli", registryName = "item")
    public static final Item BROCCOLI = null;

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
        helper.register("bell_pepper_crops", new CropsBlock(() -> BELL_PEPPER), false);
        helper.register("black_pepper_crops", new CropsBlock(() -> BLACK_PEPPER), false);
        helper.register("blueberry_crops", new CropsBlock(() -> BLUEBERRY), false);
        helper.register("broccoli_crops", new CropsBlock(() -> BROCCOLI), false);
    }

    private static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register("tomato", new ItemNameBlockItem(TOMATO_CROPS, new Item.Properties()));
        helper.register("cucumber", new ItemNameBlockItem(CUCUMBER_CROPS, new Item.Properties()));
        helper.register("bell_pepper", new ItemNameBlockItem(BELL_PEPPER_CROPS, new Item.Properties()));
        helper.register("black_pepper", new ItemNameBlockItem(BLACK_PEPPER_CROPS, new Item.Properties()));
        helper.register("blueberry", new ItemNameBlockItem(BLUEBERRY_CROPS, new Item.Properties()));
        helper.register("broccoli", new ItemNameBlockItem(BROCCOLI_CROPS, new Item.Properties()));
    }

    @FunctionalInterface // Registers blocks and schedules itemBlock registration
    private interface BlockRegistryHelper {
        void register(String name, Block block, boolean createItem);

        default void register(String name, Block block) {
            register(name, block, true);
        }
    }
}
