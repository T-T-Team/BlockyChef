package tnt.blockychef.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;
import tnt.blockychef.common.thirst.ThirstStats;

@Mod.EventBusSubscriber(modid = BlockyChef.MODID)
public final class EventHandler {

    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (BlockyChef.config.crops.restrictBonemealUsage)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        sendClientData(event.getEntity());
    }

    @SubscribeEvent
    public static void onDimensionTravel(PlayerEvent.PlayerChangedDimensionEvent event) {
        sendClientData(event.getEntity());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
            CompoundTag tag = stats.serializeNBT();
            event.getEntity().getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats1 -> {
                stats1.deserializeNBT(tag);
                stats1.sendClientData();
            });
        });
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered()) {
            Player player = event.getEntity();
            player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
                stats.setHydrationLevel(20);
                stats.setSaturationLevel(5.0F);
                stats.sendClientData();
            });
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;
        event.player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(ThirstStats::tick);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (entity instanceof Player player) {
            player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> stats.addExhaustion(source.getFoodExhaustion()));
        }

        Entity origin = source.getEntity();
        if (origin instanceof Player player) {
            player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> stats.addExhaustion(0.1F));
        }
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            float exhaustion = player.isSprinting() ? 0.2F : 0.05F;
            player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> stats.addExhaustion(exhaustion));
        }
    }

    private static void sendClientData(Player player) {
        player.getCapability(PlayerThirstStatsProvider.CAPABILITY)
                .ifPresent(ThirstStats::sendClientData);
    }
}
