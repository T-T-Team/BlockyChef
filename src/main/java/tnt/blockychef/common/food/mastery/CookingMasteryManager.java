package tnt.blockychef.common.food.mastery;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.blockychef.BlockyChef;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CookingMasteryManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    private static final Marker MARKER = MarkerManager.getMarker("CookingMasteryManager");
    private final Map<Item, CookingMastery> masteryMap = new IdentityHashMap<>();

    public CookingMasteryManager() {
        super(GSON, "cooking_mastery");
    }

    public Optional<CookingMastery> getMastery(Item item) {
        return Optional.ofNullable(masteryMap.get(item));
    }

    public void loadFromNetwork(List<CookingMastery> list) {
        this.masteryMap.clear();
        list.forEach(mastery -> masteryMap.put(mastery.item(), mastery));
    }

    public List<CookingMastery> getFullMasteryList() {
        return ImmutableList.copyOf(masteryMap.values());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
        masteryMap.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement element = entry.getValue();
            try {
                DataResult<CookingMastery> dataResult = CookingMastery.CODEC.parse(JsonOps.INSTANCE, element);
                dataResult.resultOrPartial(err -> BlockyChef.LOGGER.error(MARKER, "Error loading {} mastery: {}", id, err)).ifPresent(mastery -> {
                    Item item = mastery.item();
                    if (item == Items.AIR) {
                        throw new JsonSyntaxException("Mastery file " + id + " contains invalid item ID");
                    }
                    if (masteryMap.put(item, mastery) != null) {
                        BlockyChef.LOGGER.warn(MARKER, "Detected mastery override for item: " + ForgeRegistries.ITEMS.getKey(item));
                    }
                });
            } catch (JsonParseException e) {
                BlockyChef.LOGGER.error(MARKER, "Unable to load '" + id + "' mastery file due to error", e);
            }
        }
    }
}
