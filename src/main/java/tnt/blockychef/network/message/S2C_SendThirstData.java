package tnt.blockychef.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Server2ClientMessage;

@Network.Message(BlockyChef.MODID)
public class S2C_SendThirstData extends Server2ClientMessage {

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
    public void handle(Minecraft minecraft, CustomPayloadEvent.Context context) {
        Player player = minecraft.player;
        player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(data -> data.deserializeNBT(tag));
    }
}
