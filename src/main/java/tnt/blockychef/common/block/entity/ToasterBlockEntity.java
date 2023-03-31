package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import tnt.blockychef.util.MenuInventoryHelper;

import java.util.Arrays;
import java.util.Optional;

public class ToasterBlockEntity extends RecipeRemberingBlockEntity<ToasterRecipe> implements SynchronizableBlockEntity, IndexedColorHolder {

    private static final int[] SLOTS = { 0, 1 };
    private final NonNullList<ToastingUnit> units;
    private int[] colors;
    private boolean toasting;
    private int timeToasting;
    private int targetToastingTime = 600;

    public ToasterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.TOASTER, pos, state);
        this.colors = new int[1];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
        units = NonNullList.createWithCapacity(SLOTS.length);
        for (int slot : SLOTS) {
            units.add(new ToastingUnit(this, slot));
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
                    isChanged = true;
                    unit.cancel();
                }
            }
            if (!hasActiveUnit) {
                isChanged = true;
                toaster.toasting = false;
                toaster.timeToasting = 0;
            } else if (++toaster.timeToasting >= toaster.targetToastingTime) {
                MenuInventoryHelper.dropInventoryContents(level, pos, toaster.getItemHandler());
                isChanged = true;
                toaster.units.forEach(ToastingUnit::cancel);
                toaster.toasting = false;
                toaster.timeToasting = 0;
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
        ListTag unitData = new ListTag();
        for (ToastingUnit unit : units) {
            unitData.add(unit.serializeData());
        }
        tag.put("unitData", unitData);
        tag.putBoolean("toasting", toasting);
        tag.putInt("timeToasting", timeToasting);
        tag.putInt("targetToastingTime", targetToastingTime);
    }

    @Override
    public void decodeBlockEntityData(CompoundTag tag) {
        colors = tag.getIntArray("colors");
        toasting = tag.getBoolean("toasting");
        timeToasting = tag.getInt("timeToasting");
        targetToastingTime = tag.getInt("targetToastingTime");
        ListTag unitDataTag = tag.getList("unitData", Tag.TAG_COMPOUND);
        for (int i = 0; i < unitDataTag.size(); i++) {
            CompoundTag unitTag = unitDataTag.getCompound(i);
            if (i >= units.size())
                break;
            units.get(i).deserializeData(unitTag);
        }
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

    public Optional<ToasterRecipe> getRecipe(ItemStack stack) {
        if (level == null)
            return Optional.empty();
        RecipeManager manager = level.getRecipeManager();
        return Helper.findRecipeFor(manager, BlockyChefRecipeTypes.TOASTER_RECIPE, recipe -> recipe.matches(stack));
    }

    static final class ToastingUnit {

        private final ToasterBlockEntity toaster;
        private final int slot;
        private int time;

        ToastingUnit(ToasterBlockEntity toaster, int slot) {
            this.toaster = toaster;
            this.slot = slot;
        }

        boolean hasValidItem() {
            return getRecipe().isPresent();
        }

        void cancel() {
            time = 0;
        }

        boolean toast() {
            Optional<ToasterRecipe> optional = getRecipe();
            if (optional.isPresent()) {
                ToasterRecipe recipe = optional.get();
                int limit = recipe.getToastingTime();
                if (++time >= limit) {
                    ItemStack result = recipe.assemble(toaster, toaster.getLevel().registryAccess());
                    toaster.setItem(slot, result);
                    return true;
                }
            }
            return false;
        }

        Optional<ToasterRecipe> getRecipe() {
            ItemStack stack = toaster.getItem(slot);
            return toaster.getRecipe(stack);
        }

        CompoundTag serializeData() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("time", time);
            return tag;
        }

        void deserializeData(CompoundTag tag) {
            time = tag.getInt("time");
        }
    }
}
