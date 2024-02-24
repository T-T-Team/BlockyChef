package tnt.blockychef.common;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.FluidInteractBlock;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;
import tnt.blockychef.common.thirst.ThirstStats;
import tnt.tntlib.api.menu.MenuInventoryHelper;

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

    @SubscribeEvent
    public static void onItemInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled())
            return;
        BlockHitResult hitResult = event.getHitVec();
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = event.getPos();
            Level level = event.getLevel();
            BlockState state = level.getBlockState(pos);
            ItemStack itemStack = event.getItemStack();
            if (state.getBlock() instanceof FluidInteractBlock fluidInteraction) {
                Player player = event.getEntity();
                ItemStack interactionResult = fluidInteraction.getPickupItem(itemStack, level, pos, state, player);
                Vec3 vec = Vec3.atCenterOf(pos);
                if (!interactionResult.isEmpty()) {
                    fluidInteraction.onInteractionEvent(level, pos, state, player);
                    if (!level.isClientSide()) {
                        if (!player.isCreative())
                            itemStack.shrink(1);
                        MenuInventoryHelper.giveItemOrDrop(player, interactionResult);
                        fluidInteraction.getPickupSound(state).ifPresent(soundEvent ->
                                level.playSound(null, vec.x, vec.y, vec.z, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F));
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    private static void sendClientData(Player player) {
        player.getCapability(PlayerThirstStatsProvider.CAPABILITY)
                .ifPresent(ThirstStats::sendClientData);
    }
}
