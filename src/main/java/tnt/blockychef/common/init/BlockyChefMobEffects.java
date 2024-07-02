package tnt.blockychef.common.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ObjectHolder;

public final class BlockyChefMobEffects {

    private static final String KEY = "mob_effect";

    @ObjectHolder(value = "blockychef:thirst", registryName = KEY)
    public static final MobEffect THIRST = null;
    @ObjectHolder(value = "blockychef:hydration", registryName = KEY)
    public static final MobEffect HYDRATION = null;
}
