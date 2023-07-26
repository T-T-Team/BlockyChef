package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.GraterBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class GratingRecipe extends AbstractFoodRecipe<GraterBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<GratingRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            Codecs.INGREDIENT_CODEC.fieldOf("input").forGetter(t -> t.input),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("output").forGetter(t -> t.output),
            Codec.INT.optionalFieldOf("gratingAmount", 3).forGetter(GratingRecipe::getGratingAmount),
            resolveExperience(),
            resolveRemainderConsumer()
    ).apply(instance, (ingredient, stack, amount, exp, rem) -> new GratingRecipe(recipeId, ingredient, stack, amount, exp, rem)));
    private final Ingredient input;
    private final ItemStack output;
    private final int gratingAmount;

    private GratingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int gratingAmount, float experience, List<MultiIngredient> remainderConsumer) throws JsonParseException {
        super(id, remainderConsumer, experience);
        this.input = input;
        this.output = output;
        this.gratingAmount = gratingAmount;
        if (gratingAmount < 1) {
            throwValidationError("Grating amount cannot be lower than 1");
        }
    }

    public boolean isValidInput(ItemStack stack) {
        return input.test(stack);
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getGratingAmount() {
        return gratingAmount;
    }

    @Override
    public boolean matches(GraterBlockEntity container, Level level) {
        ItemStack stack = container.getItem(0);
        return this.isValidInput(stack);
    }

    @Override
    public ItemStack assemble(GraterBlockEntity container, RegistryAccess access) {
        return this.getResultItem(access).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return this.output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.GRATING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.GRATING_RECIPE;
    }
}
