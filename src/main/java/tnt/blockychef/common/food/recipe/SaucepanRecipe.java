package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.SaucepanBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class SaucepanRecipe extends AbstractFoodRecipe<SaucepanBlockEntity> implements BurnableRecipe {

    public static final Codec<SaucepanRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MultiIngredient.CODEC.listOf().fieldOf("inputs").forGetter(SaucepanRecipe::getInputs),
            Codecs.SIMPLE_ITEMSTACK_CODEC.listOf().fieldOf("outputs").forGetter(SaucepanRecipe::getOutputs),
            Codecs.SIMPLE_ITEMSTACK_CODEC.listOf().fieldOf("burnOutputs").forGetter(SaucepanRecipe::getBurnOutputs),
            SaucePanCookingConfiguration.CODEC.fieldOf("configuration").forGetter(SaucepanRecipe::getConfiguration),
            Codec.BOOL.optionalFieldOf("overcooking", false).forGetter(SaucepanRecipe::isBurning),
            resolveRemainderConsumer(),
            resolveExperience()
    ).apply(instance, SaucepanRecipe::new));

    private final List<MultiIngredient> inputs;
    private final List<ItemStack> outputs;
    private final List<ItemStack> burnOutputs;
    private final SaucePanCookingConfiguration configuration;
    private final boolean overcooking;

    public SaucepanRecipe(List<MultiIngredient> inputs, List<ItemStack> outputs, List<ItemStack> burnOutputs, SaucePanCookingConfiguration configuration, boolean overcooking, List<MultiIngredient> outputConsumer, float experience) {
        super(outputConsumer, experience);
        this.inputs = inputs;
        this.outputs = outputs;
        this.burnOutputs = burnOutputs;
        this.configuration = configuration;
        this.overcooking = overcooking;
    }

    public List<MultiIngredient> getInputs() {
        return inputs;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

    public List<ItemStack> getBurnOutputs() {
        return burnOutputs;
    }

    public SaucePanCookingConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public boolean isBurning() {
        return overcooking;
    }

    @Override
    public boolean matches(SaucepanBlockEntity pContainer, Level pLevel) {
        return MultiIngredient.test(pContainer, SaucepanBlockEntity.INPUTS, inputs);
    }

    @Override
    public ItemStack assemble(SaucepanBlockEntity pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.SAUCEPAN_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.SAUCEPAN_RECIPE;
    }

    public record SaucePanCookingConfiguration(int time, float minTemperature, float maxTemperature, float burnSpeed, int stirProgressLoss, float stirBurnLoss) implements BaseCookConfiguration {

        public static final Codec<SaucePanCookingConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(SaucePanCookingConfiguration::time),
                Codec.FLOAT.fieldOf("minTemperature").forGetter(SaucePanCookingConfiguration::minTemperature),
                Codec.FLOAT.fieldOf("maxTemperature").forGetter(SaucePanCookingConfiguration::maxTemperature),
                Codec.FLOAT.optionalFieldOf("burnSpeed", 1.0F).forGetter(SaucePanCookingConfiguration::burnSpeed),
                Codec.INT.optionalFieldOf("stirProgressLoss", 40).forGetter(SaucePanCookingConfiguration::stirProgressLoss),
                Codec.FLOAT.optionalFieldOf("stirBurnLoss", 0.5F).forGetter(SaucePanCookingConfiguration::stirBurnLoss)
        ).apply(instance, SaucePanCookingConfiguration::new));
    }
}
