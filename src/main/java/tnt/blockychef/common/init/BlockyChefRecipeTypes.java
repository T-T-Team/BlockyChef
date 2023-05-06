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
}
