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
import tnt.blockychef.common.block.WeedsBlock;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BlockyChef.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Registry {

    // Blocks ---
    @ObjectHolder(value = "blockychef:weeds", registryName = "block")
    public static final WeedsBlock WEEDS = null;
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
    @ObjectHolder(value = "blockychef:cabbage_crops", registryName = "block")
    public static final CropsBlock CABBAGE_CROPS = null;
    @ObjectHolder(value = "blockychef:chilli_pepper_crops", registryName = "block")
    public static final CropsBlock CHILLI_PEPPER_CROPS = null;
    @ObjectHolder(value = "blockychef:corn_crops", registryName = "block")
    public static final CropsBlock CORN_CROPS = null;
    @ObjectHolder(value = "blockychef:garlic_crops", registryName = "block")
    public static final CropsBlock GARLIC_CROPS = null;
    @ObjectHolder(value = "blockychef:leek_crops", registryName = "block")
    public static final CropsBlock LEEK_CROPS = null;
    @ObjectHolder(value = "blockychef:lettuce_crops", registryName = "block")
    public static final CropsBlock LETTUCE_CROPS = null;
    @ObjectHolder(value = "blockychef:onion_crops", registryName = "block")
    public static final CropsBlock ONION_CROPS = null;
    @ObjectHolder(value = "blockychef:parsley_crops", registryName = "block")
    public static final CropsBlock PARSLEY_CROPS = null;
    @ObjectHolder(value = "blockychef:raspberry_crops", registryName = "block")
    public static final CropsBlock RASPBERRY_CROPS = null;
    @ObjectHolder(value = "blockychef:spring_onion_crops", registryName = "block")
    public static final CropsBlock SPRING_ONION_CROPS = null;
    @ObjectHolder(value = "blockychef:strawberry_crops", registryName = "block")
    public static final CropsBlock STRAWBERRY_CROPS = null;
    @ObjectHolder(value = "blockychef:turmeric_crops", registryName = "block")
    public static final CropsBlock TURMERIC_CROPS = null;
    @ObjectHolder(value = "blockychef:pineapple_crops", registryName = "block")
    public static final CropsBlock PINEAPPLE_CROPS = null;
    @ObjectHolder(value = "blockychef:portobello_crops", registryName = "block")
    public static final CropsBlock PORTOBELLO_CROPS = null;
    @ObjectHolder(value = "blockychef:beans_crops", registryName = "block")
    public static final CropsBlock BEANS_CROPS = null;
    @ObjectHolder(value = "blockychef:peanuts_crops", registryName = "block")
    public static final CropsBlock PEANUTS_CROPS = null;
    @ObjectHolder(value = "blockychef:peas_crops", registryName = "block")
    public static final CropsBlock PEAS_CROPS = null;
    @ObjectHolder(value = "blockychef:soybeans_crops", registryName = "block")
    public static final CropsBlock SOYBEANS_CROPS = null;
    @ObjectHolder(value = "blockychef:basil_crops", registryName = "block")
    public static final CropsBlock BASIL_CROPS = null;
    @ObjectHolder(value = "blockychef:ginger_crops", registryName = "block")
    public static final CropsBlock GINGER_CROPS = null;
    @ObjectHolder(value = "blockychef:tea_crops", registryName = "block")
    public static final CropsBlock TEA_CROPS = null;


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
    @ObjectHolder(value = "blockychef:cabbage", registryName = "item")
    public static final Item CABBAGE = null;
    @ObjectHolder(value = "blockychef:chilli_pepper", registryName = "item")
    public static final Item CHILLI_PEPPER = null;
    @ObjectHolder(value = "blockychef:corn", registryName = "item")
    public static final Item CORN = null;
    @ObjectHolder(value = "blockychef:garlic", registryName = "item")
    public static final Item GARLIC = null;
    @ObjectHolder(value = "blockychef:leek", registryName = "item")
    public static final Item LEEK = null;
    @ObjectHolder(value = "blockychef:lettuce", registryName = "item")
    public static final Item LETTUCE = null;
    @ObjectHolder(value = "blockychef:onion", registryName = "item")
    public static final Item ONION = null;
    @ObjectHolder(value = "blockychef:parsley", registryName = "item")
    public static final Item PARSLEY = null;
    @ObjectHolder(value = "blockychef:raspberry", registryName = "item")
    public static final Item RASPBERRY = null;
    @ObjectHolder(value = "blockychef:spring_onion", registryName = "item")
    public static final Item SPRING_ONION = null;
    @ObjectHolder(value = "blockychef:strawberry", registryName = "item")
    public static final Item STRAWBERRY = null;
    @ObjectHolder(value = "blockychef:turmeric", registryName = "item")
    public static final Item TURMERIC = null;
    @ObjectHolder(value = "blockychef:pineapple", registryName = "item")
    public static final Item PINEAPPLE = null;
    @ObjectHolder(value = "blockychef:portobello", registryName = "item")
    public static final Item PORTOBELLO = null;
    @ObjectHolder(value = "blockychef:beans", registryName = "item")
    public static final Item BEANS = null;
    @ObjectHolder(value = "blockychef:peanuts", registryName = "item")
    public static final Item PEANUTS = null;
    @ObjectHolder(value = "blockychef:peas", registryName = "item")
    public static final Item PEAS = null;
    @ObjectHolder(value = "blockychef:soybeans", registryName = "item")
    public static final Item SOYBEANS = null;
    @ObjectHolder(value = "blockychef:basil", registryName = "item")
    public static final Item BASIL = null;
    @ObjectHolder(value = "blockychef:ginger", registryName = "item")
    public static final Item GINGER = null;
    @ObjectHolder(value = "blockychef:tea", registryName = "item")
    public static final Item TEA = null;


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
        helper.register("weeds", new WeedsBlock(), false);
        helper.register("tomato_crops", new CropsBlock(() -> TOMATO), false);
        helper.register("cucumber_crops", new CropsBlock(() -> CUCUMBER), false);
        helper.register("bell_pepper_crops", new CropsBlock(() -> BELL_PEPPER), false);
        helper.register("black_pepper_crops", new CropsBlock(() -> BLACK_PEPPER), false);
        helper.register("blueberry_crops", new CropsBlock(() -> BLUEBERRY), false);
        helper.register("broccoli_crops", new CropsBlock(() -> BROCCOLI), false);
        helper.register("cabbage_crops", new CropsBlock(() -> CABBAGE), false);
        helper.register("chilli_pepper_crops", new CropsBlock(() -> CHILLI_PEPPER), false);
        helper.register("corn_crops", new CropsBlock(() -> CORN), false);
        helper.register("garlic_crops", new CropsBlock(() -> GARLIC), false);
        helper.register("leek_crops", new CropsBlock(() -> LEEK), false);
        helper.register("lettuce_crops", new CropsBlock(() -> LETTUCE), false);
        helper.register("onion_crops", new CropsBlock(() -> ONION), false);
        helper.register("parsley_crops", new CropsBlock(() -> PARSLEY), false);
        helper.register("raspberry_crops", new CropsBlock(() -> RASPBERRY), false);
        helper.register("spring_onion_crops", new CropsBlock(() -> SPRING_ONION), false);
        helper.register("strawberry_crops", new CropsBlock(() -> STRAWBERRY), false);
        helper.register("turmeric_crops", new CropsBlock(() -> TURMERIC), false);
        helper.register("pineapple_crops", new CropsBlock(() -> PINEAPPLE), false);
        helper.register("portobello_crops", new CropsBlock(() -> PORTOBELLO), false);
        helper.register("beans_crops", new CropsBlock(() -> BEANS), false);
        helper.register("peanuts_crops", new CropsBlock(() -> PEANUTS), false);
        helper.register("peas_crops", new CropsBlock(() -> PEAS), false);
        helper.register("soybeans_crops", new CropsBlock(() -> SOYBEANS), false);
        helper.register("basil_crops", new CropsBlock(() -> BASIL), false);
        helper.register("ginger_crops", new CropsBlock(() -> GINGER), false);
        helper.register("tea_crops", new CropsBlock(() -> TEA), false);
    }

    private static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register("tomato", new ItemNameBlockItem(TOMATO_CROPS, new Item.Properties()));
        helper.register("cucumber", new ItemNameBlockItem(CUCUMBER_CROPS, new Item.Properties()));
        helper.register("bell_pepper", new ItemNameBlockItem(BELL_PEPPER_CROPS, new Item.Properties()));
        helper.register("black_pepper", new ItemNameBlockItem(BLACK_PEPPER_CROPS, new Item.Properties()));
        helper.register("blueberry", new ItemNameBlockItem(BLUEBERRY_CROPS, new Item.Properties()));
        helper.register("broccoli", new ItemNameBlockItem(BROCCOLI_CROPS, new Item.Properties()));
        helper.register("cabbage", new ItemNameBlockItem(CABBAGE_CROPS, new Item.Properties()));
        helper.register("chilli_pepper", new ItemNameBlockItem(CHILLI_PEPPER_CROPS, new Item.Properties()));
        helper.register("corn", new ItemNameBlockItem(CORN_CROPS, new Item.Properties()));
        helper.register("garlic", new ItemNameBlockItem(GARLIC_CROPS, new Item.Properties()));
        helper.register("leek", new ItemNameBlockItem(LEEK_CROPS, new Item.Properties()));
        helper.register("lettuce", new ItemNameBlockItem(LETTUCE_CROPS, new Item.Properties()));
        helper.register("onion", new ItemNameBlockItem(ONION_CROPS, new Item.Properties()));
        helper.register("parsley", new ItemNameBlockItem(PARSLEY_CROPS, new Item.Properties()));
        helper.register("raspberry", new ItemNameBlockItem(RASPBERRY_CROPS, new Item.Properties()));
        helper.register("spring_onion", new ItemNameBlockItem(SPRING_ONION_CROPS, new Item.Properties()));
        helper.register("strawberry", new ItemNameBlockItem(STRAWBERRY_CROPS, new Item.Properties()));
        helper.register("turmeric", new ItemNameBlockItem(TURMERIC_CROPS, new Item.Properties()));
        helper.register("pineapple", new ItemNameBlockItem(PINEAPPLE_CROPS, new Item.Properties()));
        helper.register("portobello", new ItemNameBlockItem(PORTOBELLO_CROPS, new Item.Properties()));
        helper.register("beans", new ItemNameBlockItem(BEANS_CROPS, new Item.Properties()));
        helper.register("peanuts", new ItemNameBlockItem(PEANUTS_CROPS, new Item.Properties()));
        helper.register("peas", new ItemNameBlockItem(PEAS_CROPS, new Item.Properties()));
        helper.register("soybeans", new ItemNameBlockItem(SOYBEANS_CROPS, new Item.Properties()));
        helper.register("basil", new ItemNameBlockItem(BASIL_CROPS, new Item.Properties()));
        helper.register("ginger", new ItemNameBlockItem(GINGER_CROPS, new Item.Properties()));
        helper.register("tea", new ItemNameBlockItem(TEA_CROPS, new Item.Properties()));
    }

    @FunctionalInterface // Registers blocks and schedules itemBlock registration
    private interface BlockRegistryHelper {
        void register(String name, Block block, boolean createItem);

        default void register(String name, Block block) {
            register(name, block, true);
        }
    }
}
