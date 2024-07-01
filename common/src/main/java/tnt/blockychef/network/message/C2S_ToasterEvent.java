package tnt.blockychef.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.ToasterBlockEntity;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Client2ServerMessage;

@Network.Message(BlockyChef.MODID)
public class C2S_ToasterEvent extends Client2ServerMessage {

    private final BlockPos pos;
    private final ToasterEventType eventType;
    private final boolean toastingStatus;
    private final int toastingTime;

    private C2S_ToasterEvent(BlockPos pos, ToasterEventType eventType, boolean toastingStatus, int toastingTime) {
        this.pos = pos;
        this.eventType = eventType;
        this.toastingStatus = toastingStatus;
        this.toastingTime = toastingTime;
    }

    public C2S_ToasterEvent(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.eventType = buffer.readEnum(ToasterEventType.class);
        this.toastingStatus = eventType == ToasterEventType.TOAST && buffer.readBoolean();
        this.toastingTime = eventType == ToasterEventType.TIME ? buffer.readInt() : 0;
    }

    public static C2S_ToasterEvent toastEvent(BlockPos pos, boolean status) {
        return new C2S_ToasterEvent(pos, ToasterEventType.TOAST, status, 0);
    }

    public static C2S_ToasterEvent timeEvent(BlockPos pos, int time) {
        return new C2S_ToasterEvent(pos, ToasterEventType.TIME, false, time);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeEnum(eventType);
        if (eventType == ToasterEventType.TOAST) {
            buffer.writeBoolean(toastingStatus);
        } else {
            buffer.writeInt(toastingTime);
        }
    }

    @Override
    public void handle(ServerPlayer player, CustomPayloadEvent.Context context) {
        ServerLevel level = player.serverLevel();
        if (!level.isLoaded(pos))
            return;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ToasterBlockEntity toaster) {
            if (eventType == ToasterEventType.TOAST) {
                toaster.setToasting(toastingStatus);
            } else {
                toaster.setToastingTimer(toastingTime);
            }
            BlockEntityHelper.sendBlockEntityClientData(toaster);
        }
    }

    private enum ToasterEventType {

        TOAST,
        TIME
    }
}
