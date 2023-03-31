package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractFoodRecipe<C extends Container> implements Recipe<C> {

    private final ResourceLocation id;
    private final float experience;

    protected AbstractFoodRecipe(ResourceLocation id, float experience) throws JsonParseException {
        this.id = id;
        this.experience = experience;
        if (experience < 0.0F) {
            throwValidationError("Experience cannot be lower than 0");
        }
    }

    public float getExperience() {
        return experience;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return true;
    }

    void throwValidationError(String message) {
        throw new JsonSyntaxException(String.format("Error in recipe [%s, %s]: %s", this.getType(), this.getId(), message));
    }

    @Override
    public String toString() {
        return getId().toString();
    }
}
