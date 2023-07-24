package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import tnt.blockychef.common.block.entity.MixerBlockEntity;
import tnt.blockychef.common.init.BlockyChefRecipeSerializers;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.SerializationHelper;

import java.util.List;

public class MixerRecipe extends AbstractItemReturningRecipe<MixerBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<MixerRecipe> CODEC_PROVIDER = recipe -> RecordCodecBuilder.create(instance -> instance.group(
            MultiIngredient.CODEC.listOf().fieldOf("inputs").forGetter(MixerRecipe::getInputs),
            SerializationHelper.enumCodec(RpmValue.class).fieldOf("rpm").forGetter(MixerRecipe::getRpm),
            FluidStack.CODEC.fieldOf("output").forGetter(MixerRecipe::getOutput),
            resolveContainerItems(),
            resolveExperience()
    ).apply(instance, (in, rpm, out, cti, exp) -> new MixerRecipe(recipe, in, rpm, out, cti, exp)));

    private final List<MultiIngredient> inputs;
    private final RpmValue rpm;
    private final FluidStack output;

    public MixerRecipe(ResourceLocation recipeId, List<MultiIngredient> inputs, RpmValue rpm, FluidStack output, List<ItemStack> containerItems, float experience) {
        super(recipeId, experience, containerItems);
        this.inputs = inputs;
        this.rpm = rpm;
        this.output = output;
    }

    public List<MultiIngredient> getInputs() {
        return inputs;
    }

    public RpmValue getRpm() {
        return rpm;
    }

    public FluidStack getOutput() {
        return output;
    }

    @Override
    public int[] getContainerSlots(MixerBlockEntity container) {
        return MixerBlockEntity.INPUTS;
    }

    @Override
    public boolean matches(MixerBlockEntity mixer, Level level) {
        for (MultiIngredient ingredient : inputs) {
            if (!ingredient.test(mixer, MixerBlockEntity.INPUTS)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(MixerBlockEntity mixer, RegistryAccess access) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockyChefRecipeTypes.MIXER_RECIPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlockyChefRecipeSerializers.MIXER_RECIPE_SERIALIZER;
    }

    public enum RpmValue {
        LOW, MEDIUM, HIGH;

        private final Component translatedText;

        RpmValue() {
            this.translatedText = Component.translatable("label.blockychef." + name().toLowerCase());
        }

        public Component getTranslatedText() {
            return translatedText;
        }
    }
}
