package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
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

import java.util.Optional;

public class PanBlockEntity extends RecipeRememberingBlockEntity<PanRecipe> implements Synchronizable {

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
        saveSharedData(compoundTag);
    }

    @Override
    public void decodeData(CompoundTag compoundTag) {
        loadSharedData(compoundTag);
    }

    private void saveSharedData(CompoundTag nbt) {
        nbt.putInt("oil", oilValue);
    }

    private void loadSharedData(CompoundTag nbt) {
        oilValue = nbt.getInt("oil");
    }

    public static final class PanCookingSlot extends CookingSlot<PanRecipe, PanBlockEntity> {

        public PanCookingSlot(int slotIndex, PanBlockEntity blockEntity) {
            super(slotIndex, blockEntity);
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
