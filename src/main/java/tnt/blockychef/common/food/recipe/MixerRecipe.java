package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
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
import tnt.tntlib.api.serialization.Codecs;

import java.util.List;

public class MixerRecipe extends AbstractFoodRecipe<MixerBlockEntity> {

    public static final CodecRecipeSerializer.CodecProvider<MixerRecipe> CODEC = id -> RecordCodecBuilder.create(instance -> instance.group(
            MultiIngredient.CODEC.listOf().fieldOf("inputs").forGetter(MixerRecipe::getInputs),
            Codecs.enumCodec(RpmValue.class).fieldOf("rpm").forGetter(MixerRecipe::getRpm),
            FluidStack.CODEC.fieldOf("output").forGetter(MixerRecipe::getOutput),
            resolveExperience(),
            resolveRemainderConsumer()
    ).apply(instance, (in, rpm, fluid, exp, cons) -> new MixerRecipe(id, in, rpm, fluid, exp, cons)));

    private final List<MultiIngredient> inputs;
    private final RpmValue rpm;
    private final FluidStack output;

    public MixerRecipe(ResourceLocation id, List<MultiIngredient> inputs, RpmValue rpm, FluidStack output, float experience, List<MultiIngredient> remainderConsumer) {
        super(id, remainderConsumer, experience);
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
    public boolean matches(MixerBlockEntity mixer, Level level) {
        return MultiIngredient.test(mixer, MixerBlockEntity.INPUTS, inputs);
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
