package tnt.blockychef.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tnt.blockychef.common.Registry;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;

public class ThirstOverlay implements IGuiOverlay {

    public static final ResourceLocation TEXTURE = new ResourceLocation("blockychef:textures/icon/hydration_level.png");

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

        player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
            int level = stats.getHydrationLevel();
            for (int i = 0; i < 10; ++i) {
                int idx = i * 2 + 1;
                int x = left - i * 8 - 9;
                int y = top;
                int icon = 16;
                byte background = 0;

                if (player.hasEffect(Registry.THIRST)) {
                    icon += 36;
                    background = 13;
                }

                if (stats.getSaturationLevel() <= 0.0F && gui.getGuiTicks() % (level * 3 + 1) == 0) {
                    y = top + (player.getRandom().nextInt(3) - 1);
                }

                gui.blit(poseStack, x, y, 16 + background * 9, 27, 9, 9);

                if (idx < level)
                    gui.blit(poseStack, x, y, icon + 36, 27, 9, 9);
                else if (idx == level)
                    gui.blit(poseStack, x, y, icon + 45, 27, 9, 9);
            }
        });
        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }
}
