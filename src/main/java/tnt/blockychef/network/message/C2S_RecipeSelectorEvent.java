package tnt.blockychef.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.SelectableRecipeHolder;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Client2ServerMessage;

@Network.Message(BlockyChef.MODID)
public class C2S_RecipeSelectorEvent extends Client2ServerMessage {

    private final BlockPos pos;
    private final EventType eventType;
    private final boolean data;

    public C2S_RecipeSelectorEvent(BlockPos pos, EventType eventType, boolean data) {
        this.pos = pos;
        this.eventType = eventType;
        this.data = data;
    }

    public C2S_RecipeSelectorEvent(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readEnum(EventType.class), buffer.readBoolean());
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeEnum(eventType);
        buffer.writeBoolean(data);
    }

    @Override
    public void handle(ServerPlayer player, NetworkEvent.Context context) {
        ServerLevel level = player.serverLevel();
        if (!level.isLoaded(pos))
            return;
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof SelectableRecipeHolder holder) {
            switch (eventType) {
                case PROCESSING -> holder.setProcessing(data);
                case RECIPE -> holder.changeRecipe(data ? 1 : -1);
            }
            BlockEntityHelper.sendBlockEntityClientData((BlockEntity & Synchronizable) holder);
        }
    }

    public enum EventType {

        PROCESSING,
        RECIPE
    }
}
