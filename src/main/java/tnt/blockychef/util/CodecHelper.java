package tnt.blockychef.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public final class CodecHelper {

    // This can be removed if Questing library is implemented
    public static <V> Codec<V> registryEntry(IForgeRegistry<V> registry) {
        return ResourceLocation.CODEC.flatXmap(location -> {
            if (!registry.containsKey(location)) {
                return DataResult.error("Unknown ID: " + location + ", does not exist in " + registry.getRegistryName() + " registry");
            }
            V value = registry.getValue(location);
            return DataResult.success(value);
        }, value -> {
            ResourceLocation key = registry.getKey(value);
            if (key == null) {
                return DataResult.error("Value is not registered");
            }
            return DataResult.success(key);
        });
    }
}
