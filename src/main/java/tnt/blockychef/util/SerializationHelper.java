package tnt.blockychef.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Function;

public final class SerializationHelper {

    // This can be removed if Questing library is implemented
    public static <V> Codec<V> registryEntry(IForgeRegistry<V> registry) {
        return ResourceLocation.CODEC.flatXmap(location -> {
            if (!registry.containsKey(location)) {
                return DataResult.error(() -> "Unknown ID: " + location + ", does not exist in " + registry.getRegistryName() + " registry");
            }
            V value = registry.getValue(location);
            return DataResult.success(value);
        }, value -> {
            ResourceLocation key = registry.getKey(value);
            if (key == null) {
                return DataResult.error(() -> "Value is not registered");
            }
            return DataResult.success(key);
        });
    }

    public static JsonObject asObject(JsonElement element) {
        if (element.isJsonObject()) {
            return element.getAsJsonObject();
        }
        throw new JsonSyntaxException(element.getClass().getSimpleName() + " in not a JsonObject");
    }

    public static ItemStack resolveItemStackFromJson(JsonObject object) {
        ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(object, "item"));
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (item == Items.AIR) {
            throw new JsonSyntaxException("Unknown item: " + itemId);
        }
        int count = GsonHelper.getAsInt(object, "count", 1);
        return new ItemStack(item, count);
    }

    public static <O> O[] mapJsonArray(JsonArray array, Function<Integer, O[]> arrayInstance, Function<JsonElement, O> mapper) {
        int length = array.size();
        O[] values = arrayInstance.apply(length);
        int i = 0;
        for (JsonElement element : array) {
            values[i++] = mapper.apply(element);
        }
        return values;
    }
}
