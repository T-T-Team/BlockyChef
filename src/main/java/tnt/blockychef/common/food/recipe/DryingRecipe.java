package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class DryingRecipe extends AbstractFoodRecipe<DryingRackBlockEntity> {

    public static final Codec<DryingRecipe> CODEC_PROVIDER = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(t -> t.input),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("output").forGetter(t -> t.output),
            Codec.INT.fieldOf("dryingTime").forGetter(DryingRecipe::getDryingTime),
            resolveExperience(),
            resolveRemainderConsumer()
    ).apply(instance, DryingRecipe::new));

    private final Ingredient input;
    private final ItemStack output;
    private final int dryingTime;

    private DryingRecipe(Ingredient input, ItemStack output, int dryingTime, float experience, List<MultiIngredient> remainderConsumer) throws JsonParseException {
        super(remainderConsumer, experience);
        this.input = input;
        this.output = output;
        this.dryingTime = dryingTime;
        if (dryingTime < 20) {
            throwValidationError("Drying time cannot be lower than 20");
        }
    }

    @Override
    public boolean matches(DryingRackBlockEntity container, Level level) {
        return false;
    }

    public boolean isValidInput(ItemStack stack) {
        return input.test(stack);
    }

    @Override
    public ItemStack assemble(DryingRackBlockEntity container, RegistryAccess access) {
        return getResultItem(access).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.DRYING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.DRYING_RECIPE;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getDryingTime() {
        return dryingTime;
    }
}
