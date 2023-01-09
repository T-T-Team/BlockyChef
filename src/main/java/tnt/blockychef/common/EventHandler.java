package tnt.blockychef.common;

import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tnt.blockychef.BlockyChef;

@Mod.EventBusSubscriber(modid = BlockyChef.MODID)
public final class EventHandler {

    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (BlockyChef.config.crops.restrictBonemealUsage)
            event.setCanceled(true);
    }
}
