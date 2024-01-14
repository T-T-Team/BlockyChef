package tnt.blockychef.integrations.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.data.fluids.FluidExtraction;
import tnt.blockychef.common.food.recipe.*;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.menu.ToasterMenu;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

@JeiPlugin
public class JeiIntegrationPlugin implements IModPlugin {

    public static final ResourceLocation PLUGIN_ID = new ResourceLocation(BlockyChef.MODID, "jei_integration");

    static final RecipeType<DryingRecipe> DRYING_RECIPE = new RecipeType<>(BlockyChef.resource("drying"), DryingRecipe.class);
    static final RecipeType<CuttingBoardRecipe> CUTTING_BOARD_RECIPE = new RecipeType<>(BlockyChef.resource("cutting_board"), CuttingBoardRecipe.class);
    static final RecipeType<ToasterRecipe> TOASTER_RECIPE = new RecipeType<>(BlockyChef.resource("toaster"), ToasterRecipe.class);
    static final RecipeType<MixingBowlRecipe> MIXING_BOWL = new RecipeType<>(BlockyChef.resource("mixing_bowl"), MixingBowlRecipe.class);
    static final RecipeType<MortarRecipe> MORTAR_AND_PESTLE = new RecipeType<>(BlockyChef.resource("mortar_and_pestle"), MortarRecipe.class);
    static final RecipeType<BarrelRecipe> BARREL = new RecipeType<>(BlockyChef.resource("barrel"), BarrelRecipe.class);
    static final RecipeType<DoughMakerRecipe> DOUGH_MAKER = new RecipeType<>(BlockyChef.resource("dough_maker"), DoughMakerRecipe.class);
    static final RecipeType<PastaMachineRecipe> PASTA_MACHINE = new RecipeType<>(BlockyChef.resource("pasta_machine"), PastaMachineRecipe.class);
    static final RecipeType<MeatGrinderRecipe> MEAT_GRINDER = new RecipeType<>(BlockyChef.resource("meat_grinder"), MeatGrinderRecipe.class);
    static final RecipeType<GratingRecipe> GRATER = new RecipeType<>(BlockyChef.resource("grater"), GratingRecipe.class);
    static final RecipeType<JuicerRecipe> JUICER = new RecipeType<>(BlockyChef.resource("juicer"), JuicerRecipe.class);
    static final RecipeType<MixerRecipe> MIXER = new RecipeType<>(BlockyChef.resource("mixer"), MixerRecipe.class);
    static final RecipeType<FluidExtraction> FLUID_EXTRACTION = new RecipeType<>(BlockyChef.resource("fluid_extraction"), FluidExtraction.class);
    static final RecipeType<StoveRecipe> STOVE = new RecipeType<>(BlockyChef.resource("stove"), StoveRecipe.class);
    static final RecipeType<PanRecipe> PAN = new RecipeType<>(BlockyChef.resource("pan"), PanRecipe.class);
    static final RecipeType<PotRecipe> POT = new RecipeType<>(BlockyChef.resource("pot"), PotRecipe.class);

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(DRYING_RECIPE, getRecipes(BlockyChefRecipeTypes.DRYING_RECIPE));
        registration.addRecipes(CUTTING_BOARD_RECIPE, getRecipes(BlockyChefRecipeTypes.CUTTING_BOARD_RECIPE));
        registration.addRecipes(TOASTER_RECIPE, getRecipes(BlockyChefRecipeTypes.TOASTER_RECIPE));
        registration.addRecipes(MIXING_BOWL, getRecipes(BlockyChefRecipeTypes.MIXING_BOWL_RECIPE));
        registration.addRecipes(MORTAR_AND_PESTLE, getRecipes(BlockyChefRecipeTypes.MORTAR_AND_PESTLE_RECIPE));
        registration.addRecipes(BARREL, getRecipes(BlockyChefRecipeTypes.BARREL_RECIPE));
        registration.addRecipes(DOUGH_MAKER, getRecipes(BlockyChefRecipeTypes.DOUGH_MAKER_RECIPE));
        registration.addRecipes(PASTA_MACHINE, getRecipes(BlockyChefRecipeTypes.PASTA_MACHINE_RECIPE));
        registration.addRecipes(MEAT_GRINDER, getRecipes(BlockyChefRecipeTypes.MEAT_GRINDER_RECIPE));
        registration.addRecipes(GRATER, getRecipes(BlockyChefRecipeTypes.GRATING_RECIPE));
        registration.addRecipes(JUICER, getRecipes(BlockyChefRecipeTypes.JUICER_RECIPE));
        registration.addRecipes(MIXER, getRecipes(BlockyChefRecipeTypes.MIXER_RECIPE));
        registration.addRecipes(FLUID_EXTRACTION, BlockyChef.EXTRACTION_MANAGER.getLoadedExtractionRecipes());
        registration.addRecipes(STOVE, getRecipes(BlockyChefRecipeTypes.STOVE_RECIPE));
        registration.addRecipes(PAN, getRecipes(BlockyChefRecipeTypes.PAN_RECIPE));
        registration.addRecipes(POT, getRecipes(BlockyChefRecipeTypes.POT_RECIPE));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new DryingRecipeCategory(helper),
                new CuttingBoardRecipeCategory(helper),
                new ToastingRecipeCategory(helper),
                new MixingBowlRecipeCategory(helper),
                new MortarRecipeCategory(helper),
                new BarrelRecipeCategory(helper),
                new DoughMakerRecipeCategory(helper),
                new PastaMachineRecipeCategory(helper),
                new MeatGrinderRecipeCategory(helper),
                new GraterRecipeCategory(helper),
                new JuicerRecipeCategory(helper),
                new MixerRecipeCategory(helper),
                new FluidExtractionRecipeCategory(helper),
                new StoveRecipeCategory(helper),
                new PanRecipeCategory(helper),
                new PotRecipeCategory(helper)
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.DRYING_RACK), DRYING_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.OAK_CUTTING_BOARD), CUTTING_BOARD_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.SPRUCE_CUTTING_BOARD), CUTTING_BOARD_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.BIRCH_CUTTING_BOARD), CUTTING_BOARD_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.JUNGLE_CUTTING_BOARD), CUTTING_BOARD_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.DARK_CUTTING_BOARD), CUTTING_BOARD_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.CRIMSON_CUTTING_BOARD), CUTTING_BOARD_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.WARPED_CUTTING_BOARD), CUTTING_BOARD_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.ACACIA_CUTTING_BOARD), CUTTING_BOARD_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.MANGROVE_CUTTING_BOARD), CUTTING_BOARD_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.TOASTER), TOASTER_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.OAK_MIXING_BOWL), MIXING_BOWL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.BIRCH_MIXING_BOWL), MIXING_BOWL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.DARK_OAK_MIXING_BOWL), MIXING_BOWL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.JUNGLE_MIXING_BOWL), MIXING_BOWL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.SPRUCE_MIXING_BOWL), MIXING_BOWL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.MANGROVE_MIXING_BOWL), MIXING_BOWL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.ACACIA_MIXING_BOWL), MIXING_BOWL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.WARPED_MIXING_BOWL), MIXING_BOWL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.CRIMSON_MIXING_BOWL), MIXING_BOWL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.DEEPSLATE_MORTAR_AND_PESTLE), MORTAR_AND_PESTLE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.ANDESITE_MORTAR_AND_PESTLE), MORTAR_AND_PESTLE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.DIORITE_MORTAR_AND_PESTLE), MORTAR_AND_PESTLE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.GRANITE_MORTAR_AND_PESTLE), MORTAR_AND_PESTLE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.QUARTZ_MORTAR_AND_PESTLE), MORTAR_AND_PESTLE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.OAK_BARREL), BARREL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.BIRCH_BARREL), BARREL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.SPRUCE_BARREL), BARREL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.JUNGLE_BARREL), BARREL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.DARK_OAK_BARREL), BARREL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.ACACIA_BARREL), BARREL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.MANGROVE_BARREL), BARREL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.WARPED_BARREL), BARREL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.CRIMSON_BARREL), BARREL);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.DOUGH_MAKER), DOUGH_MAKER);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.PASTA_MACHINE), PASTA_MACHINE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.MEAT_GRINDER), MEAT_GRINDER);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.GRATER), GRATER);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.JUICER), JUICER);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.MIXER), MIXER);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.JUICER), FLUID_EXTRACTION);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.MIXER), FLUID_EXTRACTION);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.STOVE), STOVE);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.PAN), PAN);
        registration.addRecipeCatalyst(new ItemStack(BlockyChefBlocks.POT), POT);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(ToasterMenu.class, BlockyChefMenuTypes.TOASTER, TOASTER_RECIPE, 0, 2, 2, 36);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    private static <I extends Container, R extends Recipe<I>> List<R> getRecipes(net.minecraft.world.item.crafting.RecipeType<R> type) {
        return getRecipes(type, recipe -> !(recipe instanceof BurnableRecipe burnable) || !burnable.isBurning());
    }

    private static <I extends Container, R extends Recipe<I>> List<R> getRecipes(net.minecraft.world.item.crafting.RecipeType<R> type, @Nullable Predicate<R> filter) {
        Level level = Minecraft.getInstance().level;
        RecipeManager manager = level.getRecipeManager();
        List<RecipeHolder<R>> list = manager.getAllRecipesFor(type);
        if (filter == null) {
            return list.stream().map(RecipeHolder::value).toList();
        }
        return list.stream().map(RecipeHolder::value).filter(filter).toList();
    }
}
