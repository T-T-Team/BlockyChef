package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.MortarAndPestleBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

import java.util.Arrays;
import java.util.List;

public class MortarRecipe extends AbstractFoodRecipe<MortarAndPestleBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<MortarRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            MultiIngredient.CODEC.listOf().fieldOf("inputs").forGetter(t -> t.inputs),
            SerializationHelper.SIMPLE_ITEMSTACK_CODEC.listOf().xmap(
                    list -> list.toArray(ItemStack[]::new),
                    Arrays::asList
            ).fieldOf("outputs").forGetter(MortarRecipe::getOutput),
            Codec.INT.fieldOf("processingTime").forGetter(MortarRecipe::getProcessingTime),
            resolveExperience(),
            resolveRemainderConsumer()
    ).apply(instance, (inputs, output, time, exp, rem) -> new MortarRecipe(recipeId, inputs, output, time, exp, rem)));

    private final List<MultiIngredient> inputs;
    private final ItemStack[] output;
    private final int processingTime;

    public MortarRecipe(ResourceLocation recipeId, List<MultiIngredient> inputs, ItemStack[] output, int processingTime, float exp, List<MultiIngredient> remainderConsumer) {
        super(recipeId, remainderConsumer, exp);
        this.inputs = inputs;
        this.output = output;
        this.processingTime = processingTime;
        if (inputs.isEmpty() || inputs.size() > MortarAndPestleBlockEntity.INPUTS.length) {
            throwValidationError("Input count must be bigger than 0 and smaller than " + MortarAndPestleBlockEntity.INPUTS.length);
        }
        if (processingTime <= 0) {
            throwValidationError("Processing time must be bigger than 0");
        }
    }

    public List<MultiIngredient> getInputs() {
        return inputs;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public ItemStack[] getOutput() {
        return output;
    }

    @Override
    public boolean matches(MortarAndPestleBlockEntity mortarAndPestle, Level level) {
        return MultiIngredient.test(mortarAndPestle, MortarAndPestleBlockEntity.INPUTS, inputs);
    }

    @Override
    public ItemStack assemble(MortarAndPestleBlockEntity blockEntity, RegistryAccess access) {
        return getResultItem(access).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return output[0];
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.MORTAR_AND_PESTLE_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.MORTAR_AND_PESTLE_RECIPE_SERIALIZER;
    }
}
