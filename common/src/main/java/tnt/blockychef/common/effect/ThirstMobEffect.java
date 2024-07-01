package tnt.blockychef.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;

public class ThirstMobEffect extends MobEffect {

    public ThirstMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 1;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
                float oldExh = stats.getExhaustionLevel();
                stats.setExhaustionLevel(oldExh + 0.04F * (amplifier + 1));
            });
        }
    }
}
