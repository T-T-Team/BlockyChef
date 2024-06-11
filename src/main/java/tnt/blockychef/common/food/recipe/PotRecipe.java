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
import tnt.blockychef.common.block.entity.PotBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class PotRecipe extends AbstractFoodRecipe<PotBlockEntity> implements BurnableRecipe {

    public static final Codec<PotRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(PotRecipe::getInput),
            PotCookingConfiguration.CODEC.fieldOf("configuration").forGetter(PotRecipe::getConfiguration),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("result").forGetter(PotRecipe::getResult),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("burntResult").forGetter(PotRecipe::getBurntResult),
            Codec.BOOL.optionalFieldOf("overcooking", false).forGetter(t -> t.overcooking),
            resolveRemainderConsumer(),
            resolveExperience()
    ).apply(instance, PotRecipe::new));

    private final Ingredient input;
    private final PotCookingConfiguration configuration;
    private final ItemStack result;
    private final ItemStack burntResult;
    private final boolean overcooking;

    public PotRecipe(Ingredient input, PotCookingConfiguration configuration, ItemStack result, ItemStack burntResult, boolean overcooking, List<MultiIngredient> outputConsumers, float experience) throws JsonParseException {
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

    public PotCookingConfiguration getConfiguration() {
        return configuration;
    }

    public ItemStack getResult() {
        return result;
    }

    public ItemStack getBurntResult() {
        return burntResult;
    }

    @Override
    public boolean matches(PotBlockEntity pContainer, Level pLevel) {
        return false;
    }

    public boolean matches(ItemStack stack) {
        return input.test(stack);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result;
    }

    @Override
    public ItemStack assemble(PotBlockEntity pContainer, RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess).copy();
    }

    @Override
    public boolean isOvercooked() {
        return overcooking;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.POT_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.POT_RECIPE_SERIALIZER;
    }

    public record PotCookingConfiguration(int time, float minTemperature, float maxTemperature, float burnSpeed, int waterEvaporationRate, int stirProgressLoss, float stirBurnLoss, int minWaterLevel) implements BaseCookConfiguration {

        public static final Codec<PotCookingConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(PotCookingConfiguration::time),
                Codec.FLOAT.fieldOf("minTemperature").forGetter(PotCookingConfiguration::minTemperature),
                Codec.FLOAT.fieldOf("maxTemperature").forGetter(PotCookingConfiguration::maxTemperature),
                Codec.FLOAT.optionalFieldOf("burnSpeed", 1.0F).forGetter(PotCookingConfiguration::burnSpeed),
                Codec.INT.optionalFieldOf("waterEvaporationRate", 40).forGetter(PotCookingConfiguration::waterEvaporationRate),
                Codec.INT.optionalFieldOf("stirProgressLoss", 20).forGetter(PotCookingConfiguration::stirProgressLoss),
                Codec.FLOAT.optionalFieldOf("stirBurnLoss", 0.05F).forGetter(PotCookingConfiguration::stirBurnLoss),
                Codec.INT.optionalFieldOf("minWaterLevel", 100).forGetter(PotCookingConfiguration::minWaterLevel)
        ).apply(instance, PotCookingConfiguration::new));
    }
}
