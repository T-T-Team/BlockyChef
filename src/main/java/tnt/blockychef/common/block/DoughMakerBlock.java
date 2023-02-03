package tnt.blockychef.common.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class DoughMakerBlock extends FullHorizontalAxisBlock {

    public DoughMakerBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(3.0F).noOcclusion());
    }
}
