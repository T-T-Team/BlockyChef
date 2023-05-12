package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.JuicerBlockEntity;
import tnt.blockychef.common.data.fluids.FluidExtractor;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.MenuInventoryHelper;

public class JuicerBlock extends DyeableBlock implements EntityBlock {

    private static final VoxelShape HITBOX = Block.box(4.0, 0.0, 4.0, 12.0, 4.0, 12.0);

    public JuicerBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F));
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
    protected InteractionResult handleDefaultInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, ItemStack stack) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof JuicerBlockEntity juicer) {
            FluidExtractor extractor = BlockyChef.EXTRACTION_MANAGER.getExtractor(stack, juicer);
            if (extractor != null) {
                ItemStack result = extractor.extractFluid(juicer);
                if (!result.isEmpty()) {
                    Helper.giveItem(player, result);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (juicer.hasInputItem()) {
                juicer.processRecipe(player);
                return InteractionResult.SUCCESS;
            } else if (!stack.isEmpty() && juicer.isNotFull()) {
                juicer.setInputItem(stack.copy());
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacementState, boolean p_60519_) {
        super.onRemove(state, level, pos, replacementState, p_60519_);
        MenuInventoryHelper.dropRecipeBlockInventoryContentsAndAwardExp(state, level, pos, replacementState);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos1, BlockState state) {
        return BlockyChefBlockEntities.JUICER.create(pos1, state);
    }
}
