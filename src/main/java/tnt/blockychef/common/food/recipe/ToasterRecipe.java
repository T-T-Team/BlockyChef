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
import tnt.blockychef.common.block.entity.ToasterBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

public class ToasterRecipe extends AbstractFoodRecipe<ToasterBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<ToasterRecipe> CODEC_PROVIDER = recipeId -> RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.INGREDIENT_CODEC.fieldOf("input").forGetter(t -> t.input),
            SerializationHelper.SIMPLE_ITEMSTACK_CODEC.fieldOf("output").forGetter(ToasterRecipe::getOutput),
            SerializationHelper.SIMPLE_ITEMSTACK_CODEC.fieldOf("burntOutput").forGetter(ToasterRecipe::getBurntOutput),
            ToastingLimits.CODEC.fieldOf("toasting").forGetter(ToasterRecipe::getToastingLimits),
            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(AbstractFoodRecipe::getExperience)
    ).apply(instance, (in, out, burntOut, limits, exp) -> new ToasterRecipe(recipeId, in, out, burntOut, limits, exp)));

    private final Ingredient input;
    private final ItemStack output;
    private final ItemStack burntOutput;
    private final ToastingLimits toastingLimits;

    public ToasterRecipe(ResourceLocation recipeId, Ingredient input, ItemStack output, ItemStack burntOutput, ToastingLimits limits, float experience) {
        super(recipeId, experience);
        this.input = input;
        this.output = output;
        this.burntOutput = burntOutput;
        this.toastingLimits = limits;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getBurntOutput() {
        return burntOutput;
    }

    public ToastingLimits getToastingLimits() {
        return toastingLimits;
    }

    @Override
    public boolean matches(ToasterBlockEntity toaster, Level level) {
        return false; // TODO link to toaster internal method
    }

    @Override
    public ItemStack assemble(ToasterBlockEntity toaster, RegistryAccess access) {
        return getResultItem(access).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.TOASTING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.TOASTER_RECIPE;
    }

    public record ToastingLimits(int minTimeToasting, int maxTimeToasting) {

        public static final Codec<ToastingLimits> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("minTime").forGetter(ToastingLimits::minTimeToasting),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("maxTime").forGetter(ToastingLimits::maxTimeToasting)
        ).apply(instance, ToastingLimits::new));
    }
}
