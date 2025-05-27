package tnt.blockychef.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;

public class HydrationMobEffect extends MobEffect {

    public HydrationMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 1;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
                stats.setHydrationLevel(Math.min(20, stats.getHydrationLevel() + 1));
                stats.setSaturationLevel(Math.min(stats.getHydrationLevel(), stats.getSaturationLevel() + 2.0F));
            });
        }
    }
}
