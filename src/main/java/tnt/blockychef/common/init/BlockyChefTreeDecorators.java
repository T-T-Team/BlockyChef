package tnt.blockychef.common.init;

import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.registries.ObjectHolder;
import tnt.blockychef.levelgen.tree.TreeFruitDecorator;

public final class BlockyChefTreeDecorators {

    @ObjectHolder(value = "blockychef:fruit_decorator", registryName = "worldgen/tree_decorator_type")
    public static final TreeDecoratorType<TreeFruitDecorator> FRUIT_DECORATOR = null;
}
