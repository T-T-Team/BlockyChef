package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public final class CodecRecipeSerializer<T extends AbstractFoodRecipe<?>> implements RecipeSerializer<T> {

    private final Codec<T> codec;

    private CodecRecipeSerializer(Codec<T> codec) {
        this.codec = codec;
    }

    public static <T extends AbstractFoodRecipe<?>> CodecRecipeSerializer<T> forCodec(Codec<T> codec) {
        return new CodecRecipeSerializer<>(codec);
    }

    @Override
    public Codec<T> codec() {
        return this.codec;
    }

    @Override
    public @Nullable T fromNetwork(FriendlyByteBuf pBuffer) {
        return pBuffer.readJsonWithCodec(codec());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeJsonWithCodec(codec(), recipe);
    }
}
