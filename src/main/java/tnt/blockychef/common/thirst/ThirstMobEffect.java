package tnt.blockychef.common.thirst;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ThirstMobEffect extends MobEffect {

    public ThirstMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
                stats.addExhaustion(0.005F * (amplifier + 1));
            });
        }
    }
}
