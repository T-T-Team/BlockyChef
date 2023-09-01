package tnt.blockychef.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.FoliageColor;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.client.render.block.DryingRackBlockEntityRenderer;
import tnt.blockychef.client.render.block.GraterBlockEntityRenderer;
import tnt.blockychef.client.render.block.JuicerBlockEntityRenderer;
import tnt.blockychef.client.render.block.MixerBlockEntityRenderer;
import tnt.blockychef.client.render.thirst.ThirstOverlay;
import tnt.blockychef.client.render.thirst.ThirstTooltipHandler;
import tnt.blockychef.client.screen.*;
import tnt.blockychef.common.block.DyeableBlock;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.food.mastery.FoodQuality;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefItems;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.blockychef.integrations.Integrations;
import tnt.tntlib.api.module.configuration.Configuration;
import tnt.tntlib.api.module.configuration.config.format.ConfigFormats;

public final class BlockyChefClient {

    public static final BlockyChefClient CLIENT = new BlockyChefClient();

    public BlockyChefClientConfig config;

    private KeyMapping masteryKey;

    public void constructClient() {
        config = Configuration.registerConfig(BlockyChefClientConfig.class, ConfigFormats.yaml()).getConfigInstance();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(this::setup);
        modBus.addListener(this::registerGuiOverlays);
        modBus.addListener(this::registerBlockEntityRenderers);
        modBus.addListener(this::registerBlockColors);
        modBus.addListener(this::registerItemColors);
        modBus.addListener(this::registerKeybinds);
        if (Integrations.shouldExpandFoodTooltips()) {
            modBus.addListener(ThirstTooltipHandler::registerTooltipFactory);
            forgeEventBus.addListener(EventPriority.LOWEST, ThirstTooltipHandler::gatherTooltipComponents);
        }
        forgeEventBus.addListener(this::tickClient);
        forgeEventBus.addListener(this::adjustTooltip);
        forgeEventBus.addListener(this::handleKeyPress);
    }

    private void setup(FMLClientSetupEvent event) {
        Integrations.accept(layer -> layer.setup(event));
        event.enqueueWork(this::registerScreenFactories);
    }

    private void registerKeybinds(RegisterKeyMappingsEvent event) {
        String category = BlockyChef.MODID;
        masteryKey = new KeyMapping("key.blockychef.masteries", GLFW.GLFW_KEY_O, category);
        event.register(masteryKey);
    }

    private void handleKeyPress(InputEvent.Key event) {
        if (masteryKey != null && masteryKey.consumeClick()) {
            Minecraft.getInstance().setScreen(new MasteryScreen());
        }
    }

    private void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(new ResourceLocation("minecraft:food_level"), "thirst", new ThirstOverlay());
    }

    private void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockyChefBlockEntities.DRYING_RACK, DryingRackBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockyChefBlockEntities.GRATER, GraterBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockyChefBlockEntities.JUICER, JuicerBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockyChefBlockEntities.MIXER, MixerBlockEntityRenderer::new);
    }

    private void registerScreenFactories() {
        MenuScreens.register(BlockyChefMenuTypes.CUTTING_BOARD, CuttingBoardScreen::new);
        MenuScreens.register(BlockyChefMenuTypes.TOASTER, ToasterScreen::new);
        MenuScreens.register(BlockyChefMenuTypes.MORTAR_AND_PESTLE, MortarAndPestleScreen::new);
        MenuScreens.register(BlockyChefMenuTypes.MIXING_BOWL, MixingBowlScreen::new);
        MenuScreens.register(BlockyChefMenuTypes.DOUGH_MAKER, DoughMakerScreen::new);
        MenuScreens.register(BlockyChefMenuTypes.PASTA_MACHINE, PastaMachineScreen::new);
        MenuScreens.register(BlockyChefMenuTypes.BARREL, BarrelScreen::new);
        MenuScreens.register(BlockyChefMenuTypes.MIXER, MixerScreen::new);
        MenuScreens.register(BlockyChefMenuTypes.STOVE, StoveScreen::new);
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

    private void adjustTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        FoodQuality quality = CookingMastery.getItemQuality(stack);
        if (quality != null) {
            event.getToolTip().add(Component.translatable("tooltip.blockychef.quality", quality.getLabel()));
        }
    }

    private void tickClient(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        ThirstOverlay.tick();
    }
}
