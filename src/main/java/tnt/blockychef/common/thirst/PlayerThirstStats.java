package tnt.blockychef.common.thirst;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import tnt.blockychef.common.Registry;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.packet.S2C_SendThirstData;

public class PlayerThirstStats implements ThirstStats {

    private final Player player;
    private int hydration = 20;
    private float saturation = 5.0F;
    private float exhaustion;
    private int tickTimer;

    public PlayerThirstStats(Player player) {
        this.player = player;
    }

    @Override
    public void tick() {
        Difficulty difficulty = player.level.getDifficulty();
        if (this.exhaustion > 4.0F) {
            this.exhaustion -= 4.0F;
            if (this.saturation > 0.0F) {
                this.saturation = Math.max(this.saturation - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.hydration = Math.max(this.hydration - 1, 0);
            }
            this.sendClientData();
        }
        if (this.hydration <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurt(Registry.DEHYDRATATION, 1.0F);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
    }

    @Override
    public void drink(DrinkProperties stats) {
        this.hydration = Mth.clamp(this.hydration + stats.getHydration(), 0, 20);
        this.saturation = Mth.clamp(this.saturation + Math.abs(stats.getHydration()) * stats.getSaturation() * 2.0F, 0.0F, this.hydration);
        if (!player.level.isClientSide) {
            stats.onConsumed(player);
        }
    }

    @Override
    public boolean canDrink(DrinkProperties stats) {
        return this.hydration < 20 || stats.isAlwaysDrinkable();
    }

    @Override
    public int getHydrationLevel() {
        return this.hydration;
    }

    @Override
    public void setHydrationLevel(int value) {
        this.hydration = value;
    }

    @Override
    public float getSaturationLevel() {
        return this.saturation;
    }

    @Override
    public void setSaturationLevel(float saturation) {
        this.saturation = saturation;
    }

    @Override
    public float getExhaustionLevel() {
        return exhaustion;
    }

    @Override
    public void setExhaustionLevel(float exhaustion) {
        this.exhaustion = exhaustion;
    }

    @Override
    public void addExhaustion(float exhaustion) {
        if (!player.getAbilities().invulnerable && !player.level.isClientSide) {
            this.exhaustion = Math.min(40.0F, this.exhaustion + exhaustion);
            sendClientData();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("hydration", this.hydration);
        tag.putFloat("saturation", this.saturation);
        tag.putFloat("exhaustion", this.exhaustion);
        tag.putInt("timer", this.tickTimer);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.hydration = nbt.getInt("hydration");
        this.saturation = nbt.getFloat("saturation");
        this.exhaustion = nbt.getFloat("exhaustion");
        this.tickTimer = nbt.getInt("timer");
    }

    @Override
    public void sendClientData() {
        if (!player.level.isClientSide) {
            NetworkManager.dispatchClientPacket((ServerPlayer) player, new S2C_SendThirstData(this.serializeNBT()));
        }
    }
}
