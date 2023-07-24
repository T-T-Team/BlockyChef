package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.JuicerBlockEntity;
import tnt.blockychef.common.food.fluid.Fluid;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

public class JuicerRecipe extends AbstractFoodRecipe<JuicerBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<JuicerRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.INGREDIENT_CODEC.fieldOf("input").forGetter(t -> t.input),
            Codec.INT.fieldOf("pressAmount").forGetter(JuicerRecipe::getPressAmount),
            Fluid.CODEC.fieldOf("output").forGetter(JuicerRecipe::getOutput),
            resolveExperience()
    ).apply(instance, (in, amount, out, exp) -> new JuicerRecipe(recipeId, in, amount, out, exp)));

    private final Ingredient input;
    private final int pressAmount;
    private final Fluid output;

    public JuicerRecipe(ResourceLocation recipeId, Ingredient ingredient, int pressAmount, Fluid value, float experience) {
        super(recipeId, experience);
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

    public Fluid getOutput() {
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
