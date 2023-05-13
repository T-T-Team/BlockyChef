package tnt.blockychef.common.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ObjectHolder;

public final class BlockyChefRecipeSerializers {

    private static final String KEY = "recipe_serializer";

    @ObjectHolder(value = "blockychef:drying", registryName = KEY)
    public static final RecipeSerializer<?> DRYING_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:grating", registryName = KEY)
    public static final RecipeSerializer<?> GRATING_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:cutting_board", registryName = KEY)
    public static final RecipeSerializer<?> CUTTING_BOARD_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:toasting", registryName = KEY)
    public static final RecipeSerializer<?> TOASTING_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:meat_grinding", registryName = KEY)
    public static final RecipeSerializer<?> MEAT_GRINDER_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:grinding", registryName = KEY)
    public static final RecipeSerializer<?> MORTAR_AND_PESTLE_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:mixing_bowl", registryName = KEY)
    public static final RecipeSerializer<?> MIXING_BOWL_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:dough_maker", registryName = KEY)
    public static final RecipeSerializer<?> DOUGH_MAKER_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:pasta_machine", registryName = KEY)
    public static final RecipeSerializer<?> PASTA_MACHINE_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:barrel", registryName = KEY)
    public static final RecipeSerializer<?> BARREL_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:juicing", registryName = KEY)
    public static final RecipeSerializer<?> JUICER_RECIPE_SERIALIZER = null;
    @ObjectHolder(value = "blockychef:mixer", registryName = KEY)
    public static final RecipeSerializer<?> MIXER_RECIPE_SERIALIZER = null;
}
