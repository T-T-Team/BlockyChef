package tnt.blockychef.common.block.entity;

import net.minecraft.world.entity.player.Player;

public interface ApplianceEventConsumer {

    void onEvent(Player eventOrigin, int eventId);
}
