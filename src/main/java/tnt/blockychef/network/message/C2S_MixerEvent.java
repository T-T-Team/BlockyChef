package tnt.blockychef.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.MixerBlockEntity;
import tnt.blockychef.common.food.recipe.MixerRecipe;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Client2ServerMessage;

@Network.Message(BlockyChef.MODID)
public class C2S_MixerEvent extends Client2ServerMessage {

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
    public void handle(ServerPlayer player, NetworkEvent.Context context) {
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
        BlockEntityHelper.sendBlockEntityClientData(mixer);
    }
}
