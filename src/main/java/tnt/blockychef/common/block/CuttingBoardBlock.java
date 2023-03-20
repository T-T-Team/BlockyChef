package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.CuttingBoardBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.util.Helper;

public class CuttingBoardBlock extends FullHorizontalAxisBlock implements EntityBlock {

    private static final VoxelShape HITBOX = Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0);

    public CuttingBoardBlock() {
        super(Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return HITBOX;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockyChefBlockEntities.CUTTING_BOARD.create(pos, state);
    }

    // TODO interation

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : Helper.createBlockEntityTicker(type, BlockyChefBlockEntities.CUTTING_BOARD, CuttingBoardBlockEntity::tick);
    }
}
