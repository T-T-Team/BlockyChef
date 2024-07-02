package tnt.blockychef.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import java.util.function.Supplier;

public class FillableBottleItem extends Item {

    private BottleFiller<LivingEntity> fromEntityFiller = BottleFiller.empty();
    private BottleFiller<FluidState> fromFluidFiller = BottleFiller.empty();

    public FillableBottleItem(Properties properties) {
        super(properties);
    }

    public FillableBottleItem fromEntity(BottleFiller<LivingEntity> fromEntityFiller) {
        this.fromEntityFiller = fromEntityFiller;
        return this;
    }

    public FillableBottleItem fromFluid(BottleFiller<FluidState> fromFluidFiller) {
        this.fromFluidFiller = fromFluidFiller;
        return this;
    }

    public FillableBottleItem fillMilkFromEntity(Supplier<ItemStack> provider) {
        return this.fromEntity((itemStack, entity, player, src) -> {
            if (entity.getType() == EntityType.COW) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                return provider.get();
            }
            return ItemStack.EMPTY;
        });
    }

    public FillableBottleItem fillWaterFromSource(Supplier<ItemStack> provider) {
        return this.fromFluid((itemStack, target, player, src) -> {
            if (target.is(FluidTags.WATER)) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                player.level().gameEvent(player, GameEvent.FLUID_PICKUP, src);
                return provider.get();
            }
            return ItemStack.EMPTY;
        });
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pPlayer.level().isClientSide())
            return InteractionResult.PASS;
        ItemStack filledResult = this.fromEntityFiller.doFill(pStack, pInteractionTarget, pPlayer, pInteractionTarget.blockPosition());
        if (!filledResult.isEmpty()) {
            if (!pPlayer.isCreative())
                pStack.shrink(1);
            MenuInventoryHelper.giveItemOrDrop(pPlayer, filledResult);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = blockhitresult.getBlockPos();
            if (!level.mayInteract(player, pos)) {
                return InteractionResultHolder.pass(itemStack);
            }
            FluidState state = level.getFluidState(pos);
            ItemStack result = this.fromFluidFiller.doFill(itemStack, state, player, pos);
            return !result.isEmpty() ? InteractionResultHolder.sidedSuccess(result, level.isClientSide()) : InteractionResultHolder.pass(itemStack);
        }
        return super.use(level, player, hand);
    }

    @FunctionalInterface
    public interface BottleFiller<T> {

        ItemStack doFill(ItemStack itemStack, T target, Player player, BlockPos origin);

        static <R> BottleFiller<R> empty() {
            return (itemStack, target, player, src) -> ItemStack.EMPTY;
        }
    }
}
