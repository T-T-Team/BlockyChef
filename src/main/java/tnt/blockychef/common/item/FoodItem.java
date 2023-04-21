package tnt.blockychef.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tnt.blockychef.util.Helper;

public class FoodItem extends Item {

    private ItemStack returnItem = ItemStack.EMPTY;

    public FoodItem(Properties properties) {
        super(properties);
    }

    public FoodItem returns(Item item) {
        return returns(item, 1);
    }

    public FoodItem returns(Item item, int count) {
        return returns(new ItemStack(item, count));
    }

    public FoodItem returns(ItemStack itemStack) {
        this.returnItem = itemStack;
        return this;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack itemStack = super.finishUsingItem(stack, level, entity);
        if (!returnItem.isEmpty() && entity instanceof Player player) {
            Helper.giveItem(player, returnItem.copy());
        }
        return itemStack;
    }
}
