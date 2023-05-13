package tnt.blockychef.common.food.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import tnt.blockychef.common.registry.Registry;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public final class FluidContainer {

    private final Map<FluidType, Integer> fluids;
    private final int capacity;
    private final boolean allowMultipleTypes;

    public FluidContainer(int capacity, boolean allowMultipleTypes) {
        this.capacity = capacity;
        this.allowMultipleTypes = allowMultipleTypes;
        this.fluids = new TreeMap<>(Comparator.comparingInt(FluidType::fluidDensity));
    }

    public Map<FluidType, Integer> getFluids() {
        return fluids;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean insert(Fluid fluid) {
        if (!fluids.isEmpty()) {
            if (allowMultipleTypes) {
                return insertFluid(fluid);
            }
            if (fluids.containsKey(fluid.getFluidType())) {
                return insertFluid(fluid);
            }
            return false;
        } else {
            return insertFluid(fluid);
        }
    }

    public boolean extract(Fluid fluid) {
        FluidType type = fluid.getFluidType();
        int amount = fluid.getAmount();
        int stored = fluids.getOrDefault(type, 0);
        if (stored >= amount) {
            int result = stored - amount;
            if (result == 0) {
                fluids.remove(type);
            } else {
                fluids.put(type, result);
            }
            return true;
        }
        return false;
    }

    public boolean hasFluid(Fluid fluid) {
        int amount = fluids.getOrDefault(fluid.getFluidType(), 0);
        return amount >= fluid.getAmount();
    }

    public int getAmount() {
        return fluids.values().stream().reduce(0, Integer::sum);
    }

    public float getFilledCapacityPercent(int amount) {
        return amount / (float) capacity;
    }

    public boolean isFull() {
        return getAmount() >= capacity;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<FluidType, Integer> entry : fluids.entrySet()) {
            ResourceLocation id = Registry.FLUID.get().getKey(entry.getKey());
            tag.putInt(id.toString(), entry.getValue());
        }
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        fluids.clear();
        for (String key : tag.getAllKeys()) {
            FluidType type = Registry.FLUID.get().getValue(new ResourceLocation(key));
            if (type == null)
                continue;
            int amount = tag.getInt(key);
            fluids.put(type, amount);
        }
    }

    private boolean insertFluid(Fluid fluid) {
        int limit = capacity - getAmount();
        if (limit <= 0) {
            return false;
        }
        int stored = fluids.computeIfAbsent(fluid.getFluidType(), k -> 0);
        int toStore = Math.min(limit, fluid.getAmount());
        fluid.extract(toStore);
        fluids.put(fluid.getFluidType(), stored + toStore);
        return fluid.isEmpty();
    }
}
