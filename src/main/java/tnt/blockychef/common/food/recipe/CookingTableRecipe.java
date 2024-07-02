package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.CookingTableBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class CookingTableRecipe extends AbstractFoodRecipe<CookingTableBlockEntity> {

    public static final Codec<CookingTableRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MultiIngredient.CODEC.listOf().fieldOf("inputs").forGetter(CookingTableRecipe::getInputs),
            Codecs.SIMPLE_ITEMSTACK_CODEC.listOf().fieldOf("outputs").forGetter(CookingTableRecipe::getOutputs),
            ExtraCodecs.POSITIVE_INT.fieldOf("assemblyTime").forGetter(CookingTableRecipe::getAssemblyTime),
            resolveRemainderConsumer(),
            resolveExperience()
    ).apply(instance, CookingTableRecipe::new));

    private final List<MultiIngredient> inputs;
    private final List<ItemStack> outputs;
    private final int assemblyTime;

    public CookingTableRecipe(List<MultiIngredient> inputs, List<ItemStack> outputs, int assemblyTime, List<MultiIngredient> inputConsumers, float experience) {
        super(inputConsumers, experience);
        this.inputs = inputs;
        this.outputs = outputs;
        this.assemblyTime = assemblyTime;
        if (inputs.size() == 0 || inputs.size() > 9) {
            throwValidationError("There must be between 1-9 inputs defined, got " + inputs.size());
        }
        if (outputs.size() == 0 || outputs.size() > 6) {
            throwValidationError("There must be between 1-6 outputs defined, got " + outputs.size());
        }
    }

    public List<MultiIngredient> getInputs() {
        return inputs;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

    public int getAssemblyTime() {
        return assemblyTime;
    }

    @Override
    public boolean matches(CookingTableBlockEntity pContainer, Level pLevel) {
        return MultiIngredient.test(pContainer, CookingTableBlockEntity.INPUTS, this.inputs);
    }

    @Override
    public ItemStack assemble(CookingTableBlockEntity pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.COOKING_TABLE_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.COOKING_TABLE_SERIALIZER;
    }
}
