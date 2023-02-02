package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CuttingBoardBlock extends FullHorizontalAxisBlock {

    private static final VoxelShape HITBOX = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    public CuttingBoardBlock() {
        super(Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).noCollission());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return HITBOX;
    }
}
