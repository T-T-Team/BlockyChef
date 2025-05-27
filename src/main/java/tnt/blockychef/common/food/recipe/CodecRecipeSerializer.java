package tnt.blockychef.common.food.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public final class CodecRecipeSerializer<T extends AbstractFoodRecipe<?>> implements RecipeSerializer<T> {

    private final CodecProvider<T> provider;

    private CodecRecipeSerializer(CodecProvider<T> provider) {
        this.provider = provider;
    }

    public static <T extends AbstractFoodRecipe<?>> CodecRecipeSerializer<T> forCodec(CodecProvider<T> provider) {
        return new CodecRecipeSerializer<>(provider);
    }

    @Override
    public T fromJson(ResourceLocation recipeId, JsonObject object) {
        Codec<T> codec = provider.codec(recipeId);
        DataResult<T> dataResult = codec.parse(JsonOps.INSTANCE, object);
        return dataResult.getOrThrow(false, error -> {
            throw new JsonSyntaxException("Unable to parse recipe " + recipeId + " due to error: " + error);
        });
    }

    @Override
    public @Nullable T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        Codec<T> codec = provider.codec(recipeId);
        return buffer.readJsonWithCodec(codec);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        ResourceLocation recipeId = recipe.getId();
        Codec<T> codec = provider.codec(recipeId);
        buffer.writeJsonWithCodec(codec, recipe);
    }

    @FunctionalInterface
    public interface CodecProvider<T> {
        Codec<T> codec(ResourceLocation recipeId);
    }
}
