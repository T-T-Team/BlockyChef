package tnt.blockychef.common.food.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import java.util.*;

public final class FluidContainer {

    private final List<FluidStack> fluids;
    private final int capacity;
    private final boolean allowMultipleTypes;

    public FluidContainer(int capacity, boolean allowMultipleTypes) {
        this.capacity = capacity;
        this.allowMultipleTypes = allowMultipleTypes;
        this.fluids = new ArrayList<>();
    }

    public List<FluidStack> getFluids() {
        return fluids;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean insert(FluidStack fluid) {
        if (!fluids.isEmpty()) {
            if (allowMultipleTypes) {
                return insertFluid(fluid);
            }
            if (isInsertable(fluid)) {
                return insertFluid(fluid);
            }
            return false;
        } else {
            return insertFluid(fluid);
        }
    }

    public boolean isInsertable(FluidStack stack) {
        if (fluids.isEmpty())
            return true;
        FluidStack first = fluids.get(0);
        return first.getFluid().getFluidType() == stack.getFluid().getFluidType();
    }

    public boolean extract(FluidStack fluidStack) {
        FluidType type = fluidStack.getFluid().getFluidType();
        int amount = fluidStack.getAmount();
        int stored = this.getStoredAmount(type);
        if (stored >= amount) {
            int result = stored - amount;
            fluids.removeIf(stack -> stack.getFluid().getFluidType() == type);
            if (result > 0) {
                fluids.add(new FluidStack(fluidStack.getFluid(), result));
            }
            flatten();
            return true;
        }
        return false;
    }

    public boolean hasFluid(FluidStack fluid) {
        int amount = getStoredAmount(fluid.getFluid().getFluidType());
        return amount >= fluid.getAmount();
    }

    public int getStoredAmount(FluidType type) {
        return fluids.stream().filter(stack -> stack.getFluid().getFluidType() == type).mapToInt(FluidStack::getAmount).sum();
    }

    public int getAmount() {
        return fluids.stream().mapToInt(FluidStack::getAmount).sum();
    }

    public float getFilledCapacityPercent(int amount) {
        return amount / (float) capacity;
    }

    public boolean isFull() {
        return getAmount() >= capacity;
    }

    private void flatten() {
        Map<FluidType, List<FluidStack>> map = new LinkedHashMap<>();
        for (FluidStack stack : fluids) {
            FluidType type = stack.getFluid().getFluidType();
            map.computeIfAbsent(type, k -> new ArrayList<>()).add(stack);
        }
        fluids.clear();
        for (List<FluidStack> list : map.values()) {
            FluidStack stack = FluidStack.EMPTY;
            for (FluidStack fluidStack : list) {
                if (stack.isEmpty()) {
                    stack = fluidStack.copy();
                } else {
                    stack.setAmount(stack.getAmount() + fluidStack.getAmount());
                }
            }
            if (!stack.isEmpty()) {
                fluids.add(stack);
            }
        }
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        fluids.forEach(fluid -> list.add(fluid.writeToNBT(new CompoundTag())));
        tag.put("fluids", list);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        fluids.clear();
        tag.getList("fluids", Tag.TAG_COMPOUND).forEach(fluidTag -> fluids.add(FluidStack.loadFluidStackFromNBT((CompoundTag) fluidTag)));
        flatten();
    }

    private boolean insertFluid(FluidStack fluid) {
        int limit = capacity - getAmount();
        if (limit <= 0) {
            return false;
        }
        int toStore = Math.min(limit, fluid.getAmount());
        FluidStack inserting = fluid.copy();
        inserting.setAmount(toStore);
        fluids.add(inserting);
        flatten();
        return fluid.isEmpty();
    }
}
