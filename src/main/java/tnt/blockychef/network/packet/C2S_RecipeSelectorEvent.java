package tnt.blockychef.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.common.block.entity.SelectableRecipeHolder;
import tnt.blockychef.common.block.entity.SynchronizableBlockEntity;
import tnt.blockychef.network.Packet;
import tnt.blockychef.util.Helper;

public class C2S_RecipeSelectorEvent extends Packet {

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
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        ServerLevel level = player.getLevel();
        if (!level.isLoaded(pos))
            return;
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof SelectableRecipeHolder holder) {
            switch (eventType) {
                case PROCESSING -> holder.setProcessing(data);
                case RECIPE -> holder.changeRecipe(data ? 1 : -1);
            }
            Helper.sendBlockEntityClientData((BlockEntity & SynchronizableBlockEntity) holder);
        }
    }

    public enum EventType {

        PROCESSING,
        RECIPE
    }
}
