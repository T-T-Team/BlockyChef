package tnt.blockychef.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tnt.blockychef.common.ai.ManageFarm;

@Mixin(VillagerGoalPackages.class)
public abstract class VillagerGoalPackagesMixin {

    /*
    static ImmutableList<?> list = ImmutableList.of(
            getMinimalLookBehavior(), Pair.of(5, new RunOne<>(
                    ImmutableList.of(
                            Pair.of(workatpoi, 7),
                            Pair.of(StrollAroundPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 4), 2),
                            Pair.of(StrollToPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
                            Pair.of(StrollToPoiList.create(MemoryModuleType.SECONDARY_JOB_SITE, p_24591_, 1, 6, MemoryModuleType.JOB_SITE), 5),
                            Pair.of(new HarvestFarmland(), p_24590_ == VillagerProfession.FARMER ? 2 : 5),
                            Pair.of(new UseBonemeal(), p_24590_ == VillagerProfession.FARMER ? 4 : 7)
                    )
            )),
            Pair.of(10, new ShowTradesToPlayer(400, 1600)),
            Pair.of(10, SetLookAndInteract.create(EntityType.PLAYER, 4)),
            Pair.of(2, SetWalkTargetFromBlockMemory.create(MemoryModuleType.JOB_SITE, p_24591_, 9, 100, 1200)),
            Pair.of(3, new GiveGiftToHero(100)),
            Pair.of(99, UpdateActivityFromSchedule.create()));
     */

    @ModifyArg(
            method = "getWorkPackage",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"
            ),
            index = 4
    )
    private static <E> E replace(E e1) {
        Pair<? extends BehaviorControl<? super Villager>, Integer> pair = (Pair<? extends BehaviorControl<? super Villager>, Integer>) e1;
        return (E) Pair.of(new ManageFarm(), pair.getSecond());
    }
}
