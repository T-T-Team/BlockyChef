package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class DryingRackBlock extends FullHorizontalAxisBlock {

    public DryingRackBlock() {
        this(Material.WOOD);
    }

    public DryingRackBlock(Material material) {
        super(Properties.of(material).strength(1.4F).noCollission());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // TODO implement
        return InteractionResult.PASS;
    }
}
