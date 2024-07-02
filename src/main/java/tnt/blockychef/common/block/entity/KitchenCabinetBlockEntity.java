package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.init.BlockyChefBlockEntities;

public class KitchenCabinetBlockEntity extends ColorableBlockEntity {

    public KitchenCabinetBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.KITCHEN_CABINET, pos, state);
    }

    @Override
    public int getColorLayerCount() {
        return 1;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(27);
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
