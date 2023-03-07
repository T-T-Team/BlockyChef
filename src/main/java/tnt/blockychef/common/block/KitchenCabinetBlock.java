package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.init.BlockyChefBlockEntities;

public class KitchenCabinetBlock extends DyeableBlock implements EntityBlock {

    private static final VoxelShape[] HITBOX = {
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

    @Override
    public int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockyChefBlockEntities.KITCHEN_CABINET.create(pos, state);
    }
}
