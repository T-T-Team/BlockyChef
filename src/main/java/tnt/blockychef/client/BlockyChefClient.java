package tnt.blockychef.client;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tnt.blockychef.client.render.block.DryingRackBlockEntityRenderer;
import tnt.blockychef.client.render.block.GraterBlockEntityRenderer;
import tnt.blockychef.client.render.thirst.ThirstOverlay;
import tnt.blockychef.client.render.thirst.ThirstTooltipHandler;
import tnt.blockychef.common.block.DyeableBlock;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefItems;
import tnt.blockychef.integrations.Integrations;

public final class BlockyChefClient {

    public static final BlockyChefClient CLIENT = new BlockyChefClient();

    public BlockyChefClientConfig config;

    public void constructClient() {
        config = Configuration.registerConfig(BlockyChefClientConfig.class, ConfigFormats.yaml()).getConfigInstance();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(this::setup);
        modBus.addListener(this::registerGuiOverlays);
        modBus.addListener(this::registerBlockEntityRenderers);
        modBus.addListener(this::registerBlockColors);
        modBus.addListener(this::registerItemColors);
        if (Integrations.shouldExpandFoodTooltips()) {
            modBus.addListener(ThirstTooltipHandler::registerTooltipFactory);
            forgeEventBus.addListener(EventPriority.LOWEST, ThirstTooltipHandler::gatherTooltipComponents);
        }
        forgeEventBus.addListener(this::tickClient);
    }

    private void setup(FMLClientSetupEvent event) {
        Integrations.accept(layer -> layer.setup(event));
    }

    private void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(new ResourceLocation("minecraft:food_level"), "thirst", new ThirstOverlay());
    }

    private void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockyChefBlockEntities.DRYING_RACK, DryingRackBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockyChefBlockEntities.GRATER, GraterBlockEntityRenderer::new);
    }

    private void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(DyeableBlock::getColor,
                BlockyChefBlocks.CC_KITCHEN_COUNTER, BlockyChefBlocks.CP_KITCHEN_COUNTER, BlockyChefBlocks.PC_KITCHEN_COUNTER, BlockyChefBlocks.PP_KITCHEN_COUNTER,
                BlockyChefBlocks.CC_KITCHEN_SINK, BlockyChefBlocks.CP_KITCHEN_SINK, BlockyChefBlocks.PC_KITCHEN_SINK, BlockyChefBlocks.PP_KITCHEN_SINK,
                BlockyChefBlocks.C_KITCHEN_CABINET, BlockyChefBlocks.P_KITCHEN_CABINET,
                BlockyChefBlocks.CC_COOKING_TABLE, BlockyChefBlocks.CP_COOKING_TABLE, BlockyChefBlocks.PC_COOKING_TABLE, BlockyChefBlocks.PP_COOKING_TABLE,
                BlockyChefBlocks.CC_KITCHEN_COUNTER_CORNER, BlockyChefBlocks.CP_KITCHEN_COUNTER_CORNER, BlockyChefBlocks.PC_KITCHEN_COUNTER_CORNER, BlockyChefBlocks.PP_KITCHEN_COUNTER_CORNER,
                BlockyChefBlocks.STOVE,
                BlockyChefBlocks.DOUGH_MAKER,
                BlockyChefBlocks.MIXER,
                BlockyChefBlocks.TOASTER,
                BlockyChefBlocks.JUICER,
                BlockyChefBlocks.GRILL,
                BlockyChefBlocks.PASTA_MACHINE
        );
        event.register((state, tintGetter, pos, layer) -> tintGetter != null && pos != null ? BiomeColors.getAverageFoliageColor(tintGetter, pos) : FoliageColor.getDefaultColor(),
                BlockyChefBlocks.CINNAMON_LEAVES);
    }

    private void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((itemStack, layer) -> FoliageColor.getDefaultColor(),
                BlockyChefItems.CINNAMON_LEAVES);
    }

    private void tickClient(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        ThirstOverlay.tick();
    }
}
