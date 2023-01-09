package tnt.blockychef.common.thirst;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface ThirstStats extends INBTSerializable<CompoundTag> {

    void tick(Player player);

    void drink(DrinkStats stats, Player player);

    int getHydrationLevel();

    void setHydrationLevel(int value);

    float getSaturationLevel();

    void setSaturationLevel(float saturation);

    float getExhaustionLevel();

    void setExhaustionLevel(float exhaustion);
}
