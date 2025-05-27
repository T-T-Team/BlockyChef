package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.block.entity.GrillBlockEntity;
import tnt.blockychef.common.heat.HeatValues;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class GrillRecipe extends AbstractFoodRecipe<GrillBlockEntity> implements BurnableRecipe {

    public static final CodecRecipeSerializer.CodecProvider<GrillRecipe> CODEC = id -> RecordCodecBuilder.create(instance -> instance.group(
            Codecs.INGREDIENT.fieldOf("input").forGetter(GrillRecipe::getInput),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("result").forGetter(GrillRecipe::getResult),
            Codecs.SIMPLE_ITEMSTACK_CODEC.optionalFieldOf("burntResult", ItemStack.EMPTY).forGetter(GrillRecipe::getBurnResult),
            GrillingConfiguration.CODEC.fieldOf("configuration").forGetter(GrillRecipe::getConfiguration),
            Codec.BOOL.optionalFieldOf("overcooking", false).forGetter(GrillRecipe::isOvercooked),
            resolveRemainderConsumer(),
            resolveExperience()
    ).apply(instance, (in, res, burn, cfg, overcook, cons, exp) -> new GrillRecipe(id, in, res, burn, cfg, overcook, cons, exp)));

    private final Ingredient input;
    private final ItemStack result;
    private final ItemStack burnResult;
    private final GrillingConfiguration configuration;
    private final boolean overcooking;

    public GrillRecipe(ResourceLocation id, Ingredient input, ItemStack result, ItemStack burnResult, GrillingConfiguration configuration, boolean overcooking, List<MultiIngredient> outputConsumers, float experience) throws JsonParseException {
        super(id, outputConsumers, experience);
        this.input = input;
        this.result = result;
        this.burnResult = burnResult;
        this.configuration = configuration;
        this.overcooking = overcooking;
    }

    @Override
    public boolean matches(GrillBlockEntity pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack assemble(GrillBlockEntity pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.GRILL_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.GRILL_RECIPE;
    }

    @Override
    public boolean isOvercooked() {
        return overcooking;
    }

    public boolean matches(ItemStack itemStack) {
        return this.input.test(itemStack);
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getResult() {
        return result;
    }

    public ItemStack getBurnResult() {
        return burnResult;
    }

    public GrillingConfiguration getConfiguration() {
        return configuration;
    }

    public record GrillingConfiguration(int time, float minTemperature, float maxTemperature, float burnSpeed) implements BaseCookConfiguration {

        public static final Codec<GrillingConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("grillTime").forGetter(GrillingConfiguration::time),
                Codec.floatRange(0.0F, HeatValues.MAX_TEMPERATURE).fieldOf("minTemperature").forGetter(GrillingConfiguration::minTemperature),
                Codec.floatRange(0.0F, HeatValues.MAX_TEMPERATURE).fieldOf("maxTemperature").forGetter(GrillingConfiguration::maxTemperature),
                Codec.FLOAT.optionalFieldOf("burnSpeed", 1.0F).forGetter(GrillingConfiguration::burnSpeed)
        ).apply(instance, GrillingConfiguration::new));
    }
}
