package tnt.blockychef.common.init;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.loot.ReplacerLootModifier;

public final class BlockyChefLootModifiers {

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BlockyChef.MODID);

    public static final RegistryObject<Codec<ReplacerLootModifier>> REPLACER_LOOT_MODIFIER = DEFERRED_REGISTER.register("replace_drops", () -> ReplacerLootModifier.CODEC);

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }
}
