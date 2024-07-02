package tnt.blockychef.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import tnt.blockychef.BlockyChef;

public final class BlockyChefDamageTypes {

    public static final ResourceKey<DamageType> DEHYDRATION = ResourceKey.create(Registries.DAMAGE_TYPE, BlockyChef.resource("dehydration"));
}
