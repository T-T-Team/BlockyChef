package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.BlockEntityStackHandler;

public class KitchenCounterBlockEntity extends ColorableBlockEntity {

    public KitchenCounterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.KITCHEN_COUNTER, pos, state);
    }

    @Override
    public int getColorLayerCount() {
        return 2;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new BlockEntityStackHandler(54, this::setChanged);
    }

    public void onOpen(Player player) {
        if (!remove && !player.isSpectator()) {
            level.playSound(null, worldPosition, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS);
        }
    }

    public void onClose(Player player) {
        if (!remove && !player.isSpectator()) {
            level.playSound(null, worldPosition, SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS);
        }
    }
}
