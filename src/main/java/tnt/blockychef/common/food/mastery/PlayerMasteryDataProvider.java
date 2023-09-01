package tnt.blockychef.common.food.mastery;

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

public class PlayerMasteryDataProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<MasteryDataProvider> MASTERY_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<MasteryDataProvider> instance;

    public PlayerMasteryDataProvider(Player player) {
        this.instance = LazyOptional.of(() -> new PlayerMasteryData(player));
    }

    public PlayerMasteryDataProvider() {
        this(null);
    }

    public static LazyOptional<MasteryDataProvider> getMasteryData(Player player) {
        return player.getCapability(MASTERY_DATA);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return MASTERY_DATA.orEmpty(cap, instance);
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.map(INBTSerializable::serializeNBT).orElseGet(CompoundTag::new);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.ifPresent(data -> data.deserializeNBT(nbt));
    }
}
