package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.StoveBlockEntity;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.heat.HeatSourceProvider;
import tnt.blockychef.common.heat.NoHeatSource;
import tnt.blockychef.common.heat.RegulatedHeatSource;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.StoveMenu;
import tnt.tntlib.api.blockentity.BlockEntityHelper;

public class StoveBlock extends DyeableBlock implements EntityBlock, HeatSourceProvider {

    public static final Component TITLE = Component.translatable("screen.blockychef.stove");

    public StoveBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(3.0F).noOcclusion());
    }

    @Override
    protected InteractionResult handleDefaultInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, ItemStack stack) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof StoveBlockEntity stove) {
               ((ServerPlayer) player).openMenu(new SimpleMenuProvider((id, inv, owner) -> new StoveMenu(id, inv, stove), TITLE), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pose1, BlockState state) {
        return BlockyChefBlockEntities.STOVE.create(pose1, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockEntityHelper.createBlockEntityTicker(pBlockEntityType, BlockyChefBlockEntities.STOVE, StoveBlockEntity::tick);
    }

    @Override
    public HeatSource getHeatSourceAt(Level level, BlockPos pos, @Nullable Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof StoveBlockEntity stove) {
            return direction != null ? stove.getExternalHeatSource() : stove.getStoveHeatSource();
        }
        return NoHeatSource.INSTANCE;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof StoveBlockEntity stove) {
            RegulatedHeatSource stoveHeatSource = stove.getStoveHeatSource();
            if (stoveHeatSource.isProducingHeat()) {
                double x = pos.getX() + 0.5D;
                double y = pos.getY();
                double z = pos.getZ() + 0.5D;
                if (random.nextDouble() < 0.1D) {
                    level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }

                Direction direction = blockState.getValue(FACING);
                Direction.Axis direction$axis = direction.getAxis();
                double modifier = random.nextDouble() * 0.6D - 0.3D;
                double mx = direction$axis == Direction.Axis.X ? direction.getStepX() * 0.52D : modifier;
                double my = random.nextDouble() * 6.0D / 16.0D;
                double mz = direction$axis == Direction.Axis.Z ? direction.getStepZ() * 0.52D : modifier;
                level.addParticle(ParticleTypes.SMOKE, x + mx, y + my, z + mz, 0.0D, 0.0D, 0.0D);
                level.addParticle(ParticleTypes.FLAME, x + mx, y + my, z + mz, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
