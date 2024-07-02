package tnt.blockychef.mixin;

import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tnt.blockychef.common.food.FoodList;

import javax.annotation.Nullable;

@Mixin(Item.class)
public abstract class ItemMixin implements FeatureElement, ItemLike {

    @Shadow
    @Final
    @Mutable
    @Nullable
    private FoodProperties foodProperties;

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/item/Item;foodProperties:Lnet/minecraft/world/food/FoodProperties;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void blockychef$changeFoodStats(Item item, FoodProperties properties) {
        FoodProperties override = FoodList.FOOD_OVERRIDES.get(properties);
        foodProperties = override != null ? override : properties;
    }
}
