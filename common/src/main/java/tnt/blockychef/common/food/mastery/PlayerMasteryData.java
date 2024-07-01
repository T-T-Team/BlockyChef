package tnt.blockychef.common.food.mastery;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.S2C_SendPlayerMasteryData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PlayerMasteryData implements MasteryDataProvider {

    private final Player player;
    private final Map<Item, Integer> cookedCountTracker = new HashMap<>();

    public PlayerMasteryData(Player player) {
        this.player = player;
    }

    @Override
    public int getCookedCount(Item item) {
        return cookedCountTracker.getOrDefault(item, 0);
    }

    @Override
    public void setCookedCount(Item item, int count) {
        cookedCountTracker.put(item, Math.max(0, count));
    }

    @Override
    public void sendClientData() {
        if (!player.level().isClientSide) {
            NetworkManager.DISPATCHER.sendToClient((ServerPlayer) player, new S2C_SendPlayerMasteryData(serializeNBT()));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        cookedCountTracker.forEach((item, value) -> tag.putInt(ForgeRegistries.ITEMS.getKey(item).toString(), value));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        cookedCountTracker.clear();
        Set<String> items = nbt.getAllKeys();
        items.forEach(itemId -> {
            ResourceLocation location = new ResourceLocation(itemId);
            Item item = ForgeRegistries.ITEMS.getValue(location);
            if (item != Items.AIR) {
                cookedCountTracker.put(item, nbt.getInt(itemId));
            }
        });
    }
}
