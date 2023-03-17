package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.GraterBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;

public class GratingRecipe extends AbstractFoodRecipe<GraterBlockEntity> {

    private final Ingredient input;
    private final ItemStack output;
    private final int gratingAmount;

    public GratingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int gratingAmount, float experience) {
        super(id, experience);
        this.input = input;
        this.output = output;
        this.gratingAmount = gratingAmount;
    }

    public boolean isValidInput(ItemStack stack) {
        return input.test(stack);
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
    public ItemStack assemble(GraterBlockEntity container) {
        return this.getResultItem().copy();
    }

    @Override
    public ItemStack getResultItem() {
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

    public static final class Serializer implements RecipeSerializer<GratingRecipe> {

        @Override
        public GratingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            JsonObject output = GsonHelper.getAsJsonObject(json, "output");
            ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(output, "item"));
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == Items.AIR) {
                throw new JsonSyntaxException("Unknown item: " + itemId);
            }
            int count = GsonHelper.getAsInt(output, "count", 1);
            ItemStack result = new ItemStack(item, count);
            int gratingAmount = GsonHelper.getAsInt(json, "gratingAmount", 3);
            float experience = GsonHelper.getAsFloat(json, "experience", 0.0F);
            return new GratingRecipe(id, ingredient, result, gratingAmount, experience);
        }

        @Override
        public @Nullable GratingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack stack = buffer.readItem();
            int gratingAmount = buffer.readInt();
            float experience = buffer.readFloat();
            return new GratingRecipe(id, ingredient, stack, gratingAmount, experience);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GratingRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeInt(recipe.gratingAmount);
            buffer.writeFloat(recipe.getExperience());
        }
    }
}
