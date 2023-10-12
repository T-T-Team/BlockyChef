package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.StoveBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class StoveRecipe extends AbstractFoodRecipe<StoveBlockEntity> implements BurnableRecipe {

    public static final Codec<StoveRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(StoveRecipe::getInput),
            CookingConfiguration.CODEC.fieldOf("cookingConfiguration").forGetter(StoveRecipe::getConfiguration),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("result").forGetter(StoveRecipe::getResult),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("burntResult").forGetter(StoveRecipe::getBurntResult),
            Codec.BOOL.optionalFieldOf("overcooking", false).forGetter(StoveRecipe::isOvercooking),
            resolveRemainderConsumer(),
            resolveExperience()
    ).apply(instance, StoveRecipe::new));

    private final Ingredient input;
    private final CookingConfiguration configuration;
    private final ItemStack result;
    private final ItemStack burntResult;
    private final boolean isOvercooking;

    public StoveRecipe(Ingredient input, CookingConfiguration configuration, ItemStack result, ItemStack burntResult, boolean isOvercooking, List<MultiIngredient> outputConsumers, float experience) {
        super(outputConsumers, experience);
        this.input = input;
        this.configuration = configuration;
        this.result = result;
        this.burntResult = burntResult;
        this.isOvercooking = isOvercooking;
    }

    @Override
    public boolean isBurning() {
        return isOvercooking;
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

    public boolean isOvercooking() {
        return isOvercooking;
    }

    public boolean matches(ItemStack stack) {
        return input.test(stack);
    }

    @Override
    public boolean matches(StoveBlockEntity pContainer, Level pLevel) {
        return false; // Do not use
    }

    @Override
    public ItemStack assemble(StoveBlockEntity pContainer, RegistryAccess pRegistryAccess) {
        return result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.STOVE_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.STOVE_RECIPE;
    }

}
