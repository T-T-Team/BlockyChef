package tnt.blockychef.common.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class MixerBlock extends FullHorizontalAxisBlock {

    public MixerBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(3.0F).noOcclusion());
    }
}
