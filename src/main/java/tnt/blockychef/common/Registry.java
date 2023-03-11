package tnt.blockychef.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.level.material.Material;
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
import tnt.blockychef.common.food.recipe.DryingRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefItems;
import tnt.blockychef.common.item.CropSeedsItem;
import tnt.blockychef.common.item.DrinkableItem;
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
        helper.register("eggplant_crops", new CropsBlock(() -> BlockyChefItems.EGGPLANT), false);
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
