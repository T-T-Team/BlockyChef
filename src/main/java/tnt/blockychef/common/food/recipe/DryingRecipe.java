package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

public class DryingRecipe extends AbstractFoodRecipe<DryingRackBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<DryingRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.INGREDIENT_CODEC.fieldOf("input").forGetter(t -> t.input),
            SerializationHelper.SIMPLE_ITEMSTACK_CODEC.fieldOf("output").forGetter(t -> t.output),
            Codec.INT.fieldOf("dryingTime").forGetter(DryingRecipe::getDryingTime),
            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(AbstractFoodRecipe::getExperience)
    ).apply(instance, (ingredient, stack, time, exp) -> new DryingRecipe(recipeId, ingredient, stack, time, exp)));

    private final Ingredient input;
    private final ItemStack output;
    private final int dryingTime;

    private DryingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int dryingTime, float experience) throws JsonParseException {
        super(id, experience);
        this.input = input;
        this.output = output;
        this.dryingTime = dryingTime;
        if (dryingTime < 20) {
            throwValidationError("Drying time cannot be lower than 20");
        }
    }

    @Override
    public boolean matches(DryingRackBlockEntity container, Level level) {
        ItemStack stack = container.getItem(0);
        return isValidInput(stack);
    }

    public boolean isValidInput(ItemStack stack) {
        return input.test(stack);
    }

    @Override
    public ItemStack assemble(DryingRackBlockEntity container, RegistryAccess access) {
        return getResultItem(access).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.DRYING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.DRYING_RECIPE;
    }

    public int getDryingTime() {
        return dryingTime;
    }
}
