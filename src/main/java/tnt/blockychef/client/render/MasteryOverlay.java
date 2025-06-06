package tnt.blockychef.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.mastery.CookingMastery;

public class MasteryOverlay implements IGuiOverlay {

    public static final ResourceLocation BADGES = BlockyChef.resource("textures/icon/mastery_badges.png");

    private static final int MAX_LIFETIME = 160;
    private static CookingMastery mastery;
    private static CookingMastery.Tier tierFrom;
    private static CookingMastery.Tier tierTo;
    private static int lifetime;
    private static int lastLifetime;

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        if (mastery == null)
            return;

        Minecraft client = Minecraft.getInstance();
        if (client.screen != null || client.options.hideGui) {
            return;
        }

        int scale = 32;
        int left = (screenWidth - scale) / 2;
        int top = (screenHeight - scale) / 2 + 30;
        int limit = MAX_LIFETIME - 40;
        float fade0 = Math.min(1.0F, Mth.lerp(partialTick, lastLifetime / (float) limit, lifetime / (float) limit));
        float fade1 = 1.0F - fade0;

        // animation variables
        int oldBadgeDisplayTime = 60;
        int newBadgeDisplayDelay = 30;
        int newBadgeAnimationTime = 70 + newBadgeDisplayDelay;
        float smoothLifetime = Mth.lerp(partialTick, lastLifetime, lifetime);

        // old badge render
        float oldLifetime = smoothLifetime < MAX_LIFETIME - oldBadgeDisplayTime ? 0 : smoothLifetime - (MAX_LIFETIME - oldBadgeDisplayTime);
        float oldFade = oldLifetime / oldBadgeDisplayTime;
        if (oldFade > 0.0F) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, oldFade);
            graphics.blitNineSliced(BADGES, left, top, scale, scale, 2, 2, 2, 12, 32, 32, tierFrom.badge().getTexturePositionX(), 0);
        }

        // new badge replacement
        float fadeStart = MAX_LIFETIME - newBadgeDisplayDelay;
        float fadeEnd = MAX_LIFETIME - newBadgeAnimationTime;
        float t = (smoothLifetime - fadeEnd) / (fadeStart - fadeEnd);
        float newFade = Math.max(0.0F, Math.min(1.0F, 1.0F - t));
        CookingMastery.Tier.Badge badge = tierTo.badge();
        if (newFade > 0.0F) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, newFade);
            graphics.blitNineSliced(BADGES, left, top, scale, scale, 2, 2, 2, 12, 32, 32, badge.getTexturePositionX(), 0);
        }

        // reset shader
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // render mastery item
        ItemStack itemStack = mastery.item().getDefaultInstance();
        graphics.renderItem(itemStack, left + 8, top + 2);

        // render label
        if (newFade > 0.12F) {
            Component label = Component.translatable("label.blockychef.mastery_level", itemStack.getHoverName(), badge.getLabel());
            int alpha = ((int) (newFade * 255.0F) & 0xFF) << 24;
            graphics.drawString(client.font, label, left + (scale - client.font.width(label)) / 2, top + scale + 4, tierTo.color() | alpha, true);
        }

    }

    public static void receiveMasteryUpdate(CookingMastery cookingMastery, int prevCount, int count) {
        mastery = cookingMastery;
        tierFrom = cookingMastery.getTier(prevCount);
        tierTo = cookingMastery.getTier(count);
        lifetime = MAX_LIFETIME;
        lastLifetime = lifetime;
    }

    public static void tick() {
        if (mastery != null) {
            lastLifetime = lifetime;
            Minecraft client = Minecraft.getInstance();
            if (client.screen == null && --lifetime < 0) {
                mastery = null;
            }
        }
    }
}
