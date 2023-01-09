package tnt.blockychef.common.thirst;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlayerThirstStatsProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<ThirstStats> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private final LazyOptional<ThirstStats> instance;

    public PlayerThirstStatsProvider(Player player) {
        this.instance = LazyOptional.of(() -> new PlayerThirstStats(player));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITY.orEmpty(cap, instance);
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.map(INBTSerializable::serializeNBT).orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.ifPresent(stats -> stats.deserializeNBT(nbt));
    }
}
