package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.CookingStatus;
import tnt.blockychef.common.food.recipe.ToasterRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Optional;

public class ToasterBlockEntity extends RecipeRememberingBlockEntity<ToasterRecipe> implements Synchronizable, IndexedColorHolder {

    public static final int DEFAULT_TIMER_INCREMENT = 100;
    public static final int MIN_TIMER_VALUE = 100; // 5 seconds
    public static final int MAX_TIMER_VALUE = 6000; // 5 minutes
    private static final int[] SLOTS = { 0, 1 };
    private final NonNullList<ToastingUnit> units;
    private final Integer[] colors;
    private boolean toasting;
    private int timeToasting;
    private int targetToastingTime = 600;

    public ToasterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.TOASTER, pos, state);
        this.colors = new Integer[1];
        units = NonNullList.createWithCapacity(SLOTS.length);
        for (int slot : SLOTS) {
            units.add(new ToastingUnit(this, slot));
        }
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, ToasterBlockEntity toaster) {
        if (toaster.toasting) {
            boolean isChanged = false;
            for (ToastingUnit unit : toaster.units) {
                if (unit.hasValidItem()) {
                    if (unit.toast()) {
                        isChanged = true;
                    }
                } else {
                    isChanged = true;
                    unit.cancel();
                }
            }
            if (++toaster.timeToasting >= toaster.targetToastingTime) {
                isChanged = true;
                toaster.units.forEach(ToastingUnit::cancel);
                toaster.setToasting(false);
                toaster.timeToasting = 0;
            }
            if (isChanged) {
                BlockEntityHelper.sendBlockEntityClientData(toaster);
            }
        }
    }

    public CookingStatus getCookingStatus() {
        return getCookingStatus(units.toArray(ToastingUnit[]::new), unit -> unit.status);
    }

    public void setToasting(boolean toasting) {
        if (toasting != this.toasting) {
            SoundEvent event = toasting ? BlockyChefSounds.TOASTER_B : BlockyChefSounds.TOASTER_A;
            level.playSound(null, worldPosition, event, SoundSource.BLOCKS);
        }
        this.toasting = toasting;
        setChanged();
    }

    public boolean isToasting() {
        return toasting;
    }

    public void setToastingTimer(int time) {
        if (toasting) {
            return;
        }
        this.targetToastingTime = Mth.clamp(time, MIN_TIMER_VALUE, MAX_TIMER_VALUE);
        setChanged();
    }

    public int getToastingTimer() {
        return targetToastingTime;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(SLOTS.length) {
            @Override
            protected void onContentsChanged(int slot) {
                ToasterBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    public @Nullable Integer getColor(int index) {
        return index >= 0 && index < colors.length ? colors[index] : null;
    }

    @Override
    public void setColor(int index, @Nullable Integer color) {
        if (index >= 0 && index < colors.length) {
            colors[index] = color;
            this.setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    @Override
    public void encodeData(CompoundTag tag) {
        MenuInventoryHelper.encodeInventory(getItemHandler(), tag);
        saveSharedData(tag);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        MenuInventoryHelper.decodeInventory(getItemHandler(), tag);
        loadSharedData(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveSharedData(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadSharedData(tag);
    }

    public Optional<ToasterRecipe> getRecipe(ItemStack stack) {
        if (level == null)
            return Optional.empty();
        RecipeManager manager = level.getRecipeManager();
        return Helper.findRecipeFor(manager, BlockyChefRecipeTypes.TOASTER_RECIPE, recipe -> recipe.matches(stack));
    }

    private void saveSharedData(CompoundTag tag) {
        ColorableBlockEntity.saveColorData(colors, tag);
        tag.putBoolean("toasting", toasting);
        tag.putInt("timeToasting", timeToasting);
        tag.putInt("targetToastingTime", targetToastingTime);
        ListTag unitData = new ListTag();
        units.forEach(unit -> unitData.add(unit.serializeData()));
        tag.put("unitData", unitData);
    }

    private void loadSharedData(CompoundTag tag) {
        ColorableBlockEntity.loadColorData(colors, tag);
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

    static final class ToastingUnit {

        private final ToasterBlockEntity toaster;
        private final int slot;
        private int time;
        private CookingStatus status = CookingStatus.NONE;

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
            status = CookingStatus.NONE;
            Optional<ToasterRecipe> optional = getRecipe();
            if (optional.isPresent()) {
                ToasterRecipe holder = optional.get();
                int limit = holder.getToastingTime();
                status = holder.isOvercooked() ? CookingStatus.BURNING : CookingStatus.COOKING;
                if (++time >= limit) {
                    ItemStack result = holder.assemble(toaster, toaster.getLevel().registryAccess());
                    toaster.setItem(slot, result);
                    toaster.storeRecipe(holder);
                    time = 0;
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
