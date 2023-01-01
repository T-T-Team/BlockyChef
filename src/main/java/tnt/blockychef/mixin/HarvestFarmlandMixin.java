package tnt.blockychef.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tnt.blockychef.common.Registry;

import java.util.Map;

@Mixin(HarvestFarmland.class)
public abstract class HarvestFarmlandMixin extends Behavior<Villager> {

    public HarvestFarmlandMixin(Map<MemoryModuleType<?>, MemoryStatus> p_22528_) {
        super(p_22528_);
    }

    @Inject(
            method = "validPos",
            at = @At(value = "RETURN", shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void blockychef$villagerDestroyWeeds(BlockPos p_23181_, ServerLevel p_23182_, CallbackInfoReturnable<Boolean> cir, BlockState state, Block block1, Block block2) {
        if (block2 instanceof FarmBlock && block1 == Registry.WEEDS) {
            cir.setReturnValue(true);
        }
    }
}
