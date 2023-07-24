package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.DoughMakerBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

import java.util.Arrays;
import java.util.List;

public class DoughMakerRecipe extends AbstractFoodRecipe<DoughMakerBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<DoughMakerRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            MultiIngredient.CODEC.listOf().fieldOf("inputs").forGetter(DoughMakerRecipe::getInputs),
            SerializationHelper.SIMPLE_ITEMSTACK_CODEC.listOf().xmap(
                    list -> list.toArray(ItemStack[]::new),
                    Arrays::asList
            ).fieldOf("outputs").forGetter(DoughMakerRecipe::getOutputs),
            Codec.INT.fieldOf("processingTime").forGetter(DoughMakerRecipe::getProcessingTime),
            resolveExperience()
    ).apply(instance, (in, out, time, exp) -> new DoughMakerRecipe(recipeId, in, out, time, exp)));

    private final List<MultiIngredient> inputs;
    private final ItemStack[] outputs;
    private final int processingTime;

    public DoughMakerRecipe(ResourceLocation recipeId, List<MultiIngredient> inputs, ItemStack[] outputs, int processingTime, float experience) {
        super(recipeId, experience);
        this.inputs = inputs;
        this.outputs = outputs;
        this.processingTime = processingTime;
    }

    public List<MultiIngredient> getInputs() {
        return inputs;
    }

    public ItemStack[] getOutputs() {
        return outputs;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    @Override
    public boolean matches(DoughMakerBlockEntity doughMaker, Level level) {
        for (MultiIngredient multiIngredient : inputs) {
            if (!multiIngredient.test(doughMaker, DoughMakerBlockEntity.INPUTS)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return outputs[0];
    }

    @Override
    public ItemStack assemble(DoughMakerBlockEntity doughMaker, RegistryAccess access) {
        return getResultItem(access).copy();
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.DOUGH_MAKER_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.DOUGH_MAKER_RECIPE_SERIALIZER;
    }
}
