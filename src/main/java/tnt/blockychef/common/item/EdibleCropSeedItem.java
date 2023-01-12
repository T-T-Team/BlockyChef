package tnt.blockychef.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import tnt.blockychef.common.thirst.DrinkProperties;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;

public class EdibleCropSeedItem extends CropSeedsItem implements Drinkable {

    private final DrinkProperties stats;

    public EdibleCropSeedItem(Block block, Properties properties) {
        this(block, properties, DrinkProperties.NONE);
    }

    public EdibleCropSeedItem(Block block, Properties properties, DrinkProperties stats) {
        super(block, properties);
        this.stats = stats;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult interactionResult = result.getResult();
        if (interactionResult == InteractionResult.FAIL) { // when player cannot eat
            DrinkProperties drinkProps = this.getStats();
            if (!drinkProps.isEmpty()) {
                DrinkProperties properties = DrinkProperties.adjustStats(drinkProps, stack);
                return player.getCapability(PlayerThirstStatsProvider.CAPABILITY)
                        .map(stats -> {
                            if (stats.canDrink(properties)) {
                                player.startUsingItem(hand);
                                return InteractionResultHolder.consume(stack);
                            }
                            return InteractionResultHolder.fail(stack);
                        })
                        .orElse(InteractionResultHolder.pass(stack));
            }
        }
        return result;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        DrinkProperties properties = this.getStats();
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!properties.isEmpty()) {
            DrinkProperties adjusted = DrinkProperties.adjustStats(properties, stack);
            if (entity instanceof Player player) {
                if (!this.isEdible() && !player.getAbilities().instabuild) {
                    result.shrink(1);
                }
                player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
                    stats.drink(adjusted);
                    stats.sendClientData();
                });
            }
        }
        return result;
    }

    @Override
    public DrinkProperties getStats() {
        return stats;
    }
}
