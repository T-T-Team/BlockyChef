package tnt.blockychef.common.block;

import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.material.Material;

public class TreeSaplingBlock extends SaplingBlock {

    public TreeSaplingBlock(AbstractTreeGrower treeGrower) {
        super(treeGrower, Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
    }
}
