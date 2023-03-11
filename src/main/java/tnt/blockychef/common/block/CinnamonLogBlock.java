package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefItems;

public class CinnamonLogBlock extends RotatedPillarBlock {

    public CinnamonLogBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (!simulate) {
            Level level = context.getLevel();
            if (!level.isClientSide) {
                dropCinnamonBark(context, level);
            }
            Direction.Axis axis = state.getValue(AXIS);
            return BlockyChefBlocks.CINNAMON_STRIPPED_LOG.defaultBlockState().setValue(AXIS, axis);
        }
        return null;
    }

    private void dropCinnamonBark(UseOnContext context, Level level) {
        BlockPos pos = context.getClickedPos();
        Vec3 center = pos.getCenter();
        int dropCount = 1 + level.random.nextInt(3);
        ItemEntity entity = new ItemEntity(level, center.x, center.y, center.z, new ItemStack(BlockyChefItems.CINNAMON_BARK, dropCount));
        level.addFreshEntity(entity);
    }
}
