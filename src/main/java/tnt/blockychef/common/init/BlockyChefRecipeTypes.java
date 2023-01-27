package tnt.blockychef.common.init;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ObjectHolder;
import tnt.blockychef.common.food.recipe.DryingRecipe;

public final class BlockyChefRecipeTypes {

    @ObjectHolder(value = "blockychef:drying_recipe", registryName = "recipe_type")
    public static final RecipeType<DryingRecipe> DRYING_RECIPE = null;
}
