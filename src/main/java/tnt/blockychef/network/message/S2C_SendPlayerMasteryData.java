package tnt.blockychef.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.mastery.PlayerMasteryDataProvider;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Server2ClientMessage;

@Network.Message(BlockyChef.MODID)
public final class S2C_SendPlayerMasteryData extends Server2ClientMessage {

    private final CompoundTag tag;

    public S2C_SendPlayerMasteryData(CompoundTag tag) {
        this.tag = tag;
    }

    public S2C_SendPlayerMasteryData(FriendlyByteBuf buffer) {
        this(buffer.readNbt());
    }

    @Override
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(tag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(Minecraft minecraft, NetworkEvent.Context context) {
        Player player = minecraft.player;
        PlayerMasteryDataProvider.getMasteryData(player).ifPresent(data -> data.deserializeNBT(tag));
    }
}
