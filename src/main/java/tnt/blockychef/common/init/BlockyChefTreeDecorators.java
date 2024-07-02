package tnt.blockychef.common.init;

import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.registries.ObjectHolder;
import tnt.blockychef.levelgen.tree.TreeFruitDecorator;

public final class BlockyChefTreeDecorators {

    private static final String KEY = "worldgen/tree_decorator_type";

    @ObjectHolder(value = "blockychef:fruit_decorator", registryName = KEY)
    public static final TreeDecoratorType<TreeFruitDecorator> FRUIT_DECORATOR = null;
}
