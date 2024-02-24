package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefItems;
import tnt.blockychef.common.init.BlockyChefSounds;

import java.util.Optional;

public class KitchenSinkBlock extends DyeableBlock implements EntityBlock, FluidInteractBlock {

    public KitchenSinkBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(1.5F).noOcclusion());
    }

    @Override
    public int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Vec3 vec = hitResult.getLocation();
        double y = vec.y - pos.getY();
        return y >= 0.85 ? 0 : 1;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockyChefBlockEntities.KITCHEN_SINK.create(pos, state);
    }

    @Override
    public ItemStack getPickupItem(ItemStack interactionItem, Level level, BlockPos pos, BlockState state, Player player) {
        if (interactionItem.is(Items.GLASS_BOTTLE)) {
            return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
        } else if (interactionItem.is(BlockyChefItems.EMPTY_BOTTLE)) {
            return new ItemStack(BlockyChefItems.BOTTLE_OF_WATER);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        return new ItemStack(Items.WATER_BUCKET);
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    @Override
    public void onInteractionEvent(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide())
            return;
        RandomSource random = level.getRandom();
        double multiplier = 3.0;
        for (int i = 0; i < 30; i++) {
            double x = randomValue(random) * 0.4 + pos.getX() + 0.5;
            double z = randomValue(random) * 0.4 + pos.getZ() + 0.5;
            double xs = randomValue(random) * multiplier;
            double ys = 0.0;
            double zs = randomValue(random) * multiplier;
            level.addParticle(ParticleTypes.FALLING_WATER, x, pos.getY() + 1.0, z, xs, ys, zs);
        }
        level.playSound(null, pos, BlockyChefSounds.GRILL, SoundSource.AMBIENT);
    }

    private static double randomValue(RandomSource random) {
        return random.nextDouble() - random.nextDouble();
    }
}
