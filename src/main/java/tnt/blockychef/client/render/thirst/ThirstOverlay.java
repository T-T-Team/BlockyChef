package tnt.blockychef.client.render.thirst;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import tnt.blockychef.common.Registry;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;
import tnt.blockychef.integrations.Integrations;
import tnt.blockychef.util.RenderHelper;

public class ThirstOverlay implements IGuiOverlay {

    public static final ResourceLocation TEXTURE = new ResourceLocation("blockychef:textures/icon/hydration_icons.png");
    private final RandomSource random = RandomSource.create();
    private final OverlayRenderer renderer;

    public ThirstOverlay() {
        this.renderer = Integrations.shouldRenderFancyOverlay() ? this::renderFancy : this::renderDefault;
    }

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = gui.getMinecraft();
        minecraft.getProfiler().push("blockychef:thirst");
        Player player = (Player) minecraft.getCameraEntity();

        boolean isMounted = player.getVehicle() instanceof LivingEntity;
        if (!(!isMounted && !gui.getMinecraft().options.hideGui && gui.shouldDrawSurvivalElements())) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderer.renderOverlay(player, gui, poseStack, partialTick, screenWidth, screenHeight);
        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    private void renderFancy(Player player, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        int left = screenWidth / 2 + 91;
        int top = screenHeight - gui.rightHeight;
        gui.rightHeight += 10;
        boolean isThirsty = player.hasEffect(Registry.THIRST);
        player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
            int hydration = stats.getHydrationLevel();
            float saturation = stats.getSaturationLevel();
            this.renderExhaustion(poseStack, stats.getExhaustionLevel(), left, top, gui.getBlitOffset());
            this.renderHydrationOverlay(poseStack, gui, hydration, saturation, isThirsty, left, top, 1.0F);
            // TODO render saturation overlay
            // TODO render held item overlay
        });
    }

    private void renderDefault(Player player, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        int left = screenWidth / 2 + 91;
        int top = screenHeight - gui.rightHeight;
        gui.rightHeight += 10;
        boolean thirsty = player.hasEffect(Registry.THIRST);
        player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
            int hydration = stats.getHydrationLevel();
            float saturation = stats.getSaturationLevel();
            this.renderHydrationOverlay(poseStack, gui, hydration, saturation, thirsty, left, top, 1.0F);
        });
    }

    private void renderHydrationOverlay(PoseStack stack, ForgeGui gui, int hydrationLevel, float saturationLevel, boolean thirsty, int left, int top, float alpha) {
        this.random.setSeed(gui.getGuiTicks() * 312871L);
        for (int i = 0; i < 10; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;

            int offset = 0;
            if (thirsty) {
                offset = 6;
            }

            if (saturationLevel <= 0.0F && gui.getGuiTicks() % (hydrationLevel * 3 + 1) == 0) {
                y = top + (random.nextInt(3) - 1);
            }

            Matrix4f pose = stack.last().pose();
            float txSize = 256.0F;
            int bg1 = offset;
            int bg2 = bg1 + 1;
            float v2 = 9.0F / txSize;
            int iconIndex;
            RenderHelper.texturedBlit(pose, x, y, x + 9, y + 9, gui.getBlitOffset(), (bg1 * 9) / txSize, 0.0F, (bg2 * 9) / txSize, v2);
            if (idx < hydrationLevel) {
                iconIndex = 1;
            } else if (idx == hydrationLevel) {
                iconIndex = 2;
            } else {
                iconIndex = -1;
            }
            if (iconIndex >= 0) {
                int icon = offset + iconIndex;
                RenderHelper.texturedBlit(pose, x, y, x + 9, y + 9, gui.getBlitOffset(), (icon * 9) / txSize, 0.0F, ((icon + 1) * 9) / txSize, v2);
            }
        }
    }

    private void renderExhaustion(PoseStack stack, float exhaustion, int left, int top, int z) {
        float value = exhaustion / 4.0F;
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.75F);
        Matrix4f pose = stack.last().pose();
        int xSize = (int) (value * 81);
        RenderHelper.texturedBlit(pose, left - xSize, top, left, top + 9, z, (81.0F - xSize) / 256.0F, 18.0F / 256.0F, 81.0F / 256F, 27.0F / 256.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @FunctionalInterface
    private interface OverlayRenderer {
        void renderOverlay(Player player, ForgeGui gui, PoseStack stack, float partialTick, int screenWidth, int screenHeight);
    }
}
