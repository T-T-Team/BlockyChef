package tnt.blockychef.common.food.recipe;

import com.google.common.collect.ImmutableList;
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
import java.util.List;
import java.util.Map;

public class CuttingBoardRecipe extends AbstractFoodRecipe<CuttingBoardBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<CuttingBoardRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.INGREDIENT_CODEC.fieldOf("input").forGetter(t -> t.input),
            Codec.unboundedMap(
                    ResourceLocation.CODEC.comapFlatMap(
                            id -> RecipeProcessingTypes.getById(id).map(DataResult::success).orElse(DataResult.error(() -> "Unknown recipe processing type")),
                            RecipeProcessingType::getLocation
                    ),
                    CuttingBoardSubRecipe.CODEC
            ).fieldOf("values").forGetter(t -> t.subRecipeMap),
            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(AbstractFoodRecipe::getExperience)
    ).apply(instance, (ingredient, subRecipeMap, exp) -> new CuttingBoardRecipe(recipeId, ingredient, subRecipeMap, exp)));
    private final Ingredient input;
    private final Map<RecipeProcessingType, CuttingBoardSubRecipe> subRecipeMap;

    public CuttingBoardRecipe(ResourceLocation id, Ingredient input, Map<RecipeProcessingType, CuttingBoardSubRecipe> subRecipeMap, float experience) {
        super(id, experience);
        this.input = input;
        this.subRecipeMap = subRecipeMap;
    }

    public CuttingBoardSubRecipe getSubRecipe(RecipeProcessingType recipeProcessingType) {
        return subRecipeMap.get(recipeProcessingType);
    }

    public RecipeProcessingType getFirstProcessingType() {
        return getRecipeProcessingTypes().get(0);
    }

    public List<RecipeProcessingType> getRecipeProcessingTypes() {
        return ImmutableList.copyOf(subRecipeMap.keySet());
    }

    public boolean isValidInput(ItemStack stack) {
        return this.input.test(stack);
    }

    @Override
    public boolean matches(CuttingBoardBlockEntity blockEntity, Level level) {
        ItemStack stack = blockEntity.getInputItem();
        return isValidInput(stack);
    }

    @Override
    public ItemStack assemble(CuttingBoardBlockEntity board, RegistryAccess access) {
        throw new UnsupportedOperationException(); // TODO implement
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.CUTTING_BOARD_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.CUTTING_BOARD_RECIPE_SERIALIZER;
    }

    public static final class CuttingBoardSubRecipe {

        static final Codec<CuttingBoardSubRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SerializationHelper.SIMPLE_ITEMSTACK_CODEC.listOf().fieldOf("outputs").forGetter(t -> Arrays.asList(t.outputs)),
                Codec.INT.fieldOf("time").forGetter(CuttingBoardSubRecipe::getTime)
        ).apply(instance, (itemStacks, integer) -> new CuttingBoardSubRecipe(itemStacks.toArray(new ItemStack[0]), integer)));
        private final ItemStack[] outputs;
        private final int time;

        public CuttingBoardSubRecipe(ItemStack[] outputs, int time) {
            this.outputs = outputs;
            this.time = time;
        }

        public int getTime() {
            return time;
        }

        public ItemStack[] getOutputs() {
            return outputs;
        }
    }
}
