package tnt.blockychef.common.food.mastery;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface MasteryDataProvider extends INBTSerializable<CompoundTag> {

    int getCookedCount(Item item);

    void setCookedCount(Item item, int count);

    void sendClientData();

    default void addCookedCount(Item item, int count) {
        int actual = getCookedCount(item);
        setCookedCount(item, actual + count);
    }
}
