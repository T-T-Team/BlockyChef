package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.util.Helper;

import java.util.Optional;

public class DryingRackBlock extends FullHorizontalAxisBlock implements EntityBlock {

    public static final VoxelShape[] HITBOX = {
            Block.box(0.0, 13.0, 14.0, 16.0, 16.0, 16.0), // NORTH
            Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 2.0), // SOUTH
            Block.box(14.0, 13.0, 0.0, 16.0, 16.0, 16.0), // WEST
            Block.box(0.0, 13.0, 0.0, 2.0, 16.0, 16.0)  // EAST
    };

    public DryingRackBlock() {
        this(Material.WOOD);
    }

    public DryingRackBlock(Material material) {
        super(Properties.of(material).strength(1.4F).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).ordinal() - 2;
        return HITBOX[index];
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor accessor, BlockPos pos1, BlockPos pos2) {
        return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(accessor, pos1) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockState attached = reader.getBlockState(pos.relative(direction.getOpposite()));
        return attached.isFaceSturdy(reader, pos, direction);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext placementCtx) {
        BlockState state = defaultBlockState();
        Level level = placementCtx.getLevel();
        BlockPos pos = placementCtx.getClickedPos();
        Direction[] directions = placementCtx.getNearestLookingDirections();
        for (Direction direction : directions) {
            if (direction.getAxis().isHorizontal()) {
                Direction attachDir = direction.getOpposite();
                state = state.setValue(FACING, attachDir);
                if (state.canSurvive(level, pos)) {
                    return state;
                }
            }
        }
        return null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Optional<DryingRackBlockEntity> optional = level.getBlockEntity(pos, BlockyChefBlockEntities.DRYING_RACK);
        return optional.map(dryingRack -> {
            ItemStack stack = player.getItemInHand(hand);
            if (dryingRack.hasItem()) {
                dryingRack.clearInventoryAndProcessRecipe(player);
            } else if (dryingRack.isValidInput(stack, level)) {
                if (!level.isClientSide) {
                    ItemStack insertionItem = stack.copy();
                    insertionItem.setCount(1);
                    dryingRack.setItem(insertionItem);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }).orElse(InteractionResult.PASS);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacementState, boolean someValue) {
        if (!state.is(replacementState.getBlock())) {
            if (!level.isClientSide) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof DryingRackBlockEntity dryingRack) {
                    dryingRack.clearInventoryAndProcessRecipe(null);
                }
            }
        }
        super.onRemove(state, level, pos, replacementState, someValue);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockyChefBlockEntities.DRYING_RACK.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : Helper.createBlockEntityTicker(type, BlockyChefBlockEntities.DRYING_RACK, DryingRackBlockEntity::tick);
    }
}
