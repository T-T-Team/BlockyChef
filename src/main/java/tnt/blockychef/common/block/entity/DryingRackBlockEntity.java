package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.food.recipe.DryingRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.ArrayUtils;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;
import tnt.tntlib.api.serialization.NbtUtil;

import java.util.List;
import java.util.Optional;

public class DryingRackBlockEntity extends RecipeRememberingBlockEntity<DryingRecipe> implements Synchronizable {

    public static final int DRYING_CAPACITY = 3;
    private final DryingSlot[] slots;

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.DRYING_RACK, pos, state);
        this.slots = ArrayUtils.indexedFill(new DryingSlot[DRYING_CAPACITY], DryingSlot::new);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(DRYING_CAPACITY);
    }

    public boolean isValidInput(ItemStack stack, Level level) {
        RecipeManager manager = level.getRecipeManager();
        List<DryingRecipe> recipeList = manager.getAllRecipesFor(BlockyChefRecipeTypes.DRYING_RECIPE);
        if (stack.isEmpty())
            return false;
        for (DryingRecipe dryingRecipeHolder : recipeList) {
            if (dryingRecipeHolder.isValidInput(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasItem(int slot) {
        return slot >= 0 && slot < DRYING_CAPACITY && !getItem(slot).isEmpty();
    }

    public void takeOut(@Nullable Player player, int slot) {
        if (level.isClientSide)
            return;
        if (slot < 0 || slot >= DRYING_CAPACITY)
            return;
        DryingSlot dryingSlot = slots[slot];
        ItemStack itemStack = this.getItem(slot);
        if (itemStack.isEmpty())
            return;
        if (player == null) {
            Vec3 vec3 = Vec3.atCenterOf(worldPosition);
            getRecipesToAwardAndPopExperience((ServerLevel) level, vec3);
            Containers.dropItemStack(level, vec3.x, vec3.y, vec3.z, itemStack.copy());
        } else {
            MenuInventoryHelper.giveItemOrDrop(player, itemStack.copy());
            awardUsedRecipesAndPopExperience((ServerPlayer) player);
        }
        CookingMastery.applyMastery(player, itemStack);
        this.setItem(slot, ItemStack.EMPTY);
        dryingSlot.refresh(level);
        BlockEntityHelper.sendBlockEntityClientData(this);
    }

    public DryingSlot getSlot(int slotIndex) {
        return slots[slotIndex];
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DryingRackBlockEntity dryingRack) {
        for (DryingSlot slot : dryingRack.slots) {
            if (slot.requireRefresh) {
                slot.refresh(level);
            }
            slot.update();
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        super.setItem(index, stack);
        if (index >= 0 && index < DRYING_CAPACITY) {
            slots[index].refresh(level);
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
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
    public void encodeData(CompoundTag tag) {
        MenuInventoryHelper.encodeInventory(getItemHandler(), tag);
        saveSharedData(tag);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        MenuInventoryHelper.decodeInventory(getItemHandler(), tag);
        loadSharedData(tag);
    }

    private void saveSharedData(CompoundTag tag) {
        tag.put("slots", NbtUtil.arrayToNbt(slots, DryingSlot::serialize));
    }

    private void loadSharedData(CompoundTag tag) {
        NbtUtil.arrayFromNbt(slots, tag.getList("slots", Tag.TAG_COMPOUND), (dryingSlot, tag1) -> {
            dryingSlot.deserialize(tag1);
            dryingSlot.requireRefresh = true;
            return dryingSlot;
        }, CompoundTag.class);
    }

    private void completedRecipe(DryingRecipe holder, int slotIndex) {
        ItemStack result = holder.getOutput().copy();
        this.setItem(slotIndex, result);
        this.storeRecipe(holder);

        DryingSlot slot = slots[slotIndex];
        slot.refresh(level);
        BlockEntityHelper.sendBlockEntityClientData(this);
    }

    public final class DryingSlot {

        private final int index;

        private int timeDrying;
        private DryingRecipe recipe;
        private boolean requireRefresh;

        public DryingSlot(int index) {
            this.index = index;
        }

        public boolean hasRecipe() {
            return recipe != null;
        }

        public int getTotalDryingTime() {
            return hasRecipe() ? recipe.getDryingTime() : 0;
        }

        public int getCurrentDryingTime() {
            return hasRecipe() ? timeDrying : 0;
        }

        public ItemStack getResult() {
            return hasRecipe() ? recipe.getOutput() : ItemStack.EMPTY;
        }

        void update() {
            if (recipe == null)
                return;

            int totalDryingTime = recipe.getDryingTime();
            if (++timeDrying < totalDryingTime)
                return;

            this.timeDrying = 0;
            DryingRackBlockEntity.this.completedRecipe(this.recipe, this.index);
        }

        CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("timeDrying", timeDrying);
            return tag;
        }

        void deserialize(CompoundTag tag) {
            this.timeDrying = tag.getInt("timeDrying");
        }

        void refresh(Level level) {
            this.requireRefresh = false;
            RecipeManager manager = level.getRecipeManager();

            Optional<DryingRecipe> optional = Helper.findRecipeFor(manager, BlockyChefRecipeTypes.DRYING_RECIPE, recipe -> recipe.isValidInput(this.getItemStack()));
            DryingRecipe recipeHolder = optional.orElse(null);
            if (this.recipe != recipeHolder) {
                this.timeDrying = 0;
                this.recipe = recipeHolder;
            }
        }

        ItemStack getItemStack() {
            return DryingRackBlockEntity.this.getItem(index);
        }
    }
}
