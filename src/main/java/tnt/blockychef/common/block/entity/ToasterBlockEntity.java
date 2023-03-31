package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.ToasterRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ToasterBlockEntity extends RecipeRemberingBlockEntity<ToasterRecipe> implements SynchronizableBlockEntity, IndexedColorHolder {

    private static final int[] SLOTS = { 0, 1 };
    private final NonNullList<ToastingUnit> units = NonNullList.createWithCapacity(SLOTS.length);
    private int[] colors;
    private boolean toasting;
    private int timeToasting;
    private int targetToastingTime = 600;

    public ToasterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.TOASTER, pos, state);
        this.colors = new int[1];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
        for (int slot : SLOTS) {
            units.set(slot, new ToastingUnit(this, slot));
        }
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, ToasterBlockEntity toaster) {
        if (toaster.toasting) {
            boolean hasActiveUnit = false;
            boolean isChanged = false;
            for (ToastingUnit unit : toaster.units) {
                if (unit.hasValidItem()) {
                    if (unit.toast()) {
                        isChanged = true;
                    }
                    hasActiveUnit = true;
                } else {
                    unit.cancel();
                }
            }
            if (!hasActiveUnit) {
                isChanged = true;
                toaster.toasting = false;
                toaster.timeToasting = 0;
            } else if (++toaster.timeToasting >= toaster.targetToastingTime) {
                // TODO eject items
                isChanged = true;
                toaster.units.forEach(ToastingUnit::cancel);
            }
            if (isChanged) {
                Helper.sendBlockEntityClientData(toaster);
            }
        }
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(SLOTS.length);
    }

    @Override
    public int getColor(int index) {
        return index >= 0 && index < colors.length ? colors[index] : Integer.MIN_VALUE;
    }

    @Override
    public void setColor(int index, int color) {
        if (index >= 0 && index < colors.length) {
            colors[index] = color;
            this.setChanged();
            Helper.sendBlockEntityClientData(this);
        }
    }

    @Override
    public void encodeBlockEntityData(CompoundTag tag) {
        tag.putIntArray("colors", colors);
    }

    @Override
    public void decodeBlockEntityData(CompoundTag tag) {
        colors = tag.getIntArray("colors");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        encodeBlockEntityData(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        decodeBlockEntityData(tag);
    }

    private Optional<ToasterRecipe> getRecipe(ItemStack stack) {
        if (level == null)
            return Optional.empty();
        RecipeManager manager = level.getRecipeManager();
        return manager.getRecipeFor(BlockyChefRecipeTypes.TOASTER_RECIPE, this, level);
    }

    static final class ToastingUnit {

        private final ToasterBlockEntity toaster;
        private final int slot;
        private int time;
        private ToastingStatus status = ToastingStatus.RAW;

        ToastingUnit(ToasterBlockEntity toaster, int slot) {
            this.toaster = toaster;
            this.slot = slot;
        }

        boolean hasValidItem() {
            ItemStack stack = ItemStack.EMPTY;
            return true;
        }

        void cancel() {

        }

        boolean toast() {
            ++time;
            return false;
        }
    }

    private enum ToastingStatus {
        RAW, TOASTED, BURNT
    }
}
