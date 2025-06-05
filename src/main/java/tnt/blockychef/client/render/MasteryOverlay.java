package tnt.blockychef.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.mastery.CookingMastery;

public class MasteryOverlay implements IGuiOverlay {

    public static final ResourceLocation BADGES = BlockyChef.resource("textures/icon/mastery_badges.png");

    private static final int MAX_LIFETIME = 120;
    private static CookingMastery mastery;
    private static CookingMastery.Tier.Badge badgeFrom;
    private static CookingMastery.Tier.Badge badgeTo;
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

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fade0);
        graphics.blitNineSliced(BADGES, left, top, scale, scale, 2, 2, 2, 12, 32, 32, badgeFrom.getTexturePositionX(), 0);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fade1);
        graphics.blitNineSliced(BADGES, left, top, scale, scale, 2, 2, 2, 12, 32, 32, badgeTo.getTexturePositionX(), 0);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // render mastery
        ItemStack itemStack = mastery.item().getDefaultInstance();
        graphics.renderItem(itemStack, left + 8, top + 2);
    }

    public static void receiveMasteryUpdate(CookingMastery cookingMastery, int prevCount, int count) {
        mastery = cookingMastery;
        badgeFrom = cookingMastery.getTier(prevCount).badge();
        badgeTo = cookingMastery.getTier(count).badge();
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
