package tnt.blockychef.common;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.*;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;
import tnt.blockychef.common.effect.HydrationMobEffect;
import tnt.blockychef.common.effect.ThirstMobEffect;
import tnt.blockychef.common.food.DrinkList;
import tnt.blockychef.common.food.FoodList;
import tnt.blockychef.common.food.recipe.DryingRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefItems;
import tnt.blockychef.common.item.CropSeedsItem;
import tnt.blockychef.common.item.EdibleCropSeedItem;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BlockyChef.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Registry {

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
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), Registry::registerBlockEntities);
        event.register(ForgeRegistries.MOB_EFFECTS.getRegistryKey(), Registry::registerMobEffects);
        event.register(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), helper -> {
            RecipeTypeRegistryHelper registryHelper = id -> {
                helper.register(id, new RecipeType<>() {
                    @Override
                    public String toString() {
                        return BlockyChef.MODID + ":" + id;
                    }
                });
            };
            registerRecipeTypes(registryHelper);
        });
        event.register(ForgeRegistries.RECIPE_SERIALIZERS.getRegistryKey(), Registry::registerRecipeSerializers);
    }

    private static void registerBlocks(BlockRegistryHelper helper) {
        helper.register("weeds", new DecayingGrowingBlock(BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)), false);
        helper.register("wheat_crops", new CropsBlock(() -> Items.WHEAT_SEEDS), false);
        helper.register("potato_crops", new CropsBlock(() -> Items.POTATO), false);
        helper.register("carrot_crops", new CropsBlock(() -> Items.CARROT), false);
        helper.register("beetroot_crops", new CropsBlock(() -> Items.BEETROOT_SEEDS), false);
        helper.register("tomato_crops", new CropsBlock(() -> BlockyChefItems.TOMATO), false);
        helper.register("cucumber_crops", new CropsBlock(() -> BlockyChefItems.CUCUMBER), false);
        helper.register("bell_pepper_crops", new CropsBlock(() -> BlockyChefItems.BELL_PEPPER), false);
        helper.register("black_pepper_crops", new CropsBlock(() -> BlockyChefItems.BLACK_PEPPER_CLUSTER), false);
        helper.register("blueberry_crops", new CropsBlock(() -> BlockyChefItems.BLUEBERRY), false);
        helper.register("broccoli_crops", new CropsBlock(() -> BlockyChefItems.BROCCOLI), false);
        helper.register("cabbage_crops", new CropsBlock(() -> BlockyChefItems.CABBAGE), false);
        helper.register("chilli_pepper_crops", new CropsBlock(() -> BlockyChefItems.CHILLI_PEPPER), false);
        helper.register("corn_crops", new CropsBlock(() -> BlockyChefItems.CORN_COB), false);
        helper.register("garlic_crops", new CropsBlock(() -> BlockyChefItems.GARLIC), false);
        helper.register("leek_crops", new CropsBlock(() -> BlockyChefItems.LEEK), false);
        helper.register("lettuce_crops", new CropsBlock(() -> BlockyChefItems.LETTUCE), false);
        helper.register("onion_crops", new CropsBlock(() -> BlockyChefItems.ONION), false);
        helper.register("parsley_crops", new CropsBlock(() -> BlockyChefItems.PARSLEY), false);
        helper.register("raspberry_crops", new CropsBlock(() -> BlockyChefItems.RASPBERRY), false);
        helper.register("spring_onion_crops", new CropsBlock(() -> BlockyChefItems.SPRING_ONION), false);
        helper.register("strawberry_crops", new CropsBlock(() -> BlockyChefItems.STRAWBERRY), false);
        helper.register("turmeric_crops", new CropsBlock(() -> BlockyChefItems.TURMERIC), false);
        helper.register("pineapple_crops", new CropsBlock(() -> BlockyChefItems.PINEAPPLE), false);
        helper.register("portobello_crops", new CropsBlock(() -> BlockyChefItems.PORTOBELLO_MUSHROOM), false);
        helper.register("beans_crops", new CropsBlock(() -> BlockyChefItems.BEAN_POD), false);
        helper.register("peanuts_crops", new CropsBlock(() -> BlockyChefItems.PEANUT), false);
        helper.register("peas_crops", new CropsBlock(() -> BlockyChefItems.PEA_POD), false);
        helper.register("soybeans_crops", new CropsBlock(() -> BlockyChefItems.SOYBEAN_POD), false);
        helper.register("basil_crops", new CropsBlock(() -> BlockyChefItems.BASIL), false);
        helper.register("ginger_crops", new CropsBlock(() -> BlockyChefItems.GINGER_ROOT), false);
        helper.register("tea_crops", new CropsBlock(() -> BlockyChefItems.TEA_LEAF), false);
        helper.register("grapes_crops", new CropsBlock(() -> BlockyChefItems.GRAPES), false);
        helper.register("rice_crops", new CropsBlock(() -> BlockyChefItems.RICE_CROP), false);
        helper.register("oat_crops", new CropsBlock(() -> BlockyChefItems.OAT_CROP), false);
        helper.register("mustard_crops", new CropsBlock(() -> BlockyChefItems.MUSTARD_PODS), false);
        helper.register("coffee_crops", new CropsBlock(() -> BlockyChefItems.COFFEE_BEANS_CLUSTER), false);
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
        helper.register("walnut_fruit", new TreeHangingFruitBlock(), false);
        helper.register("drying_rack", new DryingRackBlock());
        helper.register("stove", new StoveBlock());
        helper.register("oak_cutting_board", new CuttingBoardBlock());
        helper.register("spruce_cutting_board", new CuttingBoardBlock());
        helper.register("birch_cutting_board", new CuttingBoardBlock());
        helper.register("jungle_cutting_board", new CuttingBoardBlock());
        helper.register("acacia_cutting_board", new CuttingBoardBlock());
        helper.register("dark_oak_cutting_board", new CuttingBoardBlock());
        helper.register("mangrove_cutting_board", new CuttingBoardBlock());
        helper.register("crimson_cutting_board", new CuttingBoardBlock());
        helper.register("warped_cutting_board", new CuttingBoardBlock());
        helper.register("granite_mortar_and_pestle", new MortarAndPestleBlock());
        helper.register("andesite_mortar_and_pestle", new MortarAndPestleBlock());
        helper.register("diorite_mortar_and_pestle", new MortarAndPestleBlock());
        helper.register("quartz_mortar_and_pestle", new MortarAndPestleBlock());
        helper.register("deepslate_mortar_and_pestle", new MortarAndPestleBlock());
        helper.register("oak_mixing_bowl", new MixingBowlBlock());
        helper.register("spruce_mixing_bowl", new MixingBowlBlock());
        helper.register("birch_mixing_bowl", new MixingBowlBlock());
        helper.register("jungle_mixing_bowl", new MixingBowlBlock());
        helper.register("acacia_mixing_bowl", new MixingBowlBlock());
        helper.register("dark_oak_mixing_bowl", new MixingBowlBlock());
        helper.register("mangrove_mixing_bowl", new MixingBowlBlock());
        helper.register("crimson_mixing_bowl", new MixingBowlBlock());
        helper.register("warped_mixing_bowl", new MixingBowlBlock());
        helper.register("mixer", new MixerBlock());
        helper.register("meat_grinder", new MeatGrinderBlock());
        helper.register("pasta_machine", new PastaMachineBlock());
        helper.register("dough_maker", new DoughMakerBlock());
    }

    private static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register("tomato", new EdibleCropSeedItem(BlockyChefBlocks.TOMATO_CROPS, new Item.Properties().food(FoodList.TOMATO), DrinkList.TOMATO));
        helper.register("cucumber", new CropSeedsItem(BlockyChefBlocks.CUCUMBER_CROPS, new Item.Properties()));
        helper.register("bell_pepper", new CropSeedsItem(BlockyChefBlocks.BELL_PEPPER_CROPS, new Item.Properties()));
        helper.register("black_pepper_cluster", new CropSeedsItem(BlockyChefBlocks.BLACK_PEPPER_CROPS, new Item.Properties()));
        helper.register("blueberry", new CropSeedsItem(BlockyChefBlocks.BLUEBERRY_CROPS, new Item.Properties()));
        helper.register("broccoli", new CropSeedsItem(BlockyChefBlocks.BROCCOLI_CROPS, new Item.Properties()));
        helper.register("cabbage", new CropSeedsItem(BlockyChefBlocks.CABBAGE_CROPS, new Item.Properties()));
        helper.register("chilli_pepper", new CropSeedsItem(BlockyChefBlocks.CHILLI_PEPPER_CROPS, new Item.Properties()));
        helper.register("corn_cob", new CropSeedsItem(BlockyChefBlocks.CORN_CROPS, new Item.Properties()));
        helper.register("garlic", new CropSeedsItem(BlockyChefBlocks.GARLIC_CROPS, new Item.Properties()));
        helper.register("leek", new CropSeedsItem(BlockyChefBlocks.LEEK_CROPS, new Item.Properties()));
        helper.register("lettuce", new CropSeedsItem(BlockyChefBlocks.LETTUCE_CROPS, new Item.Properties()));
        helper.register("onion", new CropSeedsItem(BlockyChefBlocks.ONION_CROPS, new Item.Properties()));
        helper.register("parsley", new CropSeedsItem(BlockyChefBlocks.PARSLEY_CROPS, new Item.Properties()));
        helper.register("raspberry", new CropSeedsItem(BlockyChefBlocks.RASPBERRY_CROPS, new Item.Properties()));
        helper.register("spring_onion", new CropSeedsItem(BlockyChefBlocks.SPRING_ONION_CROPS, new Item.Properties()));
        helper.register("strawberry", new CropSeedsItem(BlockyChefBlocks.STRAWBERRY_CROPS, new Item.Properties()));
        helper.register("turmeric", new CropSeedsItem(BlockyChefBlocks.TURMERIC_CROPS, new Item.Properties()));
        helper.register("pineapple", new CropSeedsItem(BlockyChefBlocks.PINEAPPLE_CROPS, new Item.Properties()));
        helper.register("portobello_mushroom", new CropSeedsItem(BlockyChefBlocks.PORTOBELLO_CROPS, new Item.Properties()));
        helper.register("bean_pod", new CropSeedsItem(BlockyChefBlocks.BEANS_CROPS, new Item.Properties()));
        helper.register("peanut", new CropSeedsItem(BlockyChefBlocks.PEANUTS_CROPS, new Item.Properties()));
        helper.register("pea_pod", new CropSeedsItem(BlockyChefBlocks.PEAS_CROPS, new Item.Properties()));
        helper.register("soybean_pod", new CropSeedsItem(BlockyChefBlocks.SOYBEANS_CROPS, new Item.Properties()));
        helper.register("basil", new CropSeedsItem(BlockyChefBlocks.BASIL_CROPS, new Item.Properties()));
        helper.register("ginger_root", new CropSeedsItem(BlockyChefBlocks.GINGER_CROPS, new Item.Properties()));
        helper.register("tea_leaf", new CropSeedsItem(BlockyChefBlocks.TEA_CROPS, new Item.Properties()));
        helper.register("grapes", new CropSeedsItem(BlockyChefBlocks.GRAPES_CROPS, new Item.Properties()));
        helper.register("rice_crop", new CropSeedsItem(BlockyChefBlocks.RICE_CROPS, new Item.Properties()));
        helper.register("oat_crop", new CropSeedsItem(BlockyChefBlocks.OAT_CROPS, new Item.Properties()));
        helper.register("mustard_pods", new CropSeedsItem(BlockyChefBlocks.MUSTARD_CROPS, new Item.Properties()));
        helper.register("coffee_beans_cluster", new CropSeedsItem(BlockyChefBlocks.COFFEE_CROPS, new Item.Properties()));
        helper.register("almond", new Item(new Item.Properties()));
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
        helper.register("vanilla_pods", new Item(new Item.Properties()));
        helper.register("walnut", new Item(new Item.Properties()));
    }

    private static void registerBlockEntities(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        helper.register("drying_rack", BlockEntityType.Builder.of(DryingRackBlockEntity::new, BlockyChefBlocks.DRYING_RACK).build(null));
    }

    private static void registerMobEffects(RegisterEvent.RegisterHelper<MobEffect> helper) {
        helper.register("thirst", new ThirstMobEffect(MobEffectCategory.HARMFUL, 0x97AF5D));
        helper.register("hydration", new HydrationMobEffect(MobEffectCategory.BENEFICIAL, 0x3080E8));
    }

    private static void registerRecipeTypes(RecipeTypeRegistryHelper helper) {
        helper.register("drying_recipe");
    }

    private static void registerRecipeSerializers(RegisterEvent.RegisterHelper<RecipeSerializer<?>> helper) {
        helper.register("drying", new DryingRecipe.Serializer());
    }

    @FunctionalInterface // Registers blocks and schedules itemBlock registration
    private interface BlockRegistryHelper {
        void register(String name, Block block, boolean createItem);

        default void register(String name, Block block) {
            register(name, block, true);
        }
    }

    @FunctionalInterface
    private interface RecipeTypeRegistryHelper {
        void register(String id);
    }
}
