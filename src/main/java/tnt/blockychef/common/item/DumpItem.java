package tnt.blockychef.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import java.util.function.Supplier;

public class DumpItem extends Item {

    private Supplier<Item> returnItem;

    public DumpItem(Properties properties) {
        super(properties);
    }

    public DumpItem returns(Supplier<Item> itemProvider) {
        this.returnItem = itemProvider;
        return this;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 2;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.NONE;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        stack.shrink(1);
        if (returnItem != null && entity instanceof Player player) {
            MenuInventoryHelper.giveItemOrDrop(player, new ItemStack(returnItem.get()));
        }
        return stack;
    }
}
