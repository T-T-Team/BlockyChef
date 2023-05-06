package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.PastaMachineBlockEntity;
import tnt.blockychef.common.food.RecipeProcessingType;
import tnt.blockychef.common.food.RecipeProcessingTypes;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

import java.util.Arrays;

public class PastaMachineRecipe extends AbstractFoodRecipe<PastaMachineBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<PastaMachineRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.INGREDIENT_CODEC.fieldOf("input").forGetter(t -> t.input),
            ResourceLocation.CODEC.comapFlatMap(
                    location -> RecipeProcessingTypes.getById(location).map(DataResult::success).orElse(DataResult.error(() -> "Unknown recipe processing type '" + location + "'")),
                    RecipeProcessingType::getLocation
            ).fieldOf("processingType").forGetter(PastaMachineRecipe::getProcessingType),
            Codec.INT.optionalFieldOf("processingTime", 100).forGetter(PastaMachineRecipe::getProcessingTime),
            SerializationHelper.SIMPLE_ITEMSTACK_CODEC.listOf().xmap(
                    list -> list.toArray(ItemStack[]::new),
                    Arrays::asList
            ).fieldOf("outputs").forGetter(PastaMachineRecipe::getOutputs),
            resolveExperience()
    ).apply(instance, (input, type, time, outputs, exp) -> new PastaMachineRecipe(recipeId, input, type, outputs, time, exp)));

    private final Ingredient input;
    private final RecipeProcessingType processingType;
    private final ItemStack[] outputs;
    private final int processingTime;

    public PastaMachineRecipe(ResourceLocation recipeId, Ingredient input, RecipeProcessingType processingType, ItemStack[] outputs, int processingTime, float experience) {
        super(recipeId, experience);
        this.input = input;
        this.processingType = processingType;
        this.outputs = outputs;
        this.processingTime = processingTime;

        if (processingTime < 1) {
            throwValidationError("Processing time cannot be lower than 1");
        }
        if (outputs.length > PastaMachineBlockEntity.OUTPUTS.length) {
            throwValidationError(String.format("Output amount of %d exceeds maximum output amount of %d", outputs.length, PastaMachineBlockEntity.OUTPUTS.length));
        }
    }

    public ItemStack[] getOutputs() {
        return outputs;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public RecipeProcessingType getProcessingType() {
        return processingType;
    }

    @Override
    public boolean matches(PastaMachineBlockEntity pastaMachine, Level level) {
        return input.test(pastaMachine.getInputItem());
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return outputs[0];
    }

    @Override
    public ItemStack assemble(PastaMachineBlockEntity pastaMachine, RegistryAccess access) {
        return getResultItem(access).copy();
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.PASTA_MACHINE_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.PASTA_MACHINE_RECIPE_SERIALIZER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PastaMachineRecipe recipe = (PastaMachineRecipe) o;
        return getId().equals(recipe.getId()) && processingType.equals(recipe.processingType);
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + processingType.hashCode();
        return result;
    }
}
