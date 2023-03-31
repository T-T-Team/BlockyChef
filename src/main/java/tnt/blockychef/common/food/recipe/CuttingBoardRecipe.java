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
import tnt.blockychef.common.block.entity.CuttingBoardBlockEntity;
import tnt.blockychef.common.food.RecipeProcessingType;
import tnt.blockychef.common.food.RecipeProcessingTypes;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

import java.util.Arrays;

public class CuttingBoardRecipe extends AbstractFoodRecipe<CuttingBoardBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<CuttingBoardRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.INGREDIENT_CODEC.fieldOf("input").forGetter(t -> t.input),
            ResourceLocation.CODEC.comapFlatMap(
                    location -> RecipeProcessingTypes.getById(location).map(DataResult::success).orElse(DataResult.error(() -> "Unknown recipe processing type '" + location + "'")),
                    RecipeProcessingType::getLocation
            ).fieldOf("processingType").forGetter(CuttingBoardRecipe::getProcessingType),
            Codec.INT.optionalFieldOf("processingTime", 100).forGetter(CuttingBoardRecipe::getProcessingTime),
            SerializationHelper.SIMPLE_ITEMSTACK_CODEC.listOf().xmap(
                    list -> list.toArray(new ItemStack[0]),
                    Arrays::asList
            ).fieldOf("outputs").forGetter(CuttingBoardRecipe::getOutputs),
            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(AbstractFoodRecipe::getExperience)
    ).apply(instance, (input, type, time, outputs, exp) -> new CuttingBoardRecipe(recipeId, input, type, time, outputs, exp)));

    private final Ingredient input;
    private final RecipeProcessingType processingType;
    private final int processingTime;
    private final ItemStack[] outputs;

    private CuttingBoardRecipe(ResourceLocation id, Ingredient input, RecipeProcessingType processingType, int processingTime, ItemStack[] outputs, float experience) {
        super(id, experience);
        this.processingType = processingType;
        this.processingTime = processingTime;
        this.outputs = outputs;
        this.input = input;
        if (processingTime < 1) {
            throwValidationError("Processing time cannot be lower than 1");
        }
        if (outputs.length > CuttingBoardBlockEntity.SLOT_OUTPUTS.length) {
            throwValidationError(String.format("Output amount of %d exceeds maximum output amount of %d", outputs.length, CuttingBoardBlockEntity.SLOT_OUTPUTS.length));
        }
    }

    public boolean isValidInput(ItemStack stack) {
        return this.input.test(stack);
    }

    public RecipeProcessingType getProcessingType() {
        return processingType;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public ItemStack[] getOutputs() {
        return outputs;
    }

    @Override
    public boolean matches(CuttingBoardBlockEntity blockEntity, Level level) {
        ItemStack stack = blockEntity.getInputItem();
        return isValidInput(stack);
    }

    @Override
    public ItemStack assemble(CuttingBoardBlockEntity board, RegistryAccess access) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return outputs[0];
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.CUTTING_BOARD_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.CUTTING_BOARD_RECIPE_SERIALIZER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CuttingBoardRecipe recipe = (CuttingBoardRecipe) o;

        return getId().equals(recipe.getId()) && processingType.equals(recipe.processingType);
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + processingType.hashCode();
        return result;
    }
}
