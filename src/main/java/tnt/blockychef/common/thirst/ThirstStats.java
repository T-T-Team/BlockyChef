package tnt.blockychef.common.thirst;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface ThirstStats extends INBTSerializable<CompoundTag> {

    void tick();

    boolean canDrink(DrinkProperties stats);

    void drink(DrinkProperties stats);

    int getHydrationLevel();

    void setHydrationLevel(int value);

    float getSaturationLevel();

    void setSaturationLevel(float saturation);

    float getExhaustionLevel();

    void setExhaustionLevel(float exhaustion);

    void addExhaustion(float exhaustion);

    void sendClientData();
}
