package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import tnt.blockychef.common.block.entity.TeapotBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;

import java.util.List;

public class TeapotRecipe extends AbstractFoodRecipe<TeapotBlockEntity> implements BurnableRecipe {

    public static final Codec<TeapotRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MultiIngredient.CODEC.listOf().fieldOf("inputs").forGetter(TeapotRecipe::getInputs),
            FluidStack.CODEC.fieldOf("inputFluid").forGetter(TeapotRecipe::getBaseFluid),
            FluidStack.CODEC.fieldOf("result").forGetter(TeapotRecipe::getResult),
            Codec.INT.fieldOf("cookingTime").forGetter(TeapotRecipe::getCookingTime),
            Codec.FLOAT.optionalFieldOf("minTemperature", 7.0F).forGetter(TeapotRecipe::getMinTemperature),
            resolveRemainderConsumer(),
            resolveExperience()
    ).apply(instance, TeapotRecipe::new));

    private final List<MultiIngredient> inputs;
    private final FluidStack baseFluid;
    private final FluidStack result;
    private final int cookingTime;
    private final float minTemperature;

    public TeapotRecipe(List<MultiIngredient> inputs, FluidStack baseFluid, FluidStack result, int cookTime, float minTemperature, List<MultiIngredient> outputConsumers, float experience) {
        super(outputConsumers, experience);
        this.inputs = inputs;
        this.baseFluid = baseFluid;
        this.result = result;
        this.cookingTime = cookTime;
        this.minTemperature = minTemperature;

        if (!isOvercooked() && result.isEmpty()) {
            throw new JsonSyntaxException("Unknown fluid");
        }
    }

    @Override
    public boolean isOvercooked() {
        return inputs.isEmpty();
    }

    @Override
    public boolean matches(TeapotBlockEntity pContainer, Level pLevel) {
        for (MultiIngredient ingredient : inputs) {
            if (!ingredient.test(pContainer, TeapotBlockEntity.INPUTS)) {
                return false;
            }
        }
        if (!isOvercooked()) {
            for (int inputSlot : TeapotBlockEntity.INPUTS) {
                ItemStack itemStack = pContainer.getItem(inputSlot);
                if (itemStack.isEmpty())
                    continue;
                boolean accepted = false;
                for (MultiIngredient ingredient : inputs) {
                    if (ingredient.acceptsItem(itemStack)) {
                        accepted = true;
                        break;
                    }
                }
                if (!accepted) {
                    return false;
                }
            }
        }
        return pContainer.hasFluidForRecipe(this);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack assemble(TeapotBlockEntity pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.TEAPOT_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.TEAPOT_RECIPE_SERIALIZER;
    }

    public List<MultiIngredient> getInputs() {
        return inputs;
    }

    public FluidStack getBaseFluid() {
        return baseFluid;
    }

    public FluidStack getResult() {
        return result;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public float getMinTemperature() {
        return minTemperature;
    }
}
