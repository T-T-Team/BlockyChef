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
import tnt.tntlib.api.serialization.Codecs;

import java.util.Arrays;
import java.util.List;

public class CuttingBoardRecipe extends AbstractFoodRecipe<CuttingBoardBlockEntity> {

    public static final Codec<CuttingBoardRecipe> CODEC_PROVIDER = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(t -> t.input),
            ResourceLocation.CODEC.comapFlatMap(
                    location -> RecipeProcessingTypes.getById(location).map(DataResult::success).orElse(DataResult.error(() -> "Unknown recipe processing type '" + location + "'")),
                    RecipeProcessingType::getLocation
            ).fieldOf("processingType").forGetter(CuttingBoardRecipe::getProcessingType),
            Codec.INT.optionalFieldOf("processingTime", 100).forGetter(CuttingBoardRecipe::getProcessingTime),
            Codecs.SIMPLE_ITEMSTACK_CODEC.listOf().xmap(
                    list -> list.toArray(ItemStack[]::new),
                    Arrays::asList
            ).fieldOf("outputs").forGetter(CuttingBoardRecipe::getOutputs),
            resolveExperience(),
            resolveRemainderConsumer()
    ).apply(instance, CuttingBoardRecipe::new));

    private final Ingredient input;
    private final RecipeProcessingType processingType;
    private final int processingTime;
    private final ItemStack[] outputs;

    private CuttingBoardRecipe(Ingredient input, RecipeProcessingType processingType, int processingTime, ItemStack[] outputs, float experience, List<MultiIngredient> remainderConsumer) {
        super(remainderConsumer, experience);
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

    public Ingredient getInput() {
        return input;
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
        return getResultItem(access).copy();
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
}
