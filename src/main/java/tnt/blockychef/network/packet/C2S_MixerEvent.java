package tnt.blockychef.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.common.block.entity.MixerBlockEntity;
import tnt.blockychef.common.food.recipe.MixerRecipe;
import tnt.blockychef.network.Packet;
import tnt.blockychef.util.Helper;

public class C2S_MixerEvent extends Packet {

    private final BlockPos pos;
    private final boolean isBlendEvent;
    private final int rpmIndex;

    public C2S_MixerEvent(BlockPos pos, boolean isBlendEvent, int newRpmIndex) {
        this.pos = pos;
        this.isBlendEvent = isBlendEvent;
        this.rpmIndex = newRpmIndex;
    }

    public C2S_MixerEvent(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean(), buffer.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(isBlendEvent);
        buffer.writeInt(rpmIndex);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        ServerLevel level = player.serverLevel();
        if (!level.isLoaded(pos))
            return;
        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof MixerBlockEntity mixer))
            return;
        if (isBlendEvent) {
            mixer.blend(player);
        } else {
            mixer.setSelectedRpm(MixerRecipe.RpmValue.values()[rpmIndex % MixerRecipe.RpmValue.values().length]);
        }
        mixer.setChanged();
        Helper.sendBlockEntityClientData(mixer);
    }
}
