package tnt.tntlib.api.serialization;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class NbtUtil {

    public static <T> ListTag collectionToNbt(Collection<T> collection, Function<T, Tag> serializer) {
        ListTag tag = new ListTag();
        for (T t : collection) {
            tag.add(serializer.apply(t));
        }
        return tag;
    }

    @SuppressWarnings("unchecked")
    public static <T, N extends Tag, C extends Collection<T>> C collectionFromNbt(C collection, ListTag list, Function<N, T> deserializer, Class<N> nbtType) {
        collection.clear();
        for (Tag tag : list) {
            collection.add(deserializer.apply((N) tag));
        }
        return collection;
    }

    public static <T> ListTag arrayToNbt(T[] array, Function<T, Tag> serializer) {
        ListTag tag = new ListTag();
        for (T t : array) {
            tag.add(serializer.apply(t));
        }
        return tag;
    }

    @SuppressWarnings("unchecked")
    public static <T, N extends Tag> T[] arrayFromNbt(Function<Integer, T[]> inputArrayFactory, ListTag tag, Function<N, T> deserializer, Class<N> nbtType) {
        int len = tag.size();
        T[] out = inputArrayFactory.apply(len);
        int index = 0;
        for (Tag tag1 : tag) {
            out[index++] = deserializer.apply((N) tag1);
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public static <T, N extends Tag> T[] arrayFromNbt(T[] inputs, ListTag tag, BiFunction<T, N, T> deserializer, Class<N> nbtType) {
        int index = 0;
        for (Tag tag1 : tag) {
            inputs[index] = deserializer.apply(inputs[index], (N) tag1);
            ++index;
        }
        return inputs;
    }

    public static <K, V> CompoundTag mapToNbt(Map<K, V> map, Function<K, String> keySerializer, Function<V, Tag> valueSerializer) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            String key = keySerializer.apply(entry.getKey());
            Tag value = valueSerializer.apply(entry.getValue());
            tag.put(key, value);
        }
        return tag;
    }

    @SuppressWarnings("unchecked")
    public static <K, V, M extends Map<K, V>, N extends Tag> M mapFromNbt(M map, CompoundTag tag, Function<String, K> keyDeserializer, Function<N, V> valueDeserializer, Class<N> nbtType) {
        map.clear();
        Set<String> keys = tag.getAllKeys();
        for (String keyString : keys) {
            K key = keyDeserializer.apply(keyString);
            V value = valueDeserializer.apply((N) tag.get(keyString));
            map.put(key, value);
        }
        return map;
    }
}
