package tnt.blockychef.common.ai;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.ForgeEventFactory;
import tnt.blockychef.common.block.CropsBlock;
import tnt.blockychef.common.block.DecayingGrowingBlock;
import tnt.blockychef.common.init.BlockyChefBlocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ManageFarm extends Behavior<Villager> {

    private static final Set<Item> VILLAGER_WANTED_ITEMS = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS);
    private final List<BlockPos> validFarmFields = new ArrayList<>();
    private final List<BlockPos> toWeedFields = new ArrayList<>();
    private final List<BlockPos> unplantedFields = new ArrayList<>();
    private Action action;
    private long nextStartTime;
    private int workTime;

    public ManageFarm() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryStatus.VALUE_PRESENT), 200, 1000);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Villager villager) {
        if (!ForgeEventFactory.getMobGriefingEvent(level, villager)) {
            return false;
        }
        if (villager.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        }
        BlockPos.MutableBlockPos mutable = villager.blockPosition().mutable();
        this.toWeedFields.clear();
        this.validFarmFields.clear();
        this.unplantedFields.clear();
        int searchRange = 3;
        for(int x = -searchRange; x <= searchRange; ++x) {
            for(int y = -searchRange; y <= searchRange; ++y) {
                for(int z = -searchRange; z <= searchRange; ++z) {
                    mutable.set(villager.getX() + x, villager.getY() + y, villager.getZ() + z);
                    BlockPos pos = mutable.immutable();
                    BlockState state = level.getBlockState(pos);
                    Block plant = state.getBlock();
                    Block soil = level.getBlockState(pos.below()).getBlock();
                    if (soil instanceof FarmBlock) {
                        if (plant instanceof DecayingGrowingBlock block) {
                            int weedsLevel = state.getValue(block.getDecayProperty());
                            if (weedsLevel > 0) {
                                this.toWeedFields.add(pos);
                            } else if (plant instanceof CropsBlock cropsBlock) {
                                if (cropsBlock.isMaxAge(state)) {
                                    this.validFarmFields.add(pos);
                                }
                            } else if (plant == BlockyChefBlocks.WEEDS) {
                                this.unplantedFields.add(pos);
                            }
                        } else if (plant instanceof CropBlock cropBlock) { // Vanilla compat
                            if (cropBlock.isMaxAge(state)) {
                                this.validFarmFields.add(pos);
                            }
                        }
                    }
                }
            }
        }
        this.action = this.getAction(null, level.getRandom(), villager);
        return this.action != null;
    }

    @Override
    protected void start(ServerLevel level, Villager villager, long levelTime) {
        if (levelTime > this.nextStartTime && this.action != null) {
            Brain<?> brain = villager.getBrain();
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.action.pos()));
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.action.pos()), 0.5F, 1));
        }
    }

    @Override
    protected void stop(ServerLevel serverLevel, Villager villager, long levelTime) {
        Brain<?> brain = villager.getBrain();
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        this.nextStartTime = levelTime + 40L;
        this.workTime = 0;
    }

    @Override
    protected void tick(ServerLevel level, Villager villager, long levelTime) {
        if (this.action == null || this.action.pos().closerToCenterThan(villager.position(), 1)) {
            if (this.action != null && levelTime > this.nextStartTime) {
                BlockState blockstate = level.getBlockState(this.action.pos());
                Block plant = blockstate.getBlock();
                Block soil = level.getBlockState(this.action.pos().below()).getBlock();
                switch (this.action.type()) {
                    case WEED -> {
                        this.toWeedFields.remove(this.action.pos());
                        if (plant instanceof DecayingGrowingBlock block) {
                            int weedsAge = blockstate.getValue(block.getDecayProperty());
                            if (weedsAge > 0) {
                                DecayingGrowingBlock.trimWeeds(this.action.pos(), blockstate, level);
                            }
                        }
                        this.resetAction(level, villager, levelTime);
                    }
                    case PLANT -> {
                        ItemStack seeds = this.getAnySeeds(level, villager);
                        this.unplantedFields.remove(this.action.pos());
                        if (!seeds.isEmpty()) {
                            BlockState seedState = this.getSeedState(level, seeds);
                            if (seedState != null) {
                                level.setBlockAndUpdate(this.action.pos(), seedState);
                                level.gameEvent(GameEvent.BLOCK_PLACE, this.action.pos(), GameEvent.Context.of(villager, seedState));
                                level.playSound(null, this.action.pos().getX(), this.action.pos().getY(), this.action.pos().getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                                seeds.shrink(1);
                            }
                        }
                        this.resetAction(level, villager, levelTime);
                    }
                    case HARVEST -> {
                        if (this.isFullyGrown(plant, blockstate)) {
                            level.destroyBlock(this.action.pos(), true, villager);
                        }
                        blockstate = level.getBlockState(this.action.pos());
                        plant = blockstate.getBlock();
                        if ((blockstate.isAir() || blockstate.canBeReplaced()) && soil instanceof FarmBlock && !villager.getInventory().isEmpty()) {
                            ItemStack itemstack = this.getAnySeeds(level, villager);
                            if (!itemstack.isEmpty()) {
                                BlockState seedState = this.getSeedState(level, itemstack);
                                if (seedState != null) {
                                    level.setBlockAndUpdate(this.action.pos(), seedState);
                                    level.gameEvent(GameEvent.BLOCK_PLACE, this.action.pos(), GameEvent.Context.of(villager, seedState));
                                    level.playSound(null, this.action.pos().getX(), this.action.pos().getY(), this.action.pos().getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    itemstack.shrink(1);
                                }
                            }
                        }
                        if (!this.isFullyGrown(plant, blockstate)) {
                            this.validFarmFields.remove(this.action.pos());
                            this.resetAction(level, villager, levelTime);
                        }
                    }
                }
            }
            ++this.workTime;
        }
    }

    private ItemStack getAnySeeds(@Nullable ServerLevel level, Villager villager) {
        SimpleContainer container = villager.getInventory();
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (!itemStack.isEmpty()) {
                if (VILLAGER_WANTED_ITEMS.contains(itemStack.getItem())) {
                    stack = itemStack;
                    break;
                }
                if (itemStack.getItem() instanceof IPlantable plantable) {
                    if (level == null || plantable.getPlantType(level, this.action.pos()) == PlantType.CROP) {
                        stack = itemStack;
                        break;
                    }
                }
            }
        }
        return stack;
    }

    private BlockState getSeedState(ServerLevel level, ItemStack stack) {
        if (stack.is(Items.WHEAT_SEEDS)) {
            return BlockyChefBlocks.WHEAT_CROPS.defaultBlockState();
        }
        if (stack.is(Items.POTATO)) {
            return BlockyChefBlocks.POTATO_CROPS.defaultBlockState();
        }
        if (stack.is(Items.CARROT)) {
            return BlockyChefBlocks.CARROT_CROPS.defaultBlockState();
        }
        if (stack.is(Items.BEETROOT_SEEDS)) {
            return BlockyChefBlocks.BEETROOT_CROPS.defaultBlockState();
        }
        if (stack.getItem() instanceof IPlantable plantable) {
            if (plantable.getPlantType(level, this.action.pos()) == PlantType.CROP) {
                return plantable.getPlant(level, this.action.pos());
            }
        }
        return null;
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, Villager villager, long levelTime) {
        return this.workTime < 1000;
    }

    private void resetAction(ServerLevel level, Villager villager, long levelTime) {
        this.action = this.getAction(level, level.getRandom(), villager);
        if (this.action != null) {
            this.nextStartTime = levelTime + 20L;
            Brain<?> brain = villager.getBrain();
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.action.pos()), 0.5F, 1));
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.action.pos()));
        }
    }

    @Nullable
    private Action getAction(ServerLevel level, RandomSource random, Villager villager) {
        if (!this.toWeedFields.isEmpty()) {
            BlockPos pos = this.toWeedFields.get(random.nextInt(this.toWeedFields.size()));
            return new Action(pos, ActionType.WEED);
        }
        if (!this.unplantedFields.isEmpty()) {
            ItemStack seeds = this.getAnySeeds(level, villager);
            if (!seeds.isEmpty()) {
                BlockPos pos = this.unplantedFields.get(random.nextInt(this.unplantedFields.size()));
                return new Action(pos, ActionType.PLANT);
            }
        }
        if (!this.validFarmFields.isEmpty()) {
            BlockPos pos = this.validFarmFields.get(random.nextInt(this.validFarmFields.size()));
            return new Action(pos, ActionType.HARVEST);
        }
        return null;
    }

    private boolean isFullyGrown(Block plant, BlockState state) {
        if (plant instanceof CropsBlock cropsBlock) {
            return cropsBlock.isMaxAge(state);
        }
        if (plant instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        }
        return false;
    }

    private record Action(BlockPos pos, ActionType type) {}

    private enum ActionType {
        WEED,
        PLANT,
        HARVEST
    }
}
