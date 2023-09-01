package tnt.blockychef.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.food.mastery.CookingMastery;

public class MasteryApplicableItem extends Item {

    public MasteryApplicableItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        FoodProperties properties = super.getFoodProperties(stack, entity);
        return CookingMastery.getAdjustedFoodProperties(properties, stack);
    }
}
