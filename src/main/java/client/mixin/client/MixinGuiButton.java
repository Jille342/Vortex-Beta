/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package client.mixin.client;

import client.utils.font.CFontRenderer;
import client.utils.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

import static net.minecraft.client.renderer.GlStateManager.resetColor;

@Mixin(GuiButton.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiButton extends Gui {
    private final CFontRenderer font = Fonts.default18;
    @Shadow
    public boolean visible;

    @Shadow
    public int xPosition;

    @Shadow
    public int yPosition;

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    protected boolean hovered;

    @Shadow
    public boolean enabled;

    @Shadow
    protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

    @Shadow
    public String displayString;

    @Shadow
    @Final
    protected static ResourceLocation buttonTextures;
    private float cut;
    private float alpha;

    /**
     * @author CCBlueX
     */
    @Overwrite
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            hovered = (mouseX >= xPosition && mouseY >= yPosition &&
                    mouseX < xPosition + width && mouseY < yPosition + height);
            final float deltaTime =0;

            if (enabled && hovered) {
                cut += 0.05F ;

                if (cut >= 4) cut = 4;

                alpha += 0.3F ;

                if (alpha >= 210) alpha = 210;
            } else {
                cut -= 0.05F ;

                if (cut <= 0) cut = 0;

                alpha -= 0.3F ;

                if (alpha <= 120) alpha = 120;
            }

            Gui.drawRect(xPosition + (int) cut, yPosition,
                    xPosition + width - (int) cut, yPosition + height,
                    enabled ? new Color(0F, 0F, 0F, alpha / 255F).getRGB() :
                            new Color(0.5F, 0.5F, 0.5F, 0.5F).getRGB());

            mc.getTextureManager().bindTexture(buttonTextures);
            mouseDragged(mc, mouseX, mouseY);


            font.drawStringWithShadow(displayString, (float) ((xPosition + width / 2) - font.getStringWidth(displayString) / 2),
                    yPosition + (height - 5) / 2F, 14737632);

            resetColor();
        }
    }
}