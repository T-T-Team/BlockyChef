package tnt.blockychef.common.data.fluids;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.BlockyChef;

import java.util.*;

public final class FluidExtractionManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    private final Collection<FluidExtractor> extractorList = new ArrayList<>();

    public FluidExtractionManager() {
        super(GSON, "fluid_extractors");
    }

    public FluidExtractor getExtractor(ItemStack stack, FluidHolder holder) {
        for (FluidExtractor extractor : extractorList) {
            if (extractor.matches(stack, holder)) {
                return extractor;
            }
        }
        return null;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceMap.entrySet()) {
            ResourceLocation location = entry.getKey();
            try {
                JsonElement element = entry.getValue();
                DataResult<FluidExtractor> dataResult = FluidExtractor.CODEC.parse(JsonOps.INSTANCE, element);
                Optional<FluidExtractor> opt = dataResult.resultOrPartial(BlockyChef.LOGGER::error);
                opt.ifPresent(extractorList::add);
            } catch (RuntimeException e) {
                BlockyChef.LOGGER.error("Error loading fluid extractor file {}: {}", location, e);
            }
        }
    }
}
