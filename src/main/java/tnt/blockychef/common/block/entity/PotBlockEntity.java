package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.CookingStatus;
import tnt.blockychef.common.food.recipe.PotRecipe;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.heat.HeatValues;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.blockychef.common.init.BlockyChefTags;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.ArrayUtils;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import java.util.Arrays;
import java.util.Optional;

public class PotBlockEntity extends RecipeRememberingBlockEntity<PotRecipe> implements Synchronizable, ApplianceEventConsumer {

    public static final int STIR_EVENT_ID = 0;
    public static final int[] INPUTS = {0, 1, 2, 3, 4};
    public static final int[] WATER = {5};
    public static final int WATER_CAPACITY = 1000;

    private final PotCookingSlot[] slots;
    private int waterAmount;
    private float temperature;

    public PotBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.POT, pos, state);
        this.slots = ArrayUtils.indexedFill(new PotCookingSlot[INPUTS.length], index -> new PotCookingSlot(index, this));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PotBlockEntity pot) {
        HeatSource source = HeatHelper.getHeatSource(level, pos, Direction.DOWN);
        pot.temperature = HeatHelper.regulateHeat(pot.temperature, source.getHeat(Direction.UP), 0.01F);

        if (pot.canCook()) {
            for (PotCookingSlot slot : pot.slots) {
                slot.updateSlot(pot.waterAmount);
            }
            pot.evaporateWater(level);
            CookingStatus status = pot.getCookingStatus();
            if (status != CookingStatus.NONE && level.getGameTime() % 50L == 0L) {
                level.playSound(null, pos, BlockyChefSounds.POT, SoundSource.BLOCKS, 0.4F, 1.0F);
            }
        }
    }

    public CookingStatus getCookingStatus() {
        return getCookingStatus(slots, slot -> slot.cookingStatus);
    }

    @Override
    public void onEvent(Player eventOrigin, int eventId) {
        if (eventId == STIR_EVENT_ID) {
            for (PotCookingSlot slot : slots) {
                slot.stir();
            }
            BlockEntityHelper.sendBlockEntityClientData(this);
            setChanged();
        }
    }

    public boolean canCook() {
        return temperature > 0;
    }

    public void evaporateWater(Level level) {
        long time = level.getGameTime();
        boolean changed = false;
        for (PotCookingSlot slot : slots) {
            if (slot.shouldEvaporateWater(time)) {
                waterAmount = Math.max(0, waterAmount - 1);
                changed = true;
            }
        }
        if (changed) {
            setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    public PotCookingSlot[] getSlots() {
        return slots;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public void waterInputItemChanged(ItemStack stack) {
        if (stack.is(BlockyChefTags.Items.WATER) && waterAmount <= WATER_CAPACITY - 500) {
            waterAmount += 500;
            ItemStack returnItem = stack.getCraftingRemainingItem();
            if (!returnItem.isEmpty()) {
                setItem(WATER[0], returnItem);
            }
            setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    public void refreshSlot(int index) {
        if (index >= 0 && index < slots.length) {
            slots[index].loadRecipe(level.getRecipeManager());
        }
        setChanged();
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(INPUTS.length + WATER.length);
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

    @Override
    public void encodeData(CompoundTag compoundTag) {
        MenuInventoryHelper.encodeInventory(getItemHandler(), compoundTag);
        saveSharedData(compoundTag);
    }

    @Override
    public void decodeData(CompoundTag compoundTag) {
        MenuInventoryHelper.decodeInventory(getItemHandler(), compoundTag);
        loadSharedData(compoundTag);
    }

    private void saveSharedData(CompoundTag tag) {
        tag.putInt("water", waterAmount);
        tag.putFloat("temperature", temperature);
        ListTag slotNbt = new ListTag();
        Arrays.stream(slots).map(PotCookingSlot::serialize)
                .forEach(slotNbt::add);
        tag.put("slots", slotNbt);
    }

    private void loadSharedData(CompoundTag tag) {
        waterAmount = tag.getInt("water");
        temperature = tag.getFloat("temperature");
        ListTag slotsNbt = tag.getList("slots", Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(slots.length, slotsNbt.size()); i++) {
            slots[i].deserialize(slotsNbt.getCompound(i));
        }
    }

    public final class PotCookingSlot extends CookingSlot<PotRecipe, PotBlockEntity> {

        private CookingStatus cookingStatus = CookingStatus.NONE;

        public PotCookingSlot(int slotIndex, PotBlockEntity blockEntity) {
            super(slotIndex, blockEntity);
        }

        public boolean isLocked() {
            return PotBlockEntity.this.canCook() && recipe != null && !recipe.value().isOvercooked() && BlockyChef.config.cooking.lockCookingSlots;
        }

        public boolean shouldEvaporateWater(long gameTime) {
            if (recipe == null || cookingStatus == CookingStatus.NONE)
                return false;
            PotRecipe.PotCookingConfiguration configuration = recipe.value().getConfiguration();
            if (configuration.isCooking(PotBlockEntity.this.temperature)) {
                long consumeInterval = configuration.waterEvaporationRate();
                return gameTime % consumeInterval == 0L;
            }
            return false;
        }

        public void stir() {
            if (recipe == null)
                return;
            PotRecipe potRecipe = recipe.value();
            PotRecipe.PotCookingConfiguration configuration = potRecipe.getConfiguration();
            burnAmount = Math.max(0, burnAmount - configuration.stirBurnLoss());
            if (!potRecipe.isOvercooked()) {
                progressionTimer = Math.max(0, progressionTimer - configuration.stirProgressLoss());
            }
        }

        public void updateSlot(int waterAmount) {
            cookingStatus = CookingStatus.NONE;
            ItemStack stack = getItem();
            if (stack.isEmpty() || recipe == null) {
                burnAmount = 0.0F;
                progressionTimer = 0;
                return;
            }
            PotRecipe.PotCookingConfiguration configuration = recipe.value().getConfiguration();
            float temperature = PotBlockEntity.this.temperature;
            if (configuration.isCooking(temperature)) {
                cookingStatus = CookingStatus.COOKING;
                int requiredWaterLevel = configuration.minWaterLevel();
                float burnScale = 0.0F;
                if (configuration.isBurning(temperature) && !recipe.value().isOvercooked()) {
                    cookingStatus = CookingStatus.BURNING;
                    if (configuration.withinMinMaxTemperature(temperature)) {
                        burnScale = 0.01F * configuration.burnSpeed();
                    } else if (configuration.overMaxTemperature(temperature)) {
                        burnScale = 0.01F + HeatHelper.burn(temperature, configuration.maxTemperature(), configuration.burnSpeed());
                    }
                } else if (waterAmount < requiredWaterLevel) {
                    cookingStatus = CookingStatus.BURNING;
                    burnScale += HeatHelper.burn(HeatValues.MAX_TEMPERATURE, 0.0F, 0.2F);
                }

                if ((burnAmount += burnScale) >= 1.0F) {
                    ItemStack burnResult = recipe.value().getBurntResult().copy();
                    PotBlockEntity.this.setItem(getSlotIndex(), burnResult);
                    loadRecipe(PotBlockEntity.this.level.getRecipeManager());
                    return;
                }

                if (++progressionTimer >= totalTimer) {
                    ItemStack result = recipe.value().getResult().copy();
                    PotBlockEntity pot = PotBlockEntity.this;
                    pot.setItem(getSlotIndex(), result);
                    pot.storeRecipe(recipe);
                    loadRecipe(pot.level.getRecipeManager());
                }
            }
        }

        @Override
        public Optional<RecipeHolder<PotRecipe>> getRecipe(RecipeManager manager, ItemStack input) {
            return Helper.findRecipeFor(manager, getRecipeType(), t -> t.value().matches(input));
        }

        @Override
        protected void recipeLoaded(RecipeHolder<PotRecipe> recipe, boolean updated) {
            this.totalTimer = recipe.value().getConfiguration().time();
        }

        @Override
        public RecipeType<PotRecipe> getRecipeType() {
            return BlockyChefRecipeTypes.POT_RECIPE;
        }
    }
}
