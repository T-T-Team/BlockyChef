package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.GraterBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.util.MenuInventoryHelper;

public class GraterBlock extends FullHorizontalAxisBlock implements EntityBlock {

    private static final VoxelShape HITBOX = Block.box(5.0, 0.0, 5.0, 11.0, 9.0, 11.0);

    public GraterBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(1.5F).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return HITBOX;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GraterBlockEntity graterEntity) {
            ItemStack stack = player.getItemInHand(hand);
            if (graterEntity.hasActiveRecipe()) {
                if (!level.isClientSide) {
                    graterEntity.processRecipe((ServerPlayer) player);
                }
                return InteractionResult.SUCCESS;
            } else if (graterEntity.isValidInput(stack, level)) {
                ItemStack toPlace = stack.copy();
                toPlace.setCount(1);
                graterEntity.place(toPlace);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacementState, boolean someValue) {
        MenuInventoryHelper.dropRecipeBlockInventoryContentsAndAwardExp(state, level, pos, replacementState);
        super.onRemove(state, level, pos, replacementState, someValue);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockyChefBlockEntities.GRATER.create(pos, state);
    }
}
