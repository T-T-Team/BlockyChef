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

import java.util.function.Function;
import java.util.function.Supplier;

public class ConsumableItem extends MasteryApplicableItem {

    private final Function<ItemStack, UseAnim> anim;
    private Supplier<ItemStack> returnItemProvider;

    public ConsumableItem(Properties properties) {
        this(properties, stack -> UseAnim.EAT);
    }

    public ConsumableItem(Properties pProperties, Function<ItemStack, UseAnim> anim) {
        super(pProperties);
        this.anim = anim;
    }

    public ConsumableItem returnsItemStack(Supplier<ItemStack> itemProvider) {
        this.returnItemProvider = itemProvider;
        return this;
    }

    public ConsumableItem returns(Supplier<Item> itemProvider) {
        return returnsItemStack(() -> new ItemStack(itemProvider.get()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return anim.apply(stack);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return returnItemProvider != null;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return hasCraftingRemainingItem(itemStack) ? returnItemProvider.get() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        boolean consumed = false;
        if (isEdible()) {
            entity.eat(level, stack);
            consumed = true;
        }
        if (!consumed) {
            if (entity instanceof Player player) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            } else {
                stack.shrink(1);
            }
        }
        if (returnItemProvider != null && entity instanceof Player player) {
            MenuInventoryHelper.giveItemOrDrop(player, returnItemProvider.get());
        }
        return stack;
    }

    protected void consumed(ItemStack stack, Level level, LivingEntity entity) {

    }
}
