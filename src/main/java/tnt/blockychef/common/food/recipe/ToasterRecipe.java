package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.ToasterBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class ToasterRecipe extends AbstractFoodRecipe<ToasterBlockEntity> implements BurnableRecipe {

    public static final Codec<ToasterRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(t -> t.input),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("output").forGetter(ToasterRecipe::getOutput),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("toastingTime").forGetter(ToasterRecipe::getToastingTime),
            Codec.BOOL.optionalFieldOf("overcooking", false).forGetter(t -> t.overcooking),
            resolveExperience(),
            resolveRemainderConsumer()
    ).apply(instance, ToasterRecipe::new));

    private final Ingredient input;
    private final ItemStack output;
    private final int toastingTime;
    private final boolean overcooking;

    public ToasterRecipe(Ingredient input, ItemStack output, int toastingTime, boolean overcooking, float experience, List<MultiIngredient> remainderConsumer) {
        super(remainderConsumer, experience);
        this.input = input;
        this.output = output;
        this.toastingTime = toastingTime;
        this.overcooking = overcooking;
        if (toastingTime > ToasterBlockEntity.MAX_TIMER_VALUE) {
            throwValidationError("Toasting time exceeded max time setting of " + ToasterBlockEntity.MAX_TIMER_VALUE);
        }
    }

    @Override
    public boolean isOvercooked() {
        return overcooking;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getToastingTime() {
        return toastingTime;
    }

    public boolean matches(ItemStack stack) {
        return input.test(stack);
    }

    @Override
    public boolean matches(ToasterBlockEntity toaster, Level level) {
        return false;
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
}
