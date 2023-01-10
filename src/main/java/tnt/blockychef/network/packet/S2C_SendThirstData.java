package tnt.blockychef.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;
import tnt.blockychef.network.Packet;

public class S2C_SendThirstData extends Packet {

    private final CompoundTag tag;

    public S2C_SendThirstData(CompoundTag tag) {
        this.tag = tag;
    }

    public S2C_SendThirstData(FriendlyByteBuf buffer) {
        this(buffer.readNbt());
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(NetworkEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(data -> data.deserializeNBT(tag));
    }
}
