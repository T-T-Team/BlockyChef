package tnt.blockychef.levelgen.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import tnt.blockychef.common.init.BlockyChefTreeDecorators;

import java.util.List;

public class TreeFruitDecorator extends TreeDecorator {

    public static final Codec<TreeFruitDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(t -> t.fruitProbability),
            BlockStateProvider.CODEC.fieldOf("fruitProvider").forGetter(t -> t.fruitProvider)
    ).apply(instance, TreeFruitDecorator::new));
    private final float fruitProbability;
    private final BlockStateProvider fruitProvider;

    public TreeFruitDecorator(float fruitProbability, BlockStateProvider fruitProvider) {
        this.fruitProbability = fruitProbability;
        this.fruitProvider = fruitProvider;
    }

    @Override
    public void place(Context context) {
        List<BlockPos> leaves = context.leaves();
        RandomSource random = context.random();
        leaves.stream().filter(pos -> context.isAir(pos.below())).forEach(pos -> {
            float f = random.nextFloat();
            if (f < fruitProbability) {
                BlockState state = fruitProvider.getState(random, pos);
                context.setBlock(pos, state);
            }
        });
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return BlockyChefTreeDecorators.FRUIT_DECORATOR;
    }
}
