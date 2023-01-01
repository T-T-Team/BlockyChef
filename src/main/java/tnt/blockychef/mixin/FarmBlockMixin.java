package tnt.blockychef.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.Registry;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin extends Block {

    public FarmBlockMixin(Properties p_49795_) {
        super(p_49795_);
    }

    // Handles addition of weeds
    @Inject(method = "randomTick", at = @At("RETURN"))
    private void blockychef$farmBlockRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!BlockyChef.config.weeds.placeOnRawFarmland)
            return;
        BlockPos above = pos.above();
        if (!level.isClientSide && level.isEmptyBlock(above)) {
            level.setBlock(above, Registry.WEEDS.defaultBlockState(), 2);
        }
    }
}
