package tnt.blockychef.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tnt.blockychef.util.Helper;

import java.util.function.Supplier;

public class FoodItem extends Item {

    private Supplier<Item> returnItem;

    public FoodItem(Properties properties) {
        super(properties);
    }

    public FoodItem returns(Supplier<Item> itemProvider) {
        this.returnItem = itemProvider;
        return this;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack itemStack = super.finishUsingItem(stack, level, entity);
        if (returnItem != null && entity instanceof Player player) {
            Helper.giveItem(player, new ItemStack(returnItem.get()));
        }
        return itemStack;
    }
}
