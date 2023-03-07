package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KitchenCabinetBlock extends FullHorizontalAxisBlock {

    private static final VoxelShape [] HITBOX = {
            Block.box(0.0, 2.0, 9.0, 16.0, 16.0, 16.0), // NORTH
            Block.box(0.0, 2.0, 0.0, 16.0, 16.0, 7.0), // SOUTH
            Block.box(9.0, 2.0, 0.0, 16.0, 16.0, 16.0), // WEST
            Block.box(0.0, 2.0, 0.0, 7.0, 16.0, 16.0)  // EAST
    };

    public KitchenCabinetBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).ordinal() - 2;
        return HITBOX[index];
    }
}
