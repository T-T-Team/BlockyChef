package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Collections;
import java.util.List;

public abstract class AbstractFoodRecipe<C extends Container> implements Recipe<C> {

    private final float experience;
    private final List<MultiIngredient> outputConsumers;

    protected AbstractFoodRecipe(List<MultiIngredient> outputConsumers, float experience) throws JsonParseException {
        this.outputConsumers = outputConsumers;
        this.experience = experience;
        if (experience < 0.0F) {
            throwValidationError("Experience cannot be lower than 0");
        }
    }

    public static <R extends AbstractFoodRecipe<?>> RecordCodecBuilder<R, Float> resolveExperience() {
        return Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(AbstractFoodRecipe::getExperience);
    }

    public static <R extends AbstractFoodRecipe<?>> RecordCodecBuilder<R, List<MultiIngredient>> resolveRemainderConsumer() {
        return MultiIngredient.CODEC.listOf().optionalFieldOf("craftRemainderConsumers", Collections.emptyList()).forGetter(AbstractFoodRecipe::getOutputConsumers);
    }

    public float getExperience() {
        return experience;
    }

    public List<MultiIngredient> getOutputConsumers() {
        return outputConsumers;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return true;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    void throwValidationError(String message) {
        throw new JsonSyntaxException(String.format("Error in recipe [%s]: %s", this.getType(), message));
    }
}
