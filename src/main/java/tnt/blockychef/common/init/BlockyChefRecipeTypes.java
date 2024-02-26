package tnt.blockychef.common.init;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ObjectHolder;
import tnt.blockychef.common.food.recipe.*;

public final class BlockyChefRecipeTypes {

    private static final String KEY = "recipe_type";

    @ObjectHolder(value = "blockychef:drying_recipe", registryName = KEY)
    public static final RecipeType<DryingRecipe> DRYING_RECIPE = null;
    @ObjectHolder(value = "blockychef:grating_recipe", registryName = KEY)
    public static final RecipeType<GratingRecipe> GRATING_RECIPE = null;
    @ObjectHolder(value = "blockychef:cutting_board_recipe", registryName = KEY)
    public static final RecipeType<CuttingBoardRecipe> CUTTING_BOARD_RECIPE = null;
    @ObjectHolder(value = "blockychef:toasting_recipe", registryName = KEY)
    public static final RecipeType<ToasterRecipe> TOASTER_RECIPE = null;
    @ObjectHolder(value = "blockychef:meat_grinder_recipe", registryName = KEY)
    public static final RecipeType<MeatGrinderRecipe> MEAT_GRINDER_RECIPE = null;
    @ObjectHolder(value = "blockychef:mortar_and_pestle_recipe", registryName = KEY)
    public static final RecipeType<MortarRecipe> MORTAR_AND_PESTLE_RECIPE = null;
    @ObjectHolder(value = "blockychef:mixing_bowl_recipe", registryName = KEY)
    public static final RecipeType<MixingBowlRecipe> MIXING_BOWL_RECIPE = null;
    @ObjectHolder(value = "blockychef:dough_maker_recipe", registryName = KEY)
    public static final RecipeType<DoughMakerRecipe> DOUGH_MAKER_RECIPE = null;
    @ObjectHolder(value = "blockychef:pasta_machine_recipe", registryName = KEY)
    public static final RecipeType<PastaMachineRecipe> PASTA_MACHINE_RECIPE = null;
    @ObjectHolder(value = "blockychef:barrel_recipe", registryName = KEY)
    public static final RecipeType<BarrelRecipe> BARREL_RECIPE = null;
    @ObjectHolder(value = "blockychef:juicer_recipe", registryName = KEY)
    public static final RecipeType<JuicerRecipe> JUICER_RECIPE = null;
    @ObjectHolder(value = "blockychef:mixer_recipe", registryName = KEY)
    public static final RecipeType<MixerRecipe> MIXER_RECIPE = null;
    @ObjectHolder(value = "blockychef:stove_recipe", registryName = KEY)
    public static final RecipeType<StoveRecipe> STOVE_RECIPE = null;
    @ObjectHolder(value = "blockychef:pan_recipe", registryName = KEY)
    public static final RecipeType<PanRecipe> PAN_RECIPE = null;
    @ObjectHolder(value = "blockychef:pot_recipe", registryName = KEY)
    public static final RecipeType<PotRecipe> POT_RECIPE = null;
    @ObjectHolder(value = "blockychef:teapot_recipe", registryName = KEY)
    public static final RecipeType<TeapotRecipe> TEAPOT_RECIPE = null;
    @ObjectHolder(value = "blockychef:cooking_table_recipe", registryName = KEY)
    public static final RecipeType<CookingTableRecipe> COOKING_TABLE_RECIPE = null;
}
