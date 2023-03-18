package tnt.blockychef.common.init;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ObjectHolder;
import tnt.blockychef.common.food.recipe.CuttingBoardRecipe;
import tnt.blockychef.common.food.recipe.DryingRecipe;
import tnt.blockychef.common.food.recipe.GratingRecipe;

public final class BlockyChefRecipeTypes {

    @ObjectHolder(value = "blockychef:drying_recipe", registryName = "recipe_type")
    public static final RecipeType<DryingRecipe> DRYING_RECIPE = null;
    @ObjectHolder(value = "blockychef:grating_recipe", registryName = "recipe_type")
    public static final RecipeType<GratingRecipe> GRATING_RECIPE = null;
    @ObjectHolder(value = "blockychef:cutting_board_recipe", registryName = "recipe_type")
    public static final RecipeType<CuttingBoardRecipe> CUTTING_BOARD_RECIPE = null;
}
