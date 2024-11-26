package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.MixerBlockEntity;
import tnt.blockychef.common.block.entity.RecipeRememberingBlockEntity;
import tnt.blockychef.common.data.fluids.FluidExtraction;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.MixerMenu;
import tnt.tntlib.api.menu.MenuInventoryHelper;

public class MixerBlock extends DyeableBlock implements EntityBlock {

    private static final VoxelShape HITBOX = Block.box(4.0, 0.0, 4.0, 12.0, 14.0, 12.0);
    private static final Component TITLE = Component.translatable("screen.blockychef.mixer");

    public MixerBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(2.0F).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return HITBOX;
    }

    @Override
    public int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return 0;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return pDirection == Direction.DOWN && !pState.canSurvive(pLevel, pPos) ? Blocks.AIR.defaultBlockState() : pState;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos below = pPos.relative(Direction.DOWN);
        BlockState base = pLevel.getBlockState(below);
        return base.isFaceSturdy(pLevel, pPos, Direction.UP);
    }

    @Override
    protected InteractionResult handleDefaultInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, ItemStack stack) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MixerBlockEntity mixer) {
            FluidExtraction extraction = BlockyChef.EXTRACTION_MANAGER.getExtractor(stack, mixer);
            if (extraction != null) {
                ItemStack result = extraction.extractFluid(mixer);
                if (!result.isEmpty()) {
                    CookingMastery.applyMastery(player, result);
                    MenuInventoryHelper.giveItemOrDrop(player, result);
                }
            } else if (!level.isClientSide) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(
                        (menuId, inv, owner) -> new MixerMenu(menuId, inv, mixer),
                        TITLE
                ), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacementState, boolean p_60519_) {
        RecipeRememberingBlockEntity.dropRecipeBlockInventoryContentsAndAwardExp(state, level, pos, replacementState);
        super.onRemove(state, level, pos, replacementState, p_60519_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos1, BlockState state) {
        return BlockyChefBlockEntities.MIXER.create(pos1, state);
    }
}
