package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.GraterBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

public class GratingRecipe extends AbstractFoodRecipe<GraterBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<GratingRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.INGREDIENT_CODEC.fieldOf("input").forGetter(t -> t.input),
            SerializationHelper.SIMPLE_ITEMSTACK_CODEC.fieldOf("output").forGetter(t -> t.output),
            Codec.INT.optionalFieldOf("gratingAmount", 3).forGetter(GratingRecipe::getGratingAmount),
            resolveExperience()
    ).apply(instance, (ingredient, stack, amount, exp) -> new GratingRecipe(recipeId, ingredient, stack, amount, exp)));
    private final Ingredient input;
    private final ItemStack output;
    private final int gratingAmount;

    private GratingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int gratingAmount, float experience) throws JsonParseException {
        super(id, experience);
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
