package tnt.blockychef.common.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.*;
import tnt.blockychef.common.effect.HydrationMobEffect;
import tnt.blockychef.common.effect.ThirstMobEffect;
import tnt.blockychef.common.food.recipe.*;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefVanillaExtensions;
import tnt.blockychef.common.menu.*;
import tnt.blockychef.levelgen.feature.WeightedFeatureConfiguration;
import tnt.blockychef.levelgen.feature.WeightedSelectorFeature;
import tnt.blockychef.levelgen.tree.TreeFruitDecorator;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BlockyChef.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Registry {

    private static List<Block> blockEntries = new ArrayList<>();

    @SubscribeEvent
    public static void registerObjects(RegisterEvent event) {
        event.register(ForgeRegistries.BLOCKS.getRegistryKey(), Registry::registerBlocks);
        event.register(ForgeRegistries.ITEMS.getRegistryKey(), Registry::registerItems);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), Registry::registerBlockEntities);
        event.register(ForgeRegistries.MENU_TYPES.getRegistryKey(), Registry::registerMenuTypes);
        event.register(ForgeRegistries.MOB_EFFECTS.getRegistryKey(), Registry::registerMobEffects);
        event.register(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), helper -> {
            RegistrationHelper typeHelper = id -> helper.register(id, new RecipeType<>() {
                @Override
                public String toString() {
                    return BlockyChef.MODID + ":" + id;
                }
            });
            registerRecipeTypes(typeHelper);
        });
        event.register(ForgeRegistries.RECIPE_SERIALIZERS.getRegistryKey(), Registry::registerRecipeSerializers);
        event.register(ForgeRegistries.FEATURES.getRegistryKey(), Registry::registerFeatures);
        event.register(ForgeRegistries.TREE_DECORATOR_TYPES.getRegistryKey(), Registry::registerTreeDecorators);
        event.register(ForgeRegistries.SOUND_EVENTS.getRegistryKey(), helper -> {
            RegistrationHelper soundHelper = id -> helper.register(id, SoundEvent.createVariableRangeEvent(BlockyChef.resource(id)));
            registerSoundEvents(soundHelper);
        });
        registerVanilla();
    }

    private static void registerBlocks(RegisterEvent.RegisterHelper<Block> helper) {
        BlockRegistryHelper blockHelper = (name, block, createItem) -> {
            helper.register(name, block);
            if (createItem) {
                blockEntries.add(block);
            }
        };
        BlockRegistry.register(blockHelper);
    }

    private static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        ItemRegistry.register(helper);
        for (Block block : blockEntries) {
            BlockItem blockItem = new BlockItem(block, new Item.Properties());
            helper.register(ForgeRegistries.BLOCKS.getKey(block), blockItem);
        }
        blockEntries = null;
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
        helper.register("meat_grinder", BlockEntityType.Builder.of(MeatGrinderBlockEntity::new, BlockyChefBlocks.MEAT_GRINDER).build(null));
        helper.register("mortar_and_pestle", BlockEntityType.Builder.of(MortarAndPestleBlockEntity::new, BlockyChefBlocks.GRANITE_MORTAR_AND_PESTLE, BlockyChefBlocks.ANDESITE_MORTAR_AND_PESTLE, BlockyChefBlocks.DIORITE_MORTAR_AND_PESTLE, BlockyChefBlocks.QUARTZ_MORTAR_AND_PESTLE, BlockyChefBlocks.DEEPSLATE_MORTAR_AND_PESTLE).build(null));
        helper.register("mixing_bowl", BlockEntityType.Builder.of(MixingBowlBlockEntity::new, BlockyChefBlocks.OAK_MIXING_BOWL, BlockyChefBlocks.SPRUCE_MIXING_BOWL, BlockyChefBlocks.BIRCH_MIXING_BOWL, BlockyChefBlocks.JUNGLE_MIXING_BOWL, BlockyChefBlocks.ACACIA_MIXING_BOWL, BlockyChefBlocks.DARK_OAK_MIXING_BOWL, BlockyChefBlocks.MANGROVE_MIXING_BOWL, BlockyChefBlocks.CRIMSON_MIXING_BOWL, BlockyChefBlocks.WARPED_MIXING_BOWL).build(null));
        helper.register("barrel", BlockEntityType.Builder.of(BarrelBlockEntity::new, BlockyChefBlocks.OAK_BARREL, BlockyChefBlocks.SPRUCE_BARREL, BlockyChefBlocks.BIRCH_BARREL, BlockyChefBlocks.JUNGLE_BARREL, BlockyChefBlocks.ACACIA_BARREL, BlockyChefBlocks.DARK_OAK_BARREL, BlockyChefBlocks.MANGROVE_BARREL, BlockyChefBlocks.CRIMSON_BARREL, BlockyChefBlocks.WARPED_BARREL).build(null));
        helper.register("pan", BlockEntityType.Builder.of(PanBlockEntity::new, BlockyChefBlocks.PAN).build(null));
        helper.register("pot", BlockEntityType.Builder.of(PotBlockEntity::new, BlockyChefBlocks.POT).build(null));
        helper.register("teapot", BlockEntityType.Builder.of(TeapotBlockEntity::new, BlockyChefBlocks.TEAPOT).build(null));
    }

    private static void registerMenuTypes(RegisterEvent.RegisterHelper<MenuType<?>> helper) {
        helper.register("cutting_board", IForgeMenuType.create(CuttingBoardMenu::new));
        helper.register("toaster", IForgeMenuType.create(ToasterMenu::new));
        helper.register("mortar_and_pestle", IForgeMenuType.create(MortarAndPestleMenu::new));
        helper.register("mixing_bowl", IForgeMenuType.create(MixingBowlMenu::new));
        helper.register("dough_maker", IForgeMenuType.create(DoughMakerMenu::new));
        helper.register("pasta_machine", IForgeMenuType.create(PastaMachineMenu::new));
        helper.register("barrel", IForgeMenuType.create(BarrelMenu::new));
        helper.register("mixer", IForgeMenuType.create(MixerMenu::new));
        helper.register("stove", IForgeMenuType.create(StoveMenu::new));
        helper.register("pan", IForgeMenuType.create(PanMenu::new));
        helper.register("pot", IForgeMenuType.create(PotMenu::new));
        helper.register("teapot", IForgeMenuType.create(TeapotMenu::new));
        helper.register("kitchen_counter", IForgeMenuType.create(KitchenCounterMenu::new));
        helper.register("kitchen_cabinet", IForgeMenuType.create(KitchenCabinetMenu::new));
        helper.register("cooking_table", IForgeMenuType.create(CookingTableMenu::new));
    }

    private static void registerMobEffects(RegisterEvent.RegisterHelper<MobEffect> helper) {
        helper.register("thirst", new ThirstMobEffect(MobEffectCategory.HARMFUL, 0x97AF5D));
        helper.register("hydration", new HydrationMobEffect(MobEffectCategory.BENEFICIAL, 0x3080E8));
    }

    private static void registerRecipeTypes(RegistrationHelper helper) {
        helper.register("drying_recipe");
        helper.register("grating_recipe");
        helper.register("cutting_board_recipe");
        helper.register("toasting_recipe");
        helper.register("meat_grinder_recipe");
        helper.register("mortar_and_pestle_recipe");
        helper.register("mixing_bowl_recipe");
        helper.register("dough_maker_recipe");
        helper.register("pasta_machine_recipe");
        helper.register("barrel_recipe");
        helper.register("juicer_recipe");
        helper.register("mixer_recipe");
        helper.register("stove_recipe");
        helper.register("pan_recipe");
        helper.register("pot_recipe");
        helper.register("teapot_recipe");
        helper.register("cooking_table_recipe");
    }

    private static void registerRecipeSerializers(RegisterEvent.RegisterHelper<RecipeSerializer<?>> helper) {
        helper.register("drying", CodecRecipeSerializer.forCodec(DryingRecipe.CODEC_PROVIDER));
        helper.register("grating", CodecRecipeSerializer.forCodec(GratingRecipe.CODEC_PROVIDER));
        helper.register("cutting_board", CodecRecipeSerializer.forCodec(CuttingBoardRecipe.CODEC_PROVIDER));
        helper.register("toasting", CodecRecipeSerializer.forCodec(ToasterRecipe.CODEC));
        helper.register("meat_grinding", CodecRecipeSerializer.forCodec(MeatGrinderRecipe.CODEC_PROVIDER));
        helper.register("grinding", CodecRecipeSerializer.forCodec(MortarRecipe.CODEC));
        helper.register("mixing_bowl", CodecRecipeSerializer.forCodec(MixingBowlRecipe.CODEC));
        helper.register("dough_maker", CodecRecipeSerializer.forCodec(DoughMakerRecipe.CODEC_PROVIDER));
        helper.register("pasta_machine", CodecRecipeSerializer.forCodec(PastaMachineRecipe.CODEC));
        helper.register("barrel", CodecRecipeSerializer.forCodec(BarrelRecipe.CODEC_PROVIDER));
        helper.register("juicing", CodecRecipeSerializer.forCodec(JuicerRecipe.CODEC_PROVIDER));
        helper.register("mixer", CodecRecipeSerializer.forCodec(MixerRecipe.CODEC));
        helper.register("stove", CodecRecipeSerializer.forCodec(StoveRecipe.CODEC));
        helper.register("pan", CodecRecipeSerializer.forCodec(PanRecipe.CODEC));
        helper.register("pot", CodecRecipeSerializer.forCodec(PotRecipe.CODEC));
        helper.register("teapot", CodecRecipeSerializer.forCodec(TeapotRecipe.CODEC));
        helper.register("cooking_table", CodecRecipeSerializer.forCodec(CookingTableRecipe.CODEC));
    }

    private static void registerFeatures(RegisterEvent.RegisterHelper<Feature<?>> helper) {
        helper.register("weighted_selector", new WeightedSelectorFeature(WeightedFeatureConfiguration.CODEC));
    }

    private static void registerTreeDecorators(RegisterEvent.RegisterHelper<TreeDecoratorType<?>> helper) {
        helper.register("fruit_decorator", new TreeDecoratorType<>(TreeFruitDecorator.CODEC));
    }

    private static void registerSoundEvents(RegistrationHelper helper) {
        helper.register("cutting_board");
        helper.register("dough_maker");
        helper.register("grater_a");
        helper.register("grater_b");
        helper.register("grill");
        helper.register("juicer");
        helper.register("meat_grinder");
        helper.register("mixer");
        helper.register("mixing_bowl");
        helper.register("mortar_and_pestle");
        helper.register("oven");
        helper.register("pan");
        helper.register("pasta_machine");
        helper.register("pot");
        helper.register("saucepan");
        helper.register("teapot");
        helper.register("toaster_a");
        helper.register("toaster_b");
    }

    private static void registerVanilla() {
        registerEntry(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, "item_group", BlockyChefVanillaExtensions.LOOT_GROUP_ITEMS);
    }

    private static <T> void registerEntry(net.minecraft.core.Registry<T> registry, String id, T entry) {
        net.minecraft.core.Registry.register(registry, BlockyChef.resource(id), entry);
    }

    @FunctionalInterface
    private interface RegistrationHelper {
        void register(String id);
    }
}
