package tnt.blockychef.common.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ObjectHolder;

public final class BlockyChefRecipeSerializers {

    @ObjectHolder(value = "blockychef:drying", registryName = "recipe_serializer")
    public static final RecipeSerializer<?> DRYING_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:grating", registryName = "recipe_serializer")
    public static final RecipeSerializer<?> GRATING_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:cutting_board", registryName = "recipe_serializer")
    public static final RecipeSerializer<?> CUTTING_BOARD_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:toasting", registryName = "recipe_serializer")
    public static final RecipeSerializer<?> TOASTING_RECIPE_SERIALIZER = null;
}
