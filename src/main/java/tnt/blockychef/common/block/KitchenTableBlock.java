package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.CookingTableBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.menu.MenuInventoryHelper;

public class KitchenTableBlock extends DyeableBlock implements EntityBlock {

    public KitchenTableBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(1.5F).noOcclusion());
    }

    @Override
    public int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Vec3 vec = hitResult.getLocation();
        double y = vec.y - pos.getY();
        return y >= 0.85 ? 0 : 1;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockyChefBlockEntities.COOKING_TABLE.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockEntityHelper.createBlockEntityTicker(pBlockEntityType, BlockyChefBlockEntities.COOKING_TABLE, CookingTableBlockEntity::tick);
    }
}
