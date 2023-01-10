package tnt.blockychef.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.joml.Matrix4f;
import tnt.blockychef.common.Registry;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;
import tnt.blockychef.util.RenderHelper;

public class ThirstOverlay implements IGuiOverlay {

    public static final ResourceLocation TEXTURE = new ResourceLocation("blockychef:textures/icon/hydration_level.png");
    private final RandomSource random = RandomSource.create();

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
        int left = screenWidth / 2 + 91;
        int top = screenHeight - gui.rightHeight;
        gui.rightHeight += 10;
        this.random.setSeed(gui.getGuiTicks() * 312871L);

        player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
            int level = stats.getHydrationLevel();
            for (int i = 0; i < 10; ++i) {
                int idx = i * 2 + 1;
                int x = left - i * 8 - 9;
                int y = top;

                int offset = 0;
                if (player.hasEffect(Registry.THIRST)) {
                    offset = 3;
                }

                if (stats.getSaturationLevel() <= 0.0F && gui.getGuiTicks() % (level * 3 + 1) == 0) {
                    y = top + (random.nextInt(3) - 1);
                }

                Matrix4f pose = poseStack.last().pose();
                int icon;
                if (idx < level) {
                    icon = 0;
                } else if (idx == level) {
                    icon = 1;
                } else {
                    icon = 2;
                }
                int texIndex1 = offset + icon;
                int texIndex2 = texIndex1 + 1;
                RenderHelper.texturedBlit(pose, x, y, x + 9, y + 9, gui.getBlitOffset(), (texIndex1 * 9) / 54.0F, 0.0F, (texIndex2 * 9) / 54.0F, 1.0F);
            }
        });
        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }
}
