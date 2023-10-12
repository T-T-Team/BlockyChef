package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import tnt.blockychef.common.block.entity.JuicerBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;

import java.util.List;

public class JuicerRecipe extends AbstractFoodRecipe<JuicerBlockEntity> {

    public static final Codec<JuicerRecipe> CODEC_PROVIDER = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(t -> t.input),
            Codec.INT.fieldOf("pressAmount").forGetter(JuicerRecipe::getPressAmount),
            FluidStack.CODEC.fieldOf("output").forGetter(JuicerRecipe::getOutput),
            resolveExperience(),
            resolveRemainderConsumer()
    ).apply(instance, JuicerRecipe::new));

    private final Ingredient input;
    private final int pressAmount;
    private final FluidStack output;

    public JuicerRecipe(Ingredient ingredient, int pressAmount, FluidStack value, float experience, List<MultiIngredient> remainderConsumer) {
        super(remainderConsumer, experience);
        this.input = ingredient;
        this.pressAmount = pressAmount;
        this.output = value;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getPressAmount() {
        return pressAmount;
    }

    public FluidStack getOutput() {
        return output;
    }

    @Override
    public boolean matches(JuicerBlockEntity juicer, Level level) {
        return input.test(juicer.getInputItem());
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack assemble(JuicerBlockEntity juicer, RegistryAccess access) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.JUICER_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.JUICER_RECIPE_SERIALIZER;
    }
}
