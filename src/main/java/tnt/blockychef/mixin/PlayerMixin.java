package tnt.blockychef.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    @Shadow(remap = false)
    public abstract <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing);

    public PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void blockychef$addThirstExhaustionFromSwimming(double x, double y, double z, CallbackInfo ci, double d, int i) {
        addExhaustion(0.01F * i * 0.01F);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void blockychef$addThirstExhaustionFromUnderwaterWalking(double x, double y, double z, CallbackInfo ci, double d, int j) {
        addExhaustion(0.01F * j * 0.01F);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void blockychef$addThirstExhaustionFromWaterWalking(double x, double y, double z, CallbackInfo ci, double d, int k) {
        addExhaustion(0.01F * k * 0.01F);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    private void blockychef$addThirstExhaustionFromSprinting(double x, double y, double z, CallbackInfo ci, double d, int l) {
        addExhaustion(0.01F * l * 0.01F);
    }

    private void addExhaustion(float exhaustion) {
        this.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> stats.addExhaustion(exhaustion));
    }
}
