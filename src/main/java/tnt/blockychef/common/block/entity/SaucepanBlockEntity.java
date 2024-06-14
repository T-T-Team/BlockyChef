package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.CookingStatus;
import tnt.blockychef.common.food.recipe.SaucepanRecipe;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import java.util.List;
import java.util.Optional;

public class SaucepanBlockEntity extends RecipeRememberingBlockEntity<SaucepanRecipe> implements Synchronizable, ProcessableRecipeHolder, ApplianceEventConsumer {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};
    public static final int[] OUTPUTS = {6, 7, 8, 9};
    public static final int STIR_EVENT_ID = 0;

    private RecipeHolder<SaucepanRecipe> recipeHolder;
    private boolean cooking;
    private int timeCooking;
    private float temperature;
    private float burnAmount;
    private CookingStatus status = CookingStatus.NONE;

    public SaucepanBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.SAUCEPAN, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SaucepanBlockEntity saucepan) {
        saucepan.status = CookingStatus.NONE;
        HeatSource source = HeatHelper.getHeatSource(level, pos, Direction.DOWN);
        saucepan.temperature = HeatHelper.regulateHeat(saucepan.temperature, source.getHeat(Direction.UP), 0.01F);

        if (saucepan.recipeHolder == null) {
            saucepan.setRecipe(null);
            return;
        }

        if (!saucepan.cooking)
            return;

        SaucepanRecipe recipe = saucepan.recipeHolder.value();
        List<ItemStack> outputs = recipe.getOutputs();
        if (!MenuInventoryHelper.canFitItems(outputs.toArray(ItemStack[]::new), saucepan, OUTPUTS)) {
            saucepan.setRecipe(null);
            return;
        }

        SaucepanRecipe.SaucePanCookingConfiguration configuration = recipe.getConfiguration();
        if (configuration.isCooking(saucepan.temperature)) {
            saucepan.status = CookingStatus.COOKING;
            if (canPlaySound(40, saucepan.timeCooking, configuration.time())) {
                level.playSound(null, pos, BlockyChefSounds.SAUCEPAN, SoundSource.BLOCKS, 0.4F, 1.0F);
            }
            if (++saucepan.timeCooking >= configuration.time() && !level.isClientSide()) {
                saucepan.timeCooking = 0;
                saucepan.consumeIngredientsAndApplyCraftRemainder(recipe, INPUTS, OUTPUTS, in -> recipe.getInputs().forEach(ing -> ing.consume(saucepan, in)));
                ItemStack[] assembledOutput = outputs.stream().map(ItemStack::copy).toArray(ItemStack[]::new);
                MenuInventoryHelper.insertItems(assembledOutput, saucepan, OUTPUTS);
                saucepan.storeRecipe(saucepan.recipeHolder);
                saucepan.reloadRecipe();
                saucepan.burnAmount = 0.0F;
                BlockEntityHelper.sendBlockEntityClientData(saucepan);
                return;
            }
            if (configuration.isBurning(saucepan.temperature) && !saucepan.recipeHolder.value().isOvercooked()) {
                saucepan.status = CookingStatus.BURNING;
                float f = 0.0F;
                if (configuration.withinMinMaxTemperature(saucepan.temperature)) {
                    f = 0.01F * configuration.burnSpeed();
                } else if (configuration.overMaxTemperature(saucepan.temperature)) {
                    f = 0.01F + HeatHelper.burn(saucepan.temperature, configuration.maxTemperature(), configuration.burnSpeed());
                }
                saucepan.burnAmount += f;
                if (saucepan.burnAmount >= 1.0F) {
                    saucepan.consumeInputs();
                    saucepan.setRecipe(null);
                    ItemStack[] burned = recipe.getBurnOutputs().stream().map(ItemStack::copy).toArray(ItemStack[]::new);
                    if (!MenuInventoryHelper.canFitItems(burned, saucepan, OUTPUTS)) {
                        for (ItemStack burnedItemStack : burned) {
                            Containers.dropItemStack(level, saucepan.worldPosition.getX(), saucepan.worldPosition.getY(), saucepan.worldPosition.getZ(), burnedItemStack);
                        }
                    } else {
                        MenuInventoryHelper.insertItems(burned, saucepan, OUTPUTS);
                    }
                }
            } else if (recipe.isOvercooked()) {
                saucepan.status = CookingStatus.BURNING;
            }
        }
    }

    public CookingStatus getCookingStatus() {
        return status;
    }

    public float getBurnAmount() {
        return burnAmount;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getProgress() {
        if (recipeHolder == null || recipeHolder.value().isOvercooked()) {
            return 0.0F;
        }
        return timeCooking / (float) recipeHolder.value().getConfiguration().time();
    }

    @Override
    public void onEvent(Player eventOrigin, int eventId) {
        switch (eventId) {
            case STIR_EVENT_ID:
                if (recipeHolder == null)
                    return;
                SaucepanRecipe recipe = recipeHolder.value();
                SaucepanRecipe.SaucePanCookingConfiguration cfg = recipe.getConfiguration();
                burnAmount = Math.max(0, burnAmount - cfg.stirBurnLoss());
                if (!recipe.isOvercooked()) {
                    timeCooking = Math.max(0, timeCooking - cfg.stirProgressLoss());
                }
                BlockEntityHelper.sendBlockEntityClientData(this);
                setChanged();
                break;
        }
    }

    public boolean canCook() {
        return !cooking && recipeHolder != null;
    }

    public boolean canStir() {
        return cooking && recipeHolder != null && burnAmount > 0.2F;
    }

    @Override
    public void startProcessing() {
        if (cooking)
            return;
        reloadRecipe();
        if (recipeHolder == null)
            return;
        cooking = true;
        timeCooking = 0;
        burnAmount = 0.0F;
        BlockEntityHelper.sendBlockEntityClientData(this);
        setChanged();
    }

    public void reloadRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<RecipeHolder<SaucepanRecipe>> optional = manager.getRecipeFor(BlockyChefRecipeTypes.SAUCEPAN_RECIPE, this, level);
        this.setRecipe(optional.orElse(null));
    }

    public void setRecipe(RecipeHolder<SaucepanRecipe> recipeHolder) {
        if (this.recipeHolder != recipeHolder) {
            this.recipeHolder = recipeHolder;
            this.cooking = false;
            this.timeCooking = 0;
            this.burnAmount = 0.0F;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
        setChanged();
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(INPUTS.length + OUTPUTS.length);
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
        tag.putBoolean("cooking", cooking);
        tag.putInt("time", timeCooking);
        tag.putFloat("temperature", temperature);
        tag.putFloat("burnAmount", burnAmount);
    }

    private void loadSharedData(CompoundTag tag) {
        cooking = tag.getBoolean("cooking");
        timeCooking = tag.getInt("time");
        temperature = tag.getFloat("temperature");
        burnAmount = tag.getFloat("burnAmount");
        reloadRecipe();
    }

    private void consumeInputs() {
        if (recipeHolder != null) {
            recipeHolder.value().getInputs().forEach(input -> input.consume(this, INPUTS));
        }
    }
}
