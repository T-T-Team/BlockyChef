package tnt.tntlib.api.functional;

import net.minecraft.world.level.block.Block;

@FunctionalInterface
public interface BlockRegistrationHelper {

    void register(String name, Block block, boolean createItem);

    default void register(String name, Block block) {
        register(name, block, true);
    }
}
