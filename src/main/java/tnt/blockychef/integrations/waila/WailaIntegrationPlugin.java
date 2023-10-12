/*package tnt.blockychef.integrations.waila;

import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.BarrelBlock;
import tnt.blockychef.common.block.DryingRackBlock;
import tnt.blockychef.common.block.JuicerBlock;
import tnt.blockychef.common.block.entity.BarrelBlockEntity;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;
import tnt.blockychef.common.block.entity.JuicerBlockEntity;

@WailaPlugin
public class WailaIntegrationPlugin implements IWailaPlugin {

    static final ResourceLocation DRYING_RACK = BlockyChef.resource("drying_rack");
    static final ResourceLocation JUICER = BlockyChef.resource("juicer");
    static final ResourceLocation BARREL = BlockyChef.resource("barrel");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(DryingRackComponentProvider.INSTANCE, DryingRackBlockEntity.class);
        registration.registerBlockDataProvider(JuicerComponentProvider.INSTANCE, JuicerBlockEntity.class);
        registration.registerBlockDataProvider(BarrelComponentProvider.INSTANCE, BarrelBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(DryingRackComponentProvider.INSTANCE, DryingRackBlock.class);
        registration.registerBlockComponent(JuicerComponentProvider.INSTANCE, JuicerBlock.class);
        registration.registerBlockComponent(BarrelComponentProvider.INSTANCE, BarrelBlock.class);
    }
}
*/