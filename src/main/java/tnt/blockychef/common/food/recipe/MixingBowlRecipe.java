package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.MixingBowlBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.Arrays;
import java.util.List;

public class MixingBowlRecipe extends AbstractFoodRecipe<MixingBowlBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<MixingBowlRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            MultiIngredient.CODEC.listOf().fieldOf("inputs").forGetter(MixingBowlRecipe::getInputs),
            Codecs.SIMPLE_ITEMSTACK_CODEC.listOf().xmap(
                    list -> list.toArray(ItemStack[]::new),
                    Arrays::asList
            ).fieldOf("outputs").forGetter(MixingBowlRecipe::getOutputs),
            Codec.INT.fieldOf("mixingTime").forGetter(MixingBowlRecipe::getMixingTime),
            resolveExperience(),
            resolveRemainderConsumer()
    ).apply(instance, (inputs, outputs, time, exp, rem) -> new MixingBowlRecipe(recipeId, inputs, outputs, time, exp, rem)));

    private final List<MultiIngredient> ingredients;
    private final ItemStack[] outputs;
    private final int mixingTime;

    public MixingBowlRecipe(ResourceLocation recipeId, List<MultiIngredient> inputs, ItemStack[] outputs, int mixingTime, float experience, List<MultiIngredient> remainderConsumer) {
        super(recipeId, remainderConsumer, experience);
        this.ingredients = inputs;
        this.outputs = outputs;
        this.mixingTime = mixingTime;
        if (inputs.isEmpty() || inputs.size() > MixingBowlBlockEntity.INPUTS.length) {
            throwValidationError("Input count must be bigger than 0 and smaller than " + MixingBowlBlockEntity.INPUTS.length);
        }
        if (outputs.length == 0 || outputs.length > MixingBowlBlockEntity.OUTPUTS.length) {
            throwValidationError("Output count must be bigger than 0 and smaller than " + MixingBowlBlockEntity.OUTPUTS.length);
        }
        if (mixingTime <= 0) {
            throwValidationError("Mixing time must be bigger than 0");
        }
    }

    public List<MultiIngredient> getInputs() {
        return ingredients;
    }

    public ItemStack[] getOutputs() {
        return outputs;
    }

    public int getMixingTime() {
        return mixingTime;
    }

    @Override
    public boolean matches(MixingBowlBlockEntity blockEntity, Level level) {
        return MultiIngredient.test(blockEntity, MixingBowlBlockEntity.INPUTS, ingredients);
    }

    @Override
    public ItemStack assemble(MixingBowlBlockEntity blockEntity, RegistryAccess access) {
        return getResultItem(access).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return outputs[0];
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.MIXING_BOWL_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.MIXING_BOWL_RECIPE_SERIALIZER;
    }
}
