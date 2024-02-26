package tnt.tntlib.core.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import tnt.tntlib.TNTLib;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Server2ClientMessage;

@Network.Message(TNTLib.MOD_ID)
public class S2C_SendBlockEntityData extends Server2ClientMessage {

    private final BlockPos pos;
    private final CompoundTag tag;

    public S2C_SendBlockEntityData(BlockPos pos, CompoundTag tag) {
        this.pos = pos;
        this.tag = tag;
    }

    public S2C_SendBlockEntityData(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readNbt());
    }

    public static <T extends BlockEntity & Synchronizable> S2C_SendBlockEntityData create(T blockEntity) {
        CompoundTag tag = new CompoundTag();
        blockEntity.encodeData(tag);
        return new S2C_SendBlockEntityData(blockEntity.getBlockPos(), tag);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeNbt(tag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(Minecraft minecraft, CustomPayloadEvent.Context context) {
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof Synchronizable synchronizable) {
            synchronizable.decodeData(tag);
        }
    }
}
