package tnt.blockychef.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.client.render.MasteryOverlay;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.food.mastery.PlayerMasteryDataProvider;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Server2ClientMessage;

@Network.Message(BlockyChef.MODID)
public final class S2C_SendMasteryLevelUpEvent extends Server2ClientMessage {

    private final ResourceLocation mastery;
    private final int originalCount;

    public S2C_SendMasteryLevelUpEvent(ResourceLocation mastery, int originalCount) {
        this.mastery = mastery;
        this.originalCount = originalCount;
    }

    public S2C_SendMasteryLevelUpEvent(FriendlyByteBuf buf) {
        this.mastery = buf.readResourceLocation();
        this.originalCount = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(mastery);
        buffer.writeInt(originalCount);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(Minecraft minecraft, NetworkEvent.Context context) {
        Item item = ForgeRegistries.ITEMS.getValue(this.mastery);
        Player player = minecraft.player;
        int count = PlayerMasteryDataProvider.getMasteryData(player).map(data -> data.getCookedCount(item)).orElse(0);
        CookingMastery cookingMastery = BlockyChef.MASTERY_MANAGER.getMastery(item).orElseThrow(() -> new IllegalArgumentException("Mastery Not Found"));
        MasteryOverlay.receiveMasteryUpdate(cookingMastery, this.originalCount, count);
    }
}
