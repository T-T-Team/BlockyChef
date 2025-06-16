package tnt.blockychef.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tnt.blockychef.common.ai.ManageFarm;

@Mixin(VillagerGoalPackages.class)
public abstract class VillagerGoalPackagesMixin {

    @ModifyArg(
            method = "getWorkPackage",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;",
                    remap = false
            ),
            index = 4
    )
    @SuppressWarnings("unchecked")
    private static <E> E blockychef$replaceFarmingTask(E e1) {
        Pair<? extends BehaviorControl<? super Villager>, Integer> pair = (Pair<? extends BehaviorControl<? super Villager>, Integer>) e1;
        return (E) Pair.of(new ManageFarm(), pair.getSecond());
    }
}
