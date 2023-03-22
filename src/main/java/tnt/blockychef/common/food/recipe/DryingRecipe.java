package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
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
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

public class DryingRecipe extends AbstractFoodRecipe<DryingRackBlockEntity> {

    private final Ingredient input;
    private final ItemStack output;
    private final int dryingTime;

    public DryingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int dryingTime, float experience) {
        super(id, experience);
        this.input = input;
        this.output = output;
        this.dryingTime = dryingTime;
    }

    @Override
    public boolean matches(DryingRackBlockEntity container, Level level) {
        ItemStack stack = container.getItem(0);
        return isValidInput(stack);
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

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    public int getDryingTime() {
        return dryingTime;
    }

    public static final class Serializer implements RecipeSerializer<DryingRecipe> {

        @Override
        public DryingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            JsonObject output = GsonHelper.getAsJsonObject(json, "output");
            ItemStack result = SerializationHelper.resolveItemStackFromJson(output);
            int dryTime = GsonHelper.getAsInt(json, "dryingTime");
            float experience = GsonHelper.getAsFloat(json, "experience", 0.0F);
            return new DryingRecipe(id, ingredient, result, dryTime, experience);
        }

        @Override
        public @Nullable DryingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack stack = buffer.readItem();
            int dryingTime = buffer.readInt();
            float experience = buffer.readFloat();
            return new DryingRecipe(id, ingredient, stack, dryingTime, experience);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DryingRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeInt(recipe.dryingTime);
            buffer.writeFloat(recipe.getExperience());
        }
    }
}
