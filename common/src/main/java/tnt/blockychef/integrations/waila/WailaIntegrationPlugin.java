package tnt.blockychef.integrations.waila;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.*;
import tnt.blockychef.common.block.entity.*;

@WailaPlugin
public class WailaIntegrationPlugin implements IWailaPlugin {

    static final ResourceLocation DRYING_RACK = BlockyChef.resource("drying_rack");
    static final ResourceLocation JUICER = BlockyChef.resource("juicer");
    static final ResourceLocation GRATER = BlockyChef.resource("grater");
    static final ResourceLocation MEAT_GRINDER = BlockyChef.resource("meat_grinder");
    static final ResourceLocation MIXER = BlockyChef.resource("mixer");
    static final ResourceLocation TEAPOT = BlockyChef.resource("teapot");
    static final ResourceLocation GROWABLES = BlockyChef.resource("growables");
    static final ResourceLocation DECAYABLES = BlockyChef.resource("decayables");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(DryingRackComponentProvider.INSTANCE, DryingRackBlockEntity.class);
        registration.registerBlockDataProvider(JuicerComponentProvider.INSTANCE, JuicerBlockEntity.class);
        registration.registerBlockDataProvider(MeatGrinderComponentProvider.INSTANCE, MeatGrinderBlockEntity.class);
        registration.registerBlockDataProvider(GraterComponentProvider.INSTANCE, GraterBlockEntity.class);
        registration.registerBlockDataProvider(MixerComponentProvider.INSTANCE, MixerBlockEntity.class);
        registration.registerBlockDataProvider(TeapotComponentProvider.INSTANCE, TeapotBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(DryingRackComponentProvider.INSTANCE, DryingRackBlock.class);
        registration.registerBlockComponent(JuicerComponentProvider.INSTANCE, JuicerBlock.class);
        registration.registerBlockComponent(MeatGrinderComponentProvider.INSTANCE, MeatGrinderBlock.class);
        registration.registerBlockComponent(GraterComponentProvider.INSTANCE, GraterBlock.class);
        registration.registerBlockComponent(MixerComponentProvider.INSTANCE, MixerBlock.class);
        registration.registerBlockComponent(TeapotComponentProvider.INSTANCE, TeapotBlock.class);
        registration.registerBlockComponent(CustomGrowthProgressProvider.INSTANCE, TreeHangingFruitBlock.class);
        registration.registerBlockComponent(CustomGrowthProgressProvider.INSTANCE, RegrowingLogBlock.class);
        registration.registerBlockComponent(DecayComponentProvider.INSTANCE, Block.class);
    }
}