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
            PanCookingConfiguration.CODEC.fieldOf("configuration").forGetter(PanRecipe::getConfiguration),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("result").forGetter(PanRecipe::getResult),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("burntResult").forGetter(PanRecipe::getBurntResult),
            Codec.BOOL.optionalFieldOf("overcooking", false).forGetter(t -> t.overcooking),
            resolveRemainderConsumer(),
            resolveExperience()
    ).apply(instance, PanRecipe::new));

    private final Ingredient input;
    private final PanCookingConfiguration configuration;
    private final ItemStack result;
    private final ItemStack burntResult;
    private final boolean overcooking;

    public PanRecipe(Ingredient input, PanCookingConfiguration configuration, ItemStack result, ItemStack burntResult, boolean overcooking, List<MultiIngredient> outputConsumers, float experience) throws JsonParseException {
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

    public PanCookingConfiguration getConfiguration() {
        return configuration;
    }

    public int getRequiredOilAmount() {
        int rem = configuration.time % configuration.oilConsumptionRate;
        int oilUnits = configuration.time / configuration.oilConsumptionRate;
        if (rem > 0) {
            oilUnits++;
        }
        return oilUnits;
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
        return false;
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

    public record PanCookingConfiguration(int time, float minTemperature, float maxTemperature, float burnSpeed, int oilConsumptionRate, int stirProgressLoss, float stirBurnLoss) implements BaseCookConfiguration {

        public static final Codec<PanCookingConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(PanCookingConfiguration::time),
                Codec.FLOAT.fieldOf("minTemperature").forGetter(PanCookingConfiguration::minTemperature),
                Codec.FLOAT.fieldOf("maxTemperature").forGetter(PanCookingConfiguration::maxTemperature),
                Codec.FLOAT.optionalFieldOf("burnSpeed", 1.0F).forGetter(PanCookingConfiguration::burnSpeed),
                Codec.INT.optionalFieldOf("oilConsumptionInterval", 6).forGetter(PanCookingConfiguration::oilConsumptionRate),
                Codec.INT.optionalFieldOf("stirProgressLoss", 20).forGetter(PanCookingConfiguration::stirProgressLoss),
                Codec.FLOAT.optionalFieldOf("stirBurnLoss", 0.05F).forGetter(PanCookingConfiguration::stirBurnLoss)
        ).apply(instance, PanCookingConfiguration::new));
    }
}
