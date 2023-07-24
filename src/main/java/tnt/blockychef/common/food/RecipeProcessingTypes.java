package tnt.blockychef.common.food;

import net.minecraft.resources.ResourceLocation;
import tnt.blockychef.BlockyChef;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RecipeProcessingTypes {

    private static final Map<ResourceLocation, RecipeProcessingType> TYPE_MAP = new HashMap<>();

    public static final RecipeProcessingType CUTTING = register("cutting");
    public static final RecipeProcessingType SLICING = register("slicing");
    public static final RecipeProcessingType PEELING = register("peeling");
    public static final RecipeProcessingType HOLLOWING = register("hollowing");
    public static final RecipeProcessingType ROLLING = register("rolling");

    public static RecipeProcessingType registerRecipeProcessingType(ResourceLocation location) {
        RecipeProcessingType type = new RecipeProcessingType(location);
        TYPE_MAP.put(location, type);
        return type;
    }

    public static Optional<RecipeProcessingType> getById(ResourceLocation id) {
        return Optional.ofNullable(TYPE_MAP.get(id));
    }

    private static RecipeProcessingType register(String name) {
        return registerRecipeProcessingType(new ResourceLocation(BlockyChef.MODID, name));
    }
}
