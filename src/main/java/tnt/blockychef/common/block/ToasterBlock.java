package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ToasterBlock extends FullHorizontalAxisBlock {

    private static final VoxelShape HITBOX = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);

    public ToasterBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return HITBOX;
    }
}
