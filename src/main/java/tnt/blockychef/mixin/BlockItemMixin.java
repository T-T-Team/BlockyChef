package tnt.blockychef.mixin;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.Registry;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {

    private static final Map<Block, Supplier<Block>> REPLACEMENTS = new IdentityHashMap<>();

    public BlockItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Inject(method = "placeBlock", at = @At("HEAD"), cancellable = true)
    private void blockychef$replaceCrops(BlockPlaceContext context, BlockState state, CallbackInfoReturnable<Boolean> ci) {
        if (BlockyChef.config.weeds.replaceVanillaCrops) {
            Supplier<Block> replacement = REPLACEMENTS.get(state.getBlock());
            if (replacement != null) {
                ci.setReturnValue(context.getLevel().setBlock(context.getClickedPos(), replacement.get().defaultBlockState(), 11));
            }
        }
    }

    static {
        REPLACEMENTS.put(Blocks.POTATOES, () -> Registry.POTATO_CROPS);
        REPLACEMENTS.put(Blocks.CARROTS, () -> Registry.CARROT_CROPS);
        REPLACEMENTS.put(Blocks.BEETROOTS, () -> Registry.BEETROOT_CROPS);
        REPLACEMENTS.put(Blocks.WHEAT, () -> Registry.WHEAT_CROPS);
    }
}
