package tnt.blockychef.common;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.CropsBlock;
import tnt.blockychef.common.block.DecayingGrowingBlock;
import tnt.blockychef.common.block.TreeHangingFruitBlock;
import tnt.blockychef.common.item.CropSeedsItem;
import tnt.blockychef.common.thirst.ThirstMobEffect;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BlockyChef.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Registry {

    // Blocks ---
    @ObjectHolder(value = "blockychef:weeds", registryName = "block")
    public static final DecayingGrowingBlock WEEDS = null;
    @ObjectHolder(value = "blockychef:wheat_crops", registryName = "block")
    public static final CropsBlock WHEAT_CROPS = null;
    @ObjectHolder(value = "blockychef:potato_crops", registryName = "block")
    public static final CropsBlock POTATO_CROPS = null;
    @ObjectHolder(value = "blockychef:carrot_crops", registryName = "block")
    public static final CropsBlock CARROT_CROPS = null;
    @ObjectHolder(value = "blockychef:beetroot_crops", registryName = "block")
    public static final CropsBlock BEETROOT_CROPS = null;
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
    @ObjectHolder(value = "blockychef:grapes_crops", registryName = "block")
    public static final CropsBlock GRAPES_CROPS = null;
    @ObjectHolder(value = "blockychef:rice_crops", registryName = "block")
    public static final CropsBlock RICE_CROPS = null;
    @ObjectHolder(value = "blockychef:oat_crops", registryName = "block")
    public static final CropsBlock OAT_CROPS = null;


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
    @ObjectHolder(value = "blockychef:grapes", registryName = "item")
    public static final Item GRAPES = null;
    @ObjectHolder(value = "blockychef:rice", registryName = "item")
    public static final Item RICE = null;
    @ObjectHolder(value = "blockychef:oat", registryName = "item")
    public static final Item OAT = null;

    // Effects ---
    @ObjectHolder(value = "blockychef:thirst", registryName = "mob_effect")
    public static final MobEffect THIRST = null;

    // Damage Sources ---
    public static final DamageSource DEHYDRATATION = new DamageSource("blockychef.dehydratation").bypassArmor().bypassMagic();

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
        event.register(ForgeRegistries.MOB_EFFECTS.getRegistryKey(), Registry::registerMobEffects);
    }

    private static void registerBlocks(BlockRegistryHelper helper) {
        helper.register("weeds", new DecayingGrowingBlock(BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)), false);
        helper.register("wheat_crops", new CropsBlock(() -> Items.WHEAT_SEEDS), false);
        helper.register("potato_crops", new CropsBlock(() -> Items.POTATO), false);
        helper.register("carrot_crops", new CropsBlock(() -> Items.CARROT), false);
        helper.register("beetroot_crops", new CropsBlock(() -> Items.BEETROOT_SEEDS), false);
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
        helper.register("grapes_crops", new CropsBlock(() -> GRAPES), false);
        helper.register("rice_crops", new CropsBlock(() -> RICE), false);
        helper.register("oat_crops", new CropsBlock(() -> OAT), false);
        helper.register("almond_fruit", new TreeHangingFruitBlock(), false);
        helper.register("apple_fruit", new TreeHangingFruitBlock(), false);
        helper.register("avocado_fruit", new TreeHangingFruitBlock(), false);
        helper.register("banana_fruit", new TreeHangingFruitBlock(), false);
        helper.register("black_olives_fruit", new TreeHangingFruitBlock(), false);
        helper.register("cherry_fruit", new TreeHangingFruitBlock(), false);
        helper.register("coconut_fruit", new TreeHangingFruitBlock(), false);
        helper.register("green_olives_fruit", new TreeHangingFruitBlock(), false);
        helper.register("hazelnut_fruit", new TreeHangingFruitBlock(), false);
        helper.register("lemon_fruit", new TreeHangingFruitBlock(), false);
        helper.register("lime_fruit", new TreeHangingFruitBlock(), false);
        helper.register("orange_fruit", new TreeHangingFruitBlock(), false);
        helper.register("peach_fruit", new TreeHangingFruitBlock(), false);
        helper.register("pear_fruit", new TreeHangingFruitBlock(), false);
        helper.register("plum_fruit", new TreeHangingFruitBlock(), false);
        helper.register("vanilla_fruit", new TreeHangingFruitBlock(), false);
    }

    private static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register("tomato", new CropSeedsItem(TOMATO_CROPS, new Item.Properties()));
        helper.register("cucumber", new CropSeedsItem(CUCUMBER_CROPS, new Item.Properties()));
        helper.register("bell_pepper", new CropSeedsItem(BELL_PEPPER_CROPS, new Item.Properties()));
        helper.register("black_pepper", new CropSeedsItem(BLACK_PEPPER_CROPS, new Item.Properties()));
        helper.register("blueberry", new CropSeedsItem(BLUEBERRY_CROPS, new Item.Properties()));
        helper.register("broccoli", new CropSeedsItem(BROCCOLI_CROPS, new Item.Properties()));
        helper.register("cabbage", new CropSeedsItem(CABBAGE_CROPS, new Item.Properties()));
        helper.register("chilli_pepper", new CropSeedsItem(CHILLI_PEPPER_CROPS, new Item.Properties()));
        helper.register("corn", new CropSeedsItem(CORN_CROPS, new Item.Properties()));
        helper.register("garlic", new CropSeedsItem(GARLIC_CROPS, new Item.Properties()));
        helper.register("leek", new CropSeedsItem(LEEK_CROPS, new Item.Properties()));
        helper.register("lettuce", new CropSeedsItem(LETTUCE_CROPS, new Item.Properties()));
        helper.register("onion", new CropSeedsItem(ONION_CROPS, new Item.Properties()));
        helper.register("parsley", new CropSeedsItem(PARSLEY_CROPS, new Item.Properties()));
        helper.register("raspberry", new CropSeedsItem(RASPBERRY_CROPS, new Item.Properties()));
        helper.register("spring_onion", new CropSeedsItem(SPRING_ONION_CROPS, new Item.Properties()));
        helper.register("strawberry", new CropSeedsItem(STRAWBERRY_CROPS, new Item.Properties()));
        helper.register("turmeric", new CropSeedsItem(TURMERIC_CROPS, new Item.Properties()));
        helper.register("pineapple", new CropSeedsItem(PINEAPPLE_CROPS, new Item.Properties()));
        helper.register("portobello", new CropSeedsItem(PORTOBELLO_CROPS, new Item.Properties()));
        helper.register("beans", new CropSeedsItem(BEANS_CROPS, new Item.Properties()));
        helper.register("peanuts", new CropSeedsItem(PEANUTS_CROPS, new Item.Properties()));
        helper.register("peas", new CropSeedsItem(PEAS_CROPS, new Item.Properties()));
        helper.register("soybeans", new CropSeedsItem(SOYBEANS_CROPS, new Item.Properties()));
        helper.register("basil", new CropSeedsItem(BASIL_CROPS, new Item.Properties()));
        helper.register("ginger", new CropSeedsItem(GINGER_CROPS, new Item.Properties()));
        helper.register("tea", new CropSeedsItem(TEA_CROPS, new Item.Properties()));
        helper.register("grapes", new CropSeedsItem(GRAPES_CROPS, new Item.Properties()));
        helper.register("rice", new CropSeedsItem(RICE_CROPS, new Item.Properties()));
        helper.register("oat", new CropSeedsItem(OAT_CROPS, new Item.Properties()));
        helper.register("almond", new Item(new Item.Properties()));
        helper.register("apple", new Item(new Item.Properties()));
        helper.register("avocado", new Item(new Item.Properties()));
        helper.register("banana", new Item(new Item.Properties()));
        helper.register("black_olives", new Item(new Item.Properties()));
        helper.register("cherry", new Item(new Item.Properties()));
        helper.register("coconut", new Item(new Item.Properties()));
        helper.register("green_olives", new Item(new Item.Properties()));
        helper.register("hazelnut", new Item(new Item.Properties()));
        helper.register("lemon", new Item(new Item.Properties()));
        helper.register("lime", new Item(new Item.Properties()));
        helper.register("orange", new Item(new Item.Properties()));
        helper.register("peach", new Item(new Item.Properties()));
        helper.register("pear", new Item(new Item.Properties()));
        helper.register("plum", new Item(new Item.Properties()));
        helper.register("vanilla", new Item(new Item.Properties()));
    }

    private static void registerMobEffects(RegisterEvent.RegisterHelper<MobEffect> helper) {
        helper.register("thirst", new ThirstMobEffect(MobEffectCategory.HARMFUL, 0x97AF5D));
    }

    @FunctionalInterface // Registers blocks and schedules itemBlock registration
    private interface BlockRegistryHelper {
        void register(String name, Block block, boolean createItem);

        default void register(String name, Block block) {
            register(name, block, true);
        }
    }
}
