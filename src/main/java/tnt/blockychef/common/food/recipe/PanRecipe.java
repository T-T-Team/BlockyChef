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
import tnt.blockychef.common.block.entity.PanBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class PanRecipe extends AbstractFoodRecipe<PanBlockEntity> implements BurnableRecipe {

    public static final Codec<PanRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(PanRecipe::getInput),
            CookingConfiguration.CODEC.fieldOf("configuration").forGetter(PanRecipe::getConfiguration),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("result").forGetter(PanRecipe::getResult),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("burntResult").forGetter(PanRecipe::getBurntResult),
            Codec.BOOL.optionalFieldOf("overcooking", false).forGetter(t -> t.overcooking),
            resolveRemainderConsumer(),
            resolveExperience()
    ).apply(instance, PanRecipe::new));

    private final Ingredient input;
    private final CookingConfiguration configuration;
    private final ItemStack result;
    private final ItemStack burntResult;
    private final boolean overcooking;

    public PanRecipe(Ingredient input, CookingConfiguration configuration, ItemStack result, ItemStack burntResult, boolean overcooking, List<MultiIngredient> outputConsumers, float experience) throws JsonParseException {
        super(outputConsumers, experience);
        this.input = input;
        this.configuration = configuration;
        this.result = result;
        this.burntResult = burntResult;
        this.overcooking = overcooking;
    }

    public Ingredient getInput() {
        return input;
    }

    public CookingConfiguration getConfiguration() {
        return configuration;
    }

    public ItemStack getResult() {
        return result;
    }

    public ItemStack getBurntResult() {
        return burntResult;
    }

    @Override
    public boolean isBurning() {
        return overcooking;
    }

    @Override
    public boolean matches(PanBlockEntity pContainer, Level pLevel) {
        throw new UnsupportedOperationException();
    }

    public boolean matches(ItemStack stack) {
        return input.test(stack);
    }

    @Override
    public ItemStack assemble(PanBlockEntity pContainer, RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.PAN_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.PAN_RECIPE;
    }
}
