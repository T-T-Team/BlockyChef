package tnt.blockychef.mixin;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow public abstract Block getBlock();

    private static final Map<Block, Supplier<Block>> REPLACEMENTS = new IdentityHashMap<>();

    public BlockItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    private void blockyChef$getPlacementStateWithReplacements(BlockPlaceContext context, CallbackInfoReturnable<BlockState> ci) {
        if (BlockyChef.config.decay.replaceVanillaCrops) {
            Block block = ((BlockItem) (Object) this).getBlock();
            Supplier<Block> replacement = REPLACEMENTS.get(block);
            Level level = context.getLevel();
            if (replacement != null) {
                BlockState state = replacement.get().getStateForPlacement(context);
                if (state.canSurvive(level, context.getClickedPos())) {
                    ci.setReturnValue(state);
                }
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
