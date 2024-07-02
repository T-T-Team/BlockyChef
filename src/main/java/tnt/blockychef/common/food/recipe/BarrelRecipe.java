package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.BarrelBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.Arrays;
import java.util.List;

public class BarrelRecipe extends AbstractFoodRecipe<BarrelBlockEntity> {

    public static final Codec<BarrelRecipe> CODEC_PROVIDER = RecordCodecBuilder.create(instance -> instance.group(
            MultiIngredient.CODEC.listOf().fieldOf("inputs").forGetter(BarrelRecipe::getInputs),
            Codecs.SIMPLE_ITEMSTACK_CODEC.listOf().xmap(
                    list -> list.toArray(ItemStack[]::new),
                    Arrays::asList
            ).fieldOf("outputs").forGetter(BarrelRecipe::getOutputs),
            Codec.INT.fieldOf("fermentTime").forGetter(BarrelRecipe::getFermentTime),
            resolveExperience(),
            resolveRemainderConsumer()
    ).apply(instance, BarrelRecipe::new));

    private final List<MultiIngredient> inputs;
    private final ItemStack[] outputs;
    private final int fermentTime;

    public BarrelRecipe(List<MultiIngredient> inputs, ItemStack[] outputs, int fermentTime, float experience, List<MultiIngredient> remainderConsumer) {
        super(remainderConsumer, experience);
        this.inputs = inputs;
        this.outputs = outputs;
        this.fermentTime = fermentTime;
    }

    public List<MultiIngredient> getInputs() {
        return inputs;
    }

    public ItemStack[] getOutputs() {
        return outputs;
    }

    public int getFermentTime() {
        return fermentTime;
    }

    @Override
    public boolean matches(BarrelBlockEntity blockEntity, Level level) {
        return MultiIngredient.test(blockEntity, BarrelBlockEntity.INPUTS, inputs);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return outputs[0];
    }

    @Override
    public ItemStack assemble(BarrelBlockEntity block, RegistryAccess access) {
        return getResultItem(access).copy();
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.BARREL_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.BARREL_RECIPE_SERIALIZER;
    }
}
