package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import tnt.blockychef.common.food.recipe.BaseCookConfiguration;
import tnt.blockychef.common.food.recipe.PanRecipe;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefTags;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.ArrayUtils;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import java.util.Arrays;
import java.util.Optional;

public class PanBlockEntity extends RecipeRememberingBlockEntity<PanRecipe> implements Synchronizable, ApplianceEventConsumer {

    public static final int STIR_EVENT_ID = 0;
    public static final int OIL_BUFFER_SIZE = 1000;
    public static final int[] INPUTS = {0, 1, 2, 3, 4};
    public static final int[] OIL = {5};

    private final PanCookingSlot[] slots;
    private int oilValue;
    private float temperature;

    public PanBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.PAN, pos, state);
        this.slots = ArrayUtils.indexedFill(new PanCookingSlot[INPUTS.length], index -> new PanCookingSlot(index, this));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PanBlockEntity pan) {
        HeatSource source = HeatHelper.getHeatSource(level, pos, Direction.DOWN);
        pan.temperature = HeatHelper.regulateHeat(pan.temperature, source.getHeat(Direction.UP), 0.01F);

        if (pan.canCook()) {
            for (PanCookingSlot slot : pan.slots) {
                slot.updateSlot(pan.oilValue > 0);
            }
            pan.consumeOil(level);
        }
    }

    @Override
    public void onEvent(Player eventOrigin, int eventId) {
        if (eventId == STIR_EVENT_ID) {
            for (PanCookingSlot slot : slots) {
                slot.stir();
            }
            setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    public boolean canCook() {
        return temperature > 0;
    }

    public void consumeOil(Level level) {
        long gameTime = level.getGameTime();
        boolean oilChanged = false;
        for (PanCookingSlot slot : slots) {
            if (slot.shouldConsumeOil(gameTime)) {
                oilValue = Math.max(0, oilValue - 1);
                oilChanged = true;
            }
        }
        if (oilChanged) {
            setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    public PanCookingSlot[] getSlots() {
        return slots;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getOil() {
        return oilValue;
    }

    public void oilItemChanged(ItemStack stack) {
        if (stack.is(BlockyChefTags.Items.OIL) && oilValue < OIL_BUFFER_SIZE) {
            oilValue += 500;
            ItemStack returnItem = stack.getCraftingRemainingItem();
            if (!returnItem.isEmpty()) {
                setItem(OIL[0], returnItem);
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
        return new ItemStackHandler(6);
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

    private void saveSharedData(CompoundTag nbt) {
        nbt.putInt("oil", oilValue);
        nbt.putFloat("temperature", temperature);
        ListTag slotNbt = new ListTag();
        Arrays.stream(slots).map(PanCookingSlot::serialize)
                .forEach(slotNbt::add);
        nbt.put("slots", slotNbt);
    }

    private void loadSharedData(CompoundTag nbt) {
        oilValue = nbt.getInt("oil");
        temperature = nbt.getFloat("temperature");
        ListTag slotsNbt = nbt.getList("slots", Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(slots.length, slotsNbt.size()); i++) {
            slots[i].deserialize(slotsNbt.getCompound(i));
        }
    }

    public final class PanCookingSlot extends CookingSlot<PanRecipe, PanBlockEntity> {

        public PanCookingSlot(int slotIndex, PanBlockEntity blockEntity) {
            super(slotIndex, blockEntity);
        }

        @Override
        protected void recipeLoaded(RecipeHolder<PanRecipe> recipe, boolean updated) {
            this.totalTimer = recipe.value().getConfiguration().time();
        }

        public void stir() {
            if (recipe == null)
                return;
            PanRecipe panRecipe = recipe.value();
            PanRecipe.PanCookingConfiguration configuration = panRecipe.getConfiguration();
            this.burnAmount = Math.max(0, burnAmount - configuration.stirBurnLoss());
            if (!panRecipe.isBurning()) {
                this.progressionTimer = Math.max(0, progressionTimer - configuration.stirProgressLoss());
            }
        }

        public boolean shouldConsumeOil(long gameTime) {
            if (recipe == null)
                return false;
            PanRecipe.PanCookingConfiguration configuration = recipe.value().getConfiguration();
            if (configuration.isCooking(PanBlockEntity.this.temperature)) {
                long consumeInterval = configuration.oilConsumptionRate();
                return gameTime % consumeInterval == 0L;
            }
            return false;
        }

        public boolean isLocked() {
            return PanBlockEntity.this.canCook() && recipe != null && !recipe.value().isBurning() && BlockyChef.config.cooking.lockCookingSlots;
        }

        public void updateSlot(boolean hasOil) {
            ItemStack stack = getItem();
            if (stack.isEmpty() || recipe == null) {
                burnAmount = 0.0F;
                progressionTimer = 0;
                return;
            }
            BaseCookConfiguration configuration = recipe.value().getConfiguration();
            float temperature = PanBlockEntity.this.temperature;
            if (configuration.isCooking(temperature)) {
                float burnScale = 0.0F;
                if (configuration.isBurning(temperature)) {
                    float diff = temperature - configuration.maxTemperature();
                    burnScale = diff * (0.015F * configuration.burnSpeed());
                } else if (!hasOil) {
                    burnScale += (0.015F * 5);
                }

                if ((burnAmount += burnScale) >= 1.0F) {
                    ItemStack burnResult = recipe.value().getBurntResult().copy();
                    PanBlockEntity.this.setItem(getSlotIndex(), burnResult);
                    loadRecipe(PanBlockEntity.this.level.getRecipeManager());
                    return;
                }

                if (++progressionTimer >= totalTimer) {
                    ItemStack result = recipe.value().getResult().copy();
                    PanBlockEntity pan = PanBlockEntity.this;
                    pan.setItem(getSlotIndex(), result);
                    pan.storeRecipe(recipe);
                    loadRecipe(pan.level.getRecipeManager());
                }
            }
        }

        @Override
        public RecipeType<PanRecipe> getRecipeType() {
            return BlockyChefRecipeTypes.PAN_RECIPE;
        }

        @Override
        public Optional<RecipeHolder<PanRecipe>> getRecipe(RecipeManager manager, ItemStack input) {
            return Helper.findRecipeFor(manager, getRecipeType(), t -> t.value().matches(input));
        }
    }
}
