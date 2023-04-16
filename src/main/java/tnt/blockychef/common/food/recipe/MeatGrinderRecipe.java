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
import tnt.blockychef.common.block.entity.MeatGrinderBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

public class MeatGrinderRecipe extends AbstractFoodRecipe<MeatGrinderBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<MeatGrinderRecipe> CODEC_PROVIDER = recipe -> RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.INGREDIENT_CODEC.fieldOf("input").forGetter(t -> t.input),
            Codec.intRange(1, 99).optionalFieldOf("processingAmount", 1).forGetter(MeatGrinderRecipe::getProcessingAmount),
            SerializationHelper.SIMPLE_ITEMSTACK_CODEC.fieldOf("output").forGetter(t -> t.result),
            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(AbstractFoodRecipe::getExperience)
    ).apply(instance, (ingredient, amount, item, exp) -> new MeatGrinderRecipe(recipe, ingredient, amount, item, exp)));

    private final Ingredient input;
    private final int processingAmount;
    private final ItemStack result;

    public MeatGrinderRecipe(ResourceLocation id, Ingredient input, int processingAmount, ItemStack result, float experience) {
        super(id, experience);
        this.input = input;
        this.processingAmount = processingAmount;
        this.result = result;
    }

    public int getProcessingAmount() {
        return processingAmount;
    }

    @Override
    public boolean matches(MeatGrinderBlockEntity meatGrinder, Level level) {
        return input.test(meatGrinder.getInputItem());
    }

    @Override
    public ItemStack assemble(MeatGrinderBlockEntity entity, RegistryAccess access) {
        return getResultItem(access).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.MEAT_GRINDER_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.MEAT_GRINDER_RECIPE;
    }
}
