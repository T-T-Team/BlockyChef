package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class StoveBlock extends FullHorizontalAxisBlock {

    public StoveBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(3.0F));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Direction direction = hitResult.getDirection();
        if (direction == Direction.UP) {
            // Interact with heater
        } else {
            // Interact with oven
        }
        return InteractionResult.PASS;
    }
}
