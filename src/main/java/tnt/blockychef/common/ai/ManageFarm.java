package tnt.blockychef.common.ai;

import com.google.common.collect.ImmutableMap;
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
import tnt.blockychef.common.Registry;
import tnt.blockychef.common.block.CropsBlock;
import tnt.blockychef.common.block.WeedsGrowingBlock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ManageFarm extends Behavior<Villager> {

    private final List<BlockPos> validFarmFields = new ArrayList<>();
    private final List<BlockPos> toWeedFields = new ArrayList<>();
    private Action action;
    private long nextStartTime;
    private int workTime;

    public ManageFarm() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryStatus.VALUE_PRESENT));
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
        for(int x = -1; x <= 1; ++x) {
            for(int y = -1; y <= 1; ++y) {
                for(int z = -1; z <= 1; ++z) {
                    mutable.set(villager.getX() + x, villager.getY() + y, villager.getZ() + z);
                    BlockPos pos = mutable.immutable();
                    BlockState state = level.getBlockState(pos);
                    Block plant = state.getBlock();
                    Block soil = level.getBlockState(pos.below()).getBlock();
                    if (soil instanceof FarmBlock) {
                        if (plant instanceof WeedsGrowingBlock) {
                            int weedsLevel = state.getValue(WeedsGrowingBlock.WEEDS_AGE);
                            if (weedsLevel > 0) {
                                this.toWeedFields.add(pos);
                            } else if (plant instanceof CropsBlock cropsBlock) {
                                if (cropsBlock.isMaxAge(state)) {
                                    this.validFarmFields.add(pos);
                                }
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
        this.action = this.getAction(level);
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
                BlockState blockstate = level.getBlockState(this.action.pos);
                Block plant = blockstate.getBlock();
                Block soil = level.getBlockState(this.action.pos.below()).getBlock();
                if (this.action.isWeeding()) {
                    if (plant instanceof WeedsGrowingBlock) {
                        int weedsAge = blockstate.getValue(WeedsGrowingBlock.WEEDS_AGE);
                        if (weedsAge > 0) {
                            WeedsGrowingBlock.trimWeeds(this.action.pos, blockstate, level);
                        }
                    }
                    this.resetAction(level, villager, levelTime);
                } else {
                    if (this.isFullyGrown(plant, blockstate)) {
                        level.destroyBlock(this.action.pos, true, villager);
                    }

                    blockstate = level.getBlockState(this.action.pos);
                    plant = blockstate.getBlock();
                    if ((blockstate.isAir() || blockstate.canBeReplaced()) && soil instanceof FarmBlock && !villager.getInventory().isEmpty()) {
                        SimpleContainer simplecontainer = villager.getInventory();
                        for(int i = 0; i < simplecontainer.getContainerSize(); ++i) {
                            ItemStack itemstack = simplecontainer.getItem(i);
                            boolean flag = false;
                            if (!itemstack.isEmpty()) {
                                if (itemstack.is(Items.WHEAT_SEEDS)) {
                                    BlockState blockstate1 = Registry.WHEAT_CROPS.defaultBlockState();
                                    level.setBlockAndUpdate(this.action.pos, blockstate1);
                                    level.gameEvent(GameEvent.BLOCK_PLACE, this.action.pos, GameEvent.Context.of(villager, blockstate1));
                                    flag = true;
                                } else if (itemstack.is(Items.POTATO)) {
                                    BlockState blockstate2 = Registry.POTATO_CROPS.defaultBlockState();
                                    level.setBlockAndUpdate(this.action.pos, blockstate2);
                                    level.gameEvent(GameEvent.BLOCK_PLACE, this.action.pos, GameEvent.Context.of(villager, blockstate2));
                                    flag = true;
                                } else if (itemstack.is(Items.CARROT)) {
                                    BlockState blockstate3 = Registry.CARROT_CROPS.defaultBlockState();
                                    level.setBlockAndUpdate(this.action.pos, blockstate3);
                                    level.gameEvent(GameEvent.BLOCK_PLACE, this.action.pos, GameEvent.Context.of(villager, blockstate3));
                                    flag = true;
                                } else if (itemstack.is(Items.BEETROOT_SEEDS)) {
                                    BlockState blockstate4 = Registry.BEETROOT_CROPS.defaultBlockState();
                                    level.setBlockAndUpdate(this.action.pos, blockstate4);
                                    level.gameEvent(GameEvent.BLOCK_PLACE, this.action.pos, GameEvent.Context.of(villager, blockstate4));
                                    flag = true;
                                } else if (itemstack.getItem() instanceof IPlantable plantable) {
                                    if (plantable.getPlantType(level, this.action.pos) == PlantType.CROP) {
                                        BlockState plantState = plantable.getPlant(level, this.action.pos);
                                        level.setBlock(this.action.pos, plantState, 3);
                                        flag = true;
                                    }
                                }
                            }

                            if (flag) {
                                level.playSound(null, this.action.pos.getX(), this.action.pos.getY(), this.action.pos.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                                itemstack.shrink(1);
                                if (itemstack.isEmpty()) {
                                    simplecontainer.setItem(i, ItemStack.EMPTY);
                                }
                                break;
                            }
                        }
                    }

                    if (!this.isFullyGrown(plant, blockstate)) {
                        this.validFarmFields.remove(this.action.pos);
                        this.resetAction(level, villager, levelTime);
                    }
                }
            }
            ++this.workTime;
        }
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, Villager villager, long levelTime) {
        return this.workTime < 500;
    }

    private void resetAction(ServerLevel level, Villager villager, long levelTime) {
        this.action = this.getAction(level);
        if (this.action != null) {
            this.nextStartTime = levelTime + 20L;
            Brain<?> brain = villager.getBrain();
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.action.pos()), 0.5F, 1));
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.action.pos()));
        }
    }

    @Nullable
    private Action getAction(ServerLevel level) {
        RandomSource random = level.getRandom();
        if (!this.toWeedFields.isEmpty()) {
            BlockPos pos = this.toWeedFields.get(random.nextInt(this.toWeedFields.size()));
            return new Action(pos, true);
        }
        if (!this.validFarmFields.isEmpty()) {
            BlockPos pos = this.validFarmFields.get(random.nextInt(this.validFarmFields.size()));
            return new Action(pos, false);
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

    private record Action(BlockPos pos, boolean isWeeding) {}
}
