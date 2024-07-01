package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.MeatGrinderBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class MeatGrinderRecipe extends AbstractFoodRecipe<MeatGrinderBlockEntity> {

    public static final Codec<MeatGrinderRecipe> CODEC_PROVIDER = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(t -> t.input),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("processingAmount", 1).forGetter(MeatGrinderRecipe::getProcessingAmount),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("output").forGetter(t -> t.result),
            resolveExperience(),
            resolveRemainderConsumer()
    ).apply(instance, MeatGrinderRecipe::new));

    private final Ingredient input;
    private final int processingAmount;
    private final ItemStack result;

    public MeatGrinderRecipe(Ingredient input, int processingAmount, ItemStack result, float experience, List<MultiIngredient> remainderConsumer) {
        super(remainderConsumer, experience);
        this.input = input;
        this.processingAmount = processingAmount;
        this.result = result;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return result;
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
