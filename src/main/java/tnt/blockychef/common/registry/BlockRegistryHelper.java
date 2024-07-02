package tnt.blockychef.common.registry;

import net.minecraft.world.level.block.Block;

@FunctionalInterface // Registers blocks and schedules itemBlock registration
public interface BlockRegistryHelper {
    void register(String name, Block block, boolean createItem);

    default void register(String name, Block block) {
        register(name, block, true);
    }
}
