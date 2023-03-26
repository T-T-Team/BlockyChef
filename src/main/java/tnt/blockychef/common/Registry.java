package tnt.blockychef.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.*;
import tnt.blockychef.common.block.entity.*;
import tnt.blockychef.common.effect.HydrationMobEffect;
import tnt.blockychef.common.effect.ThirstMobEffect;
import tnt.blockychef.common.food.DrinkList;
import tnt.blockychef.common.food.FoodList;
import tnt.blockychef.common.food.recipe.CodecRecipeSerializer;
import tnt.blockychef.common.food.recipe.CuttingBoardRecipe;
import tnt.blockychef.common.food.recipe.DryingRecipe;
import tnt.blockychef.common.food.recipe.GratingRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefItems;
import tnt.blockychef.common.init.BlockyChefTrees;
import tnt.blockychef.common.item.CropSeedsItem;
import tnt.blockychef.common.item.DrinkableItem;
import tnt.blockychef.common.item.EdibleCropSeedItem;
import tnt.blockychef.common.menu.CuttingBoardMenu;
import tnt.blockychef.levelgen.feature.WeightedFeatureConfiguration;
import tnt.blockychef.levelgen.feature.WeightedSelectorFeature;
import tnt.blockychef.levelgen.tree.SimpleTreeGrower;
import tnt.blockychef.levelgen.tree.TreeFruitDecorator;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BlockyChef.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Registry {

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
        event.register(ForgeRegistries.MENU_TYPES.getRegistryKey(), Registry::registerMenuTypes);
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
        event.register(ForgeRegistries.FEATURES.getRegistryKey(), Registry::registerFeatures);
        event.register(ForgeRegistries.TREE_DECORATOR_TYPES.getRegistryKey(), Registry::registerTreeDecorators);
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
        helper.register("eggplant_crops", new CropsBlock(() -> BlockyChefItems.EGGPLANT), false);
        helper.register("spinach_crops", new CropsBlock(() -> BlockyChefItems.SPINACH_LEAF), false);
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
        helper.register("juicer", new JuicerBlock());
        helper.register("teapot", new TeapotBlock());
        helper.register("acacia_barrel", new BarrelBlock());
        helper.register("birch_barrel", new BarrelBlock());
        helper.register("crimson_barrel", new BarrelBlock());
        helper.register("dark_oak_barrel", new BarrelBlock());
        helper.register("jungle_barrel", new BarrelBlock());
        helper.register("mangrove_barrel", new BarrelBlock());
        helper.register("oak_barrel", new BarrelBlock());
        helper.register("spruce_barrel", new BarrelBlock());
        helper.register("warped_barrel", new BarrelBlock());
        helper.register("toaster", new ToasterBlock());
        helper.register("grill", new GrillBlock());
        helper.register("grater", new GraterBlock());
        helper.register("pot", new PotBlock());
        helper.register("pan", new PanBlock());
        helper.register("saucepan", new SaucepanBlock());
        helper.register("concrete_concrete_kitchen_counter", new KitchenCounterBlock());
        helper.register("concrete_plank_kitchen_counter", new KitchenCounterBlock());
        helper.register("plank_concrete_kitchen_counter", new KitchenCounterBlock());
        helper.register("plank_plank_kitchen_counter", new KitchenCounterBlock());
        helper.register("concrete_concrete_cooking_table", new CookingTableBlock());
        helper.register("concrete_plank_cooking_table", new CookingTableBlock());
        helper.register("plank_concrete_cooking_table", new CookingTableBlock());
        helper.register("plank_plank_cooking_table", new CookingTableBlock());
        helper.register("concrete_kitchen_cabinet", new KitchenCabinetBlock());
        helper.register("plank_kitchen_cabinet", new KitchenCabinetBlock());
        helper.register("concrete_concrete_kitchen_counter_corner", new KitchenCounterCornerBlock());
        helper.register("concrete_plank_kitchen_counter_corner", new KitchenCounterCornerBlock());
        helper.register("plank_concrete_kitchen_counter_corner", new KitchenCounterCornerBlock());
        helper.register("plank_plank_kitchen_counter_corner", new KitchenCounterCornerBlock());
        helper.register("concrete_concrete_kitchen_sink", new KitchenSinkBlock());
        helper.register("concrete_plank_kitchen_sink", new KitchenSinkBlock());
        helper.register("plank_concrete_kitchen_sink", new KitchenSinkBlock());
        helper.register("plank_plank_kitchen_sink", new KitchenSinkBlock());
        helper.register("cinnamon_log", new CinnamonLogBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)));
        helper.register("cinnamon_stripped_log", new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)));
        helper.register("cinnamon_leaves", new LeavesBlock(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS)
                .noOcclusion().isValidSpawn(Registry::allowParrotOrOcelotSpawn).isSuffocating(Registry::alwaysFalse).isViewBlocking(Registry::alwaysFalse)));
        helper.register("cinnamon_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.CINNAMON_TREE)));
        helper.register("apple_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.APPLE_TREE)));
        helper.register("pear_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.PEAR_TREE)));
        helper.register("orange_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.ORANGE_TREE)));
        helper.register("banana_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.BANANA_TREE)));
        helper.register("almond_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.ALMOND_TREE)));
        helper.register("avocado_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.AVOCADO_TREE)));
        helper.register("black_olives_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.BLACK_OLIVES_TREE)));
        helper.register("green_olives_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.GREEN_OLIVES_TREE)));
        helper.register("lemon_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.LEMON_TREE)));
        helper.register("lime_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.LIME_TREE)));
        helper.register("peach_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.PEACH_TREE)));
        helper.register("coconut_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.COCONUT_TREE)));
        helper.register("vanilla_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.VANILLA_TREE)));
        helper.register("cherry_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.CHERRY_TREE)));
        helper.register("hazelnut_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.HAZELNUT_TREE)));
        helper.register("plum_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.PLUM_TREE)));
        helper.register("walnut_sapling", new TreeSaplingBlock(new SimpleTreeGrower(BlockyChefTrees.WALNUT_TREE)));
    }

    private static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register("tomato", new EdibleCropSeedItem(BlockyChefBlocks.TOMATO_CROPS, new Item.Properties().food(FoodList.TOMATO), DrinkList.TOMATO));
        helper.register("cucumber", new CropSeedsItem(BlockyChefBlocks.CUCUMBER_CROPS, new Item.Properties().food(FoodList.CUCUMBER)));
        helper.register("bell_pepper", new EdibleCropSeedItem(BlockyChefBlocks.BELL_PEPPER_CROPS, new Item.Properties().food(FoodList.BELL_PEPPER), DrinkList.BELL_PEPPER));
        helper.register("black_pepper_cluster", new EdibleCropSeedItem(BlockyChefBlocks.BLACK_PEPPER_CROPS, new Item.Properties().food(FoodList.BLACK_PEPPER), DrinkList.BLACK_PEPPER));
        helper.register("blueberry", new CropSeedsItem(BlockyChefBlocks.BLUEBERRY_CROPS, new Item.Properties().food(FoodList.BLUEBERRY)));
        helper.register("broccoli", new EdibleCropSeedItem(BlockyChefBlocks.BROCCOLI_CROPS, new Item.Properties().food(FoodList.BROCCOLI), DrinkList.BROCCOLI));
        helper.register("cabbage", new EdibleCropSeedItem(BlockyChefBlocks.CABBAGE_CROPS, new Item.Properties().food(FoodList.CABBAGE), DrinkList.CABBAGE));
        helper.register("chilli_pepper", new EdibleCropSeedItem(BlockyChefBlocks.CHILLI_PEPPER_CROPS, new Item.Properties().food(FoodList.CHILLI_PEPPER), DrinkList.CHILLI_PEPPER));
        helper.register("corn_cob", new EdibleCropSeedItem(BlockyChefBlocks.CORN_CROPS, new Item.Properties().food(FoodList.CORN), DrinkList.CORN));
        helper.register("garlic", new EdibleCropSeedItem(BlockyChefBlocks.GARLIC_CROPS, new Item.Properties().food(FoodList.GARLIC), DrinkList.GARLIC));
        helper.register("leek", new EdibleCropSeedItem(BlockyChefBlocks.LEEK_CROPS, new Item.Properties().food(FoodList.LEEK), DrinkList.LEEK));
        helper.register("lettuce", new EdibleCropSeedItem(BlockyChefBlocks.LETTUCE_CROPS, new Item.Properties().food(FoodList.LETTUCE), DrinkList.LETTUCE));
        helper.register("onion", new EdibleCropSeedItem(BlockyChefBlocks.ONION_CROPS, new Item.Properties().food(FoodList.ONION), DrinkList.ONION));
        helper.register("parsley", new EdibleCropSeedItem(BlockyChefBlocks.PARSLEY_CROPS, new Item.Properties().food(FoodList.PARSLEY), DrinkList.PARSLEY));
        helper.register("raspberry", new CropSeedsItem(BlockyChefBlocks.RASPBERRY_CROPS, new Item.Properties().food(FoodList.RASPBERRY)));
        helper.register("spring_onion", new EdibleCropSeedItem(BlockyChefBlocks.SPRING_ONION_CROPS, new Item.Properties().food(FoodList.SPRING_ONION), DrinkList.SPRING_ONION));
        helper.register("strawberry", new CropSeedsItem(BlockyChefBlocks.STRAWBERRY_CROPS, new Item.Properties().food(FoodList.STRAWBERRY)));
        helper.register("turmeric", new EdibleCropSeedItem(BlockyChefBlocks.TURMERIC_CROPS, new Item.Properties().food(FoodList.TURMERIC), DrinkList.TURMERIC));
        helper.register("pineapple", new EdibleCropSeedItem(BlockyChefBlocks.PINEAPPLE_CROPS, new Item.Properties().food(FoodList.PINEAPPLE), DrinkList.PINEAPPLE));
        helper.register("portobello_mushroom", new EdibleCropSeedItem(BlockyChefBlocks.PORTOBELLO_CROPS, new Item.Properties().food(FoodList.PORTOBELLO), DrinkList.PORTOBELLO));
        helper.register("bean_pod", new EdibleCropSeedItem(BlockyChefBlocks.BEANS_CROPS, new Item.Properties().food(FoodList.BEANS), DrinkList.BEANS));
        helper.register("peanut", new EdibleCropSeedItem(BlockyChefBlocks.PEANUTS_CROPS, new Item.Properties().food(FoodList.PEANUTS), DrinkList.PEANUTS));
        helper.register("pea_pod", new EdibleCropSeedItem(BlockyChefBlocks.PEAS_CROPS, new Item.Properties().food(FoodList.PEAS), DrinkList.PEAS));
        helper.register("soybean_pod", new EdibleCropSeedItem(BlockyChefBlocks.SOYBEANS_CROPS, new Item.Properties().food(FoodList.SOYBEANS), DrinkList.SOYBEANS));
        helper.register("basil", new EdibleCropSeedItem(BlockyChefBlocks.BASIL_CROPS, new Item.Properties().food(FoodList.BASIL), DrinkList.BASIL));
        helper.register("ginger_root", new EdibleCropSeedItem(BlockyChefBlocks.GINGER_CROPS, new Item.Properties().food(FoodList.GINGER), DrinkList.GINGER));
        helper.register("tea_leaf", new EdibleCropSeedItem(BlockyChefBlocks.TEA_CROPS, new Item.Properties().food(FoodList.TEA), DrinkList.TEA));
        helper.register("grapes", new EdibleCropSeedItem(BlockyChefBlocks.GRAPES_CROPS, new Item.Properties().food(FoodList.GRAPES), DrinkList.GRAPES));
        helper.register("rice_crop", new EdibleCropSeedItem(BlockyChefBlocks.RICE_CROPS, new Item.Properties().food(FoodList.RICE), DrinkList.RICE));
        helper.register("oat_crop", new EdibleCropSeedItem(BlockyChefBlocks.OAT_CROPS, new Item.Properties().food(FoodList.OAT), DrinkList.OAT));
        helper.register("mustard_pods", new EdibleCropSeedItem(BlockyChefBlocks.MUSTARD_CROPS, new Item.Properties().food(FoodList.MUSTARD), DrinkList.MUSTARD));
        helper.register("coffee_beans_cluster", new EdibleCropSeedItem(BlockyChefBlocks.COFFEE_CROPS, new Item.Properties().food(FoodList.COFFEE), DrinkList.COFFEE));
        helper.register("eggplant", new EdibleCropSeedItem(BlockyChefBlocks.EGGPLANT_CROPS, new Item.Properties().food(FoodList.EGGPLANT), DrinkList.EGGPLANT));
        helper.register("spinach_leaf", new EdibleCropSeedItem(BlockyChefBlocks.SPINACH_CROPS, new Item.Properties().food(FoodList.SPINACH_LEAF), DrinkList.SPINACH_LEAF));
        helper.register("almond", new DrinkableItem(new Item.Properties().food(FoodList.ALMOND), DrinkList.ALMOND));
        helper.register("avocado", new DrinkableItem(new Item.Properties().food(FoodList.AVOCADO), DrinkList.AVOCADO));
        helper.register("banana", new DrinkableItem(new Item.Properties().food(FoodList.BANANA), DrinkList.BANANA));
        helper.register("black_olives", new DrinkableItem(new Item.Properties().food(FoodList.BLACK_OLIVES), DrinkList.BLACK_OLIVES));
        helper.register("cherry", new Item(new Item.Properties().food(FoodList.CHERRY)));
        helper.register("coconut", new Item(new Item.Properties()));
        helper.register("green_olives", new DrinkableItem(new Item.Properties().food(FoodList.GREEN_OLIVES), DrinkList.GREEN_OLIVES));
        helper.register("hazelnut", new DrinkableItem(new Item.Properties().food(FoodList.HAZELNUT), DrinkList.HAZELNUT));
        helper.register("lemon", new Item(new Item.Properties().food(FoodList.LEMON)));
        helper.register("lime", new Item(new Item.Properties().food(FoodList.LIME)));
        helper.register("orange", new DrinkableItem(new Item.Properties().food(FoodList.ORANGE), DrinkList.ORANGE));
        helper.register("peach", new Item(new Item.Properties().food(FoodList.PEACH)));
        helper.register("pear", new Item(new Item.Properties().food(FoodList.PEAR)));
        helper.register("plum", new Item(new Item.Properties().food(FoodList.PLUM)));
        helper.register("cinnamon_bark", new Item(new Item.Properties()));
        helper.register("vanilla_pods", new DrinkableItem(new Item.Properties().food(FoodList.VANILLA_PODS), DrinkList.VANILLA_PODS));
        helper.register("walnut", new DrinkableItem(new Item.Properties().food(FoodList.WALNUT), DrinkList.WALNUT));

        helper.register("shrimp", new Item(new Item.Properties().food(FoodList.SHRIMP)));
        helper.register("raw_shrimp", new Item(new Item.Properties().food(FoodList.RAW_SHRIMP)));
        helper.register("fried_shrimp", new DrinkableItem(new Item.Properties().food(FoodList.FRIED_SHRIMP), DrinkList.FRIED_SHRIMP));
        helper.register("burnt_shrimp", new Item(new Item.Properties()));

        helper.register("apple_extract", new Item(new Item.Properties()));
        helper.register("apple_half", new Item(new Item.Properties()));
        helper.register("apple_marmalade", new Item(new Item.Properties()));
        helper.register("apple_slice", new Item(new Item.Properties()));
        helper.register("avocado_slice", new Item(new Item.Properties()));
        helper.register("banana_slice", new Item(new Item.Properties()));
        helper.register("bbq_sauce", new Item(new Item.Properties()));
        helper.register("beans", new Item(new Item.Properties()));
        helper.register("beef_burger_patty", new Item(new Item.Properties()));
        helper.register("beef_slice", new Item(new Item.Properties()));
        helper.register("beetroot_slice", new Item(new Item.Properties()));
        helper.register("bellpepper_slice", new Item(new Item.Properties()));
        helper.register("black_olive_slice", new Item(new Item.Properties()));
        helper.register("blueberry_extract", new Item(new Item.Properties()));
        helper.register("blueberry_marmalade", new Item(new Item.Properties()));
        helper.register("boiled_rice", new Item(new Item.Properties()));
        helper.register("bread_slice", new Item(new Item.Properties()));
        helper.register("broccoli_cuts", new Item(new Item.Properties()));
        helper.register("burnt_beef_burger_patty", new Item(new Item.Properties()));
        helper.register("burnt_chicken_burger_patty", new Item(new Item.Properties()));
        helper.register("burnt_pork_burger_patty", new Item(new Item.Properties()));
        helper.register("burnt_rice", new Item(new Item.Properties()));
        helper.register("cabbage_leaf", new Item(new Item.Properties()));
        helper.register("carrot_extract", new Item(new Item.Properties()));
        helper.register("carrot_slice", new Item(new Item.Properties()));
        helper.register("cherry_extract", new Item(new Item.Properties()));
        helper.register("cherry_marmalade", new Item(new Item.Properties()));
        helper.register("chicken_burger_patty", new Item(new Item.Properties()));
        helper.register("chilli_powder", new Item(new Item.Properties()));
        helper.register("cinnamon_powder", new Item(new Item.Properties()));
        helper.register("cocoa_powder", new Item(new Item.Properties()));
        helper.register("cocoa_spread", new Item(new Item.Properties()));
        helper.register("coconut_half", new Item(new Item.Properties()));
        helper.register("coconut_milk", new Item(new Item.Properties()));
        helper.register("coffee_beans", new Item(new Item.Properties()));
        helper.register("coffee_powder", new Item(new Item.Properties()));
        helper.register("corn", new Item(new Item.Properties()));
        helper.register("corn_meal", new Item(new Item.Properties()));
        helper.register("cracked_egg", new Item(new Item.Properties()));
        helper.register("cucumber_slice", new Item(new Item.Properties()));
        helper.register("curry_powder", new Item(new Item.Properties()));
        helper.register("diced_beef_meat", new Item(new Item.Properties()));
        helper.register("diced_beetroot", new Item(new Item.Properties()));
        helper.register("diced_carrot", new Item(new Item.Properties()));
        helper.register("diced_chicken_meat", new Item(new Item.Properties()));
        helper.register("diced_chilly_pepper", new Item(new Item.Properties()));
        helper.register("diced_onion", new Item(new Item.Properties()));
        helper.register("diced_pork_meat", new Item(new Item.Properties()));
        helper.register("diced_scallion", new Item(new Item.Properties()));
        helper.register("diced_tomato", new Item(new Item.Properties()));
        helper.register("dried_basil", new Item(new Item.Properties()));
        helper.register("dried_black_pepper_cluster", new Item(new Item.Properties()));
        helper.register("dried_bread", new Item(new Item.Properties()));
        helper.register("dried_chilli_pepper", new Item(new Item.Properties()));
        helper.register("dried_cinamon_bark", new Item(new Item.Properties()));
        helper.register("dried_corn", new Item(new Item.Properties()));
        helper.register("dried_corn_cob", new Item(new Item.Properties()));
        helper.register("dried_mustard_pods", new Item(new Item.Properties()));
        helper.register("dried_turmeric_slice", new Item(new Item.Properties()));
        helper.register("dried_vanilla_pods", new Item(new Item.Properties()));
        helper.register("eggplant_slice", new Item(new Item.Properties()));
        helper.register("egg_yolk", new Item(new Item.Properties()));
        helper.register("empty_bottle", new Item(new Item.Properties()));
        helper.register("empty_jar", new Item(new Item.Properties()));
        helper.register("fried_egg", new Item(new Item.Properties()));
        helper.register("garlic_clove", new Item(new Item.Properties()));
        helper.register("garlic_slice", new Item(new Item.Properties()));
        helper.register("ginger_slice", new Item(new Item.Properties()));
        helper.register("grape_extract", new Item(new Item.Properties()));
        helper.register("green_olive_slice", new Item(new Item.Properties()));
        helper.register("grilled_beef_burger_patty", new Item(new Item.Properties()));
        helper.register("grilled_chicken_burger_patty", new Item(new Item.Properties()));
        helper.register("grilled_pork_burger_patty", new Item(new Item.Properties()));
        helper.register("grounded_almond", new Item(new Item.Properties()));
        helper.register("grounded_basil", new Item(new Item.Properties()));
        helper.register("grounded_beef_meat", new Item(new Item.Properties()));
        helper.register("grounded_chicken_meat", new Item(new Item.Properties()));
        helper.register("grounded_hazelnuts", new Item(new Item.Properties()));
        helper.register("grounded_pork_meat", new Item(new Item.Properties()));
        helper.register("grounded_walnuts", new Item(new Item.Properties()));
        helper.register("ham_slice", new Item(new Item.Properties()));
        helper.register("hazelnuts", new Item(new Item.Properties()));
        helper.register("hollowed_bellpepper", new Item(new Item.Properties()));
        helper.register("hollowed_eggplant", new Item(new Item.Properties()));
        helper.register("hollowed_portobello", new Item(new Item.Properties()));
        helper.register("hollowed_potato", new Item(new Item.Properties()));
        helper.register("ketchup", new Item(new Item.Properties()));
        helper.register("leek_slice", new Item(new Item.Properties()));
        helper.register("lemon_extract", new Item(new Item.Properties()));
        helper.register("lemon_half", new Item(new Item.Properties()));
        helper.register("lemon_marmalade", new Item(new Item.Properties()));
        helper.register("lemon_slice", new Item(new Item.Properties()));
        helper.register("lettuce_leaf", new Item(new Item.Properties()));
        helper.register("lime_extract", new Item(new Item.Properties()));
        helper.register("lime_half", new Item(new Item.Properties()));
        helper.register("lime_marmalade", new Item(new Item.Properties()));
        helper.register("lime_slice", new Item(new Item.Properties()));
        helper.register("mayo", new Item(new Item.Properties()));
        helper.register("melon_extract", new Item(new Item.Properties()));
        helper.register("mustard", new Item(new Item.Properties()));
        helper.register("oat_meal", new Item(new Item.Properties()));
        helper.register("oil", new Item(new Item.Properties()));
        helper.register("olive_oil", new Item(new Item.Properties()));
        helper.register("onion_slice", new Item(new Item.Properties()));
        helper.register("orange_extract", new Item(new Item.Properties()));
        helper.register("orange_half", new Item(new Item.Properties()));
        helper.register("orange_marmalade", new Item(new Item.Properties()));
        helper.register("orange_slice", new Item(new Item.Properties()));
        helper.register("parsley_slice", new Item(new Item.Properties()));
        helper.register("parsley_stem", new Item(new Item.Properties()));
        helper.register("peach_extract", new Item(new Item.Properties()));
        helper.register("peach_half", new Item(new Item.Properties()));
        helper.register("peach_marmalade", new Item(new Item.Properties()));
        helper.register("peach_slice", new Item(new Item.Properties()));
        helper.register("peanuts", new Item(new Item.Properties()));
        helper.register("peanut_butter", new Item(new Item.Properties()));
        helper.register("pear_extract", new Item(new Item.Properties()));
        helper.register("pear_half", new Item(new Item.Properties()));
        helper.register("pear_slice", new Item(new Item.Properties()));
        helper.register("peas", new Item(new Item.Properties()));
        helper.register("peeled_banana", new Item(new Item.Properties()));
        helper.register("pepper_powder", new Item(new Item.Properties()));
        helper.register("picked_cherry", new Item(new Item.Properties()));
        helper.register("pineapple_cuts", new Item(new Item.Properties()));
        helper.register("pineapple_extract", new Item(new Item.Properties()));
        helper.register("pineapple_half", new Item(new Item.Properties()));
        helper.register("pineapple_rings", new Item(new Item.Properties()));
        helper.register("plum_extract", new Item(new Item.Properties()));
        helper.register("plum_half", new Item(new Item.Properties()));
        helper.register("plum_marmalade", new Item(new Item.Properties()));
        helper.register("plum_slice", new Item(new Item.Properties()));
        helper.register("pork_burger_patty", new Item(new Item.Properties()));
        helper.register("portobello_slice", new Item(new Item.Properties()));
        helper.register("potato_cuts", new Item(new Item.Properties()));
        helper.register("potato_slice", new Item(new Item.Properties()));
        helper.register("raspberry_extract", new Item(new Item.Properties()));
        helper.register("raspberry_marmalade", new Item(new Item.Properties()));
        helper.register("raw_bacon", new Item(new Item.Properties()));
        helper.register("raw_beef_steak", new Item(new Item.Properties()));
        helper.register("raw_chicken_breast", new Item(new Item.Properties()));
        helper.register("raw_chicken_drumstick", new Item(new Item.Properties()));
        helper.register("raw_chicken_wing", new Item(new Item.Properties()));
        helper.register("raw_corn_cob", new Item(new Item.Properties()));
        helper.register("raw_fish_fillet", new Item(new Item.Properties()));
        helper.register("raw_mutton_ribs", new Item(new Item.Properties()));
        helper.register("raw_mutton_slice", new Item(new Item.Properties()));
        helper.register("raw_pork_ribs", new Item(new Item.Properties()));
        helper.register("raw_pork_steak", new Item(new Item.Properties()));
        helper.register("raw_potato_fries", new Item(new Item.Properties()));
        helper.register("raw_rabbit_leg", new Item(new Item.Properties()));
        helper.register("raw_rabbit_slice", new Item(new Item.Properties()));
        helper.register("raw_salmon_fillet", new Item(new Item.Properties()));
        helper.register("rice", new Item(new Item.Properties()));
        helper.register("sake", new Item(new Item.Properties()));
        helper.register("salt", new Item(new Item.Properties()));
        helper.register("shreded_apple", new Item(new Item.Properties()));
        helper.register("shreded_cabbage", new Item(new Item.Properties()));
        helper.register("shreded_carrot", new Item(new Item.Properties()));
        helper.register("shreded_cucumber", new Item(new Item.Properties()));
        helper.register("shreded_ginger", new Item(new Item.Properties()));
        helper.register("shreded_lettuce", new Item(new Item.Properties()));
        helper.register("shreded_pear", new Item(new Item.Properties()));
        helper.register("shreded_potato", new Item(new Item.Properties()));
        helper.register("soybeans", new Item(new Item.Properties()));
        helper.register("soy_milk", new Item(new Item.Properties()));
        helper.register("soy_sauce", new Item(new Item.Properties()));
        helper.register("strawberry_extract", new Item(new Item.Properties()));
        helper.register("strawberry_marmalade", new Item(new Item.Properties()));
        helper.register("strawberry_slice", new Item(new Item.Properties()));
        helper.register("sweet_berry_extract", new Item(new Item.Properties()));
        helper.register("sweet_chilli_sauce", new Item(new Item.Properties()));
        helper.register("tomato_extract", new Item(new Item.Properties()));
        helper.register("tomato_slice", new Item(new Item.Properties()));
        helper.register("turmeric_powder", new Item(new Item.Properties()));
        helper.register("turmeric_slice", new Item(new Item.Properties()));
        helper.register("vanilla_powder", new Item(new Item.Properties()));
        helper.register("vinegar", new Item(new Item.Properties()));
        helper.register("walnuts", new Item(new Item.Properties()));
        helper.register("wine", new Item(new Item.Properties()));

    }

    private static void registerBlockEntities(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        helper.register("drying_rack", BlockEntityType.Builder.of(DryingRackBlockEntity::new, BlockyChefBlocks.DRYING_RACK).build(null));
        helper.register("kitchen_counter", BlockEntityType.Builder.of(KitchenCounterBlockEntity::new, BlockyChefBlocks.CC_KITCHEN_COUNTER, BlockyChefBlocks.CP_KITCHEN_COUNTER, BlockyChefBlocks.PC_KITCHEN_COUNTER, BlockyChefBlocks.PP_KITCHEN_COUNTER).build(null));
        helper.register("kitchen_sink", BlockEntityType.Builder.of(KitchenSinkBlockEntity::new, BlockyChefBlocks.CC_KITCHEN_SINK, BlockyChefBlocks.CP_KITCHEN_SINK, BlockyChefBlocks.PC_KITCHEN_SINK, BlockyChefBlocks.PP_KITCHEN_SINK).build(null));
        helper.register("kitchen_cabinet", BlockEntityType.Builder.of(KitchenCabinetBlockEntity::new, BlockyChefBlocks.C_KITCHEN_CABINET, BlockyChefBlocks.P_KITCHEN_CABINET).build(null));
        helper.register("cooking_table", BlockEntityType.Builder.of(CookingTableBlockEntity::new, BlockyChefBlocks.CC_COOKING_TABLE, BlockyChefBlocks.CP_COOKING_TABLE, BlockyChefBlocks.PC_COOKING_TABLE, BlockyChefBlocks.PP_COOKING_TABLE).build(null));
        helper.register("kitchen_counter_corner", BlockEntityType.Builder.of(KitchenCounterCornerBlockEntity::new, BlockyChefBlocks.CC_KITCHEN_COUNTER_CORNER, BlockyChefBlocks.CP_KITCHEN_COUNTER_CORNER, BlockyChefBlocks.PC_KITCHEN_COUNTER_CORNER, BlockyChefBlocks.PP_KITCHEN_COUNTER_CORNER).build(null));
        helper.register("stove", BlockEntityType.Builder.of(StoveBlockEntity::new, BlockyChefBlocks.STOVE).build(null));
        helper.register("dough_maker", BlockEntityType.Builder.of(DoughMakerBlockEntity::new, BlockyChefBlocks.DOUGH_MAKER).build(null));
        helper.register("mixer", BlockEntityType.Builder.of(MixerBlockEntity::new, BlockyChefBlocks.MIXER).build(null));
        helper.register("toaster", BlockEntityType.Builder.of(ToasterBlockEntity::new, BlockyChefBlocks.TOASTER).build(null));
        helper.register("juicer", BlockEntityType.Builder.of(JuicerBlockEntity::new, BlockyChefBlocks.JUICER).build(null));
        helper.register("grill", BlockEntityType.Builder.of(GrillBlockEntity::new, BlockyChefBlocks.GRILL).build(null));
        helper.register("pasta_machine", BlockEntityType.Builder.of(PastaMachineBlockEntity::new, BlockyChefBlocks.PASTA_MACHINE).build(null));
        helper.register("grater", BlockEntityType.Builder.of(GraterBlockEntity::new, BlockyChefBlocks.GRATER).build(null));
        helper.register("cutting_board", BlockEntityType.Builder.of(CuttingBoardBlockEntity::new, BlockyChefBlocks.OAK_CUTTING_BOARD, BlockyChefBlocks.SPRUCE_CUTTING_BOARD, BlockyChefBlocks.BIRCH_CUTTING_BOARD, BlockyChefBlocks.JUNGLE_CUTTING_BOARD, BlockyChefBlocks.ACACIA_CUTTING_BOARD, BlockyChefBlocks.DARK_CUTTING_BOARD, BlockyChefBlocks.MANGROVE_CUTTING_BOARD, BlockyChefBlocks.CRIMSON_CUTTING_BOARD, BlockyChefBlocks.WARPED_CUTTING_BOARD).build(null));
    }

    private static void registerMenuTypes(RegisterEvent.RegisterHelper<MenuType<?>> helper) {
        helper.register("cutting_board", IForgeMenuType.create(CuttingBoardMenu::new));
    }

    private static void registerMobEffects(RegisterEvent.RegisterHelper<MobEffect> helper) {
        helper.register("thirst", new ThirstMobEffect(MobEffectCategory.HARMFUL, 0x97AF5D));
        helper.register("hydration", new HydrationMobEffect(MobEffectCategory.BENEFICIAL, 0x3080E8));
    }

    private static void registerRecipeTypes(RecipeTypeRegistryHelper helper) {
        helper.register("drying_recipe");
        helper.register("grating_recipe");
        helper.register("cutting_board_recipe");
    }

    private static void registerRecipeSerializers(RegisterEvent.RegisterHelper<RecipeSerializer<?>> helper) {
        helper.register("drying", CodecRecipeSerializer.forCodec(DryingRecipe.CODEC_PROVIDER));
        helper.register("grating", CodecRecipeSerializer.forCodec(GratingRecipe.CODEC_PROVIDER));
        helper.register("cutting_board", CodecRecipeSerializer.forCodec(CuttingBoardRecipe.CODEC_PROVIDER));
    }

    private static void registerFeatures(RegisterEvent.RegisterHelper<Feature<?>> helper) {
        helper.register("weighted_selector", new WeightedSelectorFeature(WeightedFeatureConfiguration.CODEC));
    }

    private static void registerTreeDecorators(RegisterEvent.RegisterHelper<TreeDecoratorType<?>> helper) {
        helper.register("fruit_decorator", new TreeDecoratorType<>(TreeFruitDecorator.CODEC));
    }

    private static boolean allowParrotOrOcelotSpawn(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> entityType) {
        return entityType == EntityType.OCELOT || entityType == EntityType.PARROT;
    }

    private static boolean alwaysFalse(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
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
